package git.jbredwards.smithing_table.mod.common.block;

import com.google.common.primitives.Floats;
import git.jbredwards.smithing_table.api.SmithingContent;
import git.jbredwards.smithing_table.api.SmithingRecipe;
import git.jbredwards.smithing_table.mod.SmithingTable;
import git.jbredwards.smithing_table.mod.SmithingTableCfg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public class TileSmithingTable extends TileEntity implements IWorldNameable
{
    public static final int TEMPLATE = 0, EQUIPMENT = 1, MATERIAL = 2, OUTPUT = 3;

    @Nonnull
    public final ItemStackHandler basicInventory = new ItemStackHandler(4) {
        @Override
        public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
            switch(slot) {
                case TEMPLATE:  return SmithingRecipe.partialMatch(stack, getStackInSlot(EQUIPMENT), getStackInSlot(MATERIAL));
                case EQUIPMENT: return SmithingRecipe.partialMatch(getStackInSlot(TEMPLATE), stack, getStackInSlot(MATERIAL));
                case MATERIAL:  return SmithingRecipe.partialMatch(getStackInSlot(TEMPLATE), getStackInSlot(EQUIPMENT), stack);
            }

            return false;
        }

        @Override
        protected void onContentsChanged(final int slot) {
            markDirty();
        }
    };

    @Nonnull
    public final SmithingRecipeProcessor processor = new SmithingRecipeProcessor();
    public final class SmithingRecipeProcessor implements IItemHandler
    {
        @Nullable
        private SmithingRecipe recipe;

        @Override
        public int getSlots() {
            return basicInventory.getSlots();
        }

        @Override
        public int getSlotLimit(final int slot) {
            return basicInventory.getSlotLimit(slot);
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(final int slot) {
            return slot == OUTPUT ? extractItem(OUTPUT, getSlotLimit(OUTPUT), true) : basicInventory.getStackInSlot(slot);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(final int slot, @Nonnull final ItemStack stack, final boolean simulate) {
            return isItemValid(slot, stack) ? basicInventory.insertItem(slot, stack, simulate) : stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
            if(amount <= 0 || slot > OUTPUT) return ItemStack.EMPTY;
            else if(slot < OUTPUT) return basicInventory.extractItem(slot, amount, simulate);
            // Find craft for automation.
            @Nonnull final ItemStack template = getStackInSlot(TEMPLATE), equipment = getStackInSlot(EQUIPMENT), material = getStackInSlot(MATERIAL);
            if(recipe != null && !SmithingRecipe.testResult(recipe, template, equipment, material)) recipe = null;
            if(recipe == null) recipe = SmithingRecipe.lookupResult(template, equipment, material);
            if(recipe == null) return basicInventory.extractItem(OUTPUT, amount, simulate);
            @Nonnull final ItemStack crafted = recipe.getCraftedResult(basicInventory);
            if(crafted.isEmpty()) return basicInventory.extractItem(OUTPUT, amount, simulate);
            // Account for extra items from previous crafts.
            @Nonnull final ItemStack extras = basicInventory.getStackInSlot(OUTPUT);
            final int skipped;
            if(extras.isEmpty()) skipped = 0;
            else if(extras.getCount() < amount && ItemHandlerHelper.canItemStacksStack(crafted, extras)) skipped = extras.getCount();
            else return basicInventory.extractItem(OUTPUT, amount, simulate);
            // Find number of items to craft.
            final int crafts = MathHelper.ceil(getSmithingOperations(recipe, amount - skipped));
            crafted.setCount(Math.min(crafts * recipe.getResult().getCount() + skipped, amount));
            // Consume ingredients.
            if(!simulate) {
                if(SmithingTableCfg.automationSound) playSound(crafted);
                if(!template.isEmpty()) {
                    if(template.getMaxDamage() == 0) template.shrink(crafts);
                    else if(template.attemptDamageItem(crafts, world.rand, null)) template.setCount(0);
                }
                equipment.shrink(crafts);
                material.shrink(crafts);
                // Store extras within internal output slot, and update comparator state.
                final int newExtras = Math.max(0, crafts * recipe.getResult().getCount() + skipped - amount);
                basicInventory.setStackInSlot(OUTPUT, ItemHandlerHelper.copyStackWithSize(crafted, newExtras));
            }

            return crafted;
        }

        @Override
        public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
            return basicInventory.isItemValid(slot, stack);
        }
    }

    public float getSmithingOperations(@Nonnull final SmithingRecipe recipe, final float maxOutputs) {
        @Nonnull final ItemStack template = basicInventory.getStackInSlot(TEMPLATE);
        return Floats.min(maxOutputs / recipe.getResult().getCount(),
                template.isEmpty() ? basicInventory.getSlotLimit(TEMPLATE) :
                template.getMaxDamage() == 0 ? template.getCount() : template.getMaxDamage(),
                basicInventory.getStackInSlot(EQUIPMENT).getCount(), basicInventory.getStackInSlot(MATERIAL).getCount());
    }

    public void playSound(@Nonnull final ItemStack result) {
        if(hasWorld() && !world.isRemote) world.playSound(null, pos,
                processor.recipe != null ? processor.recipe.getSound(result) : SmithingContent.BLOCK_SMITHING_TABLE_USE,
                SoundCategory.BLOCKS, 1, MathHelper.nextFloat(world.rand, 0.9f, 1));
    }

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return SmithingTableCfg.automation && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        if(!SmithingTableCfg.automation || capability != CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return super.getCapability(capability, facing);
        else return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new IItemHandler() {
            @Override
            public int getSlots() {
                return processor.getSlots();
            }

            @Nonnull
            @Override
            public ItemStack getStackInSlot(final int slot) {
                return processor.getStackInSlot(slot);
            }

            @Nonnull
            @Override
            public ItemStack insertItem(final int slot, @Nonnull final ItemStack stack, final boolean simulate) {
                return processor.insertItem(slot, stack, simulate);
            }

            @Nonnull
            @Override
            public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
                if(amount <= 0 || slot > OUTPUT || slot < OUTPUT && facing != null) return ItemStack.EMPTY;
                else return processor.extractItem(slot, amount, simulate);
            }

            @Override
            public int getSlotLimit(final int slot) {
                return processor.getSlotLimit(slot);
            }
        });
    }

    // -----------------------------
    // Custom Inventory Display Name
    // -----------------------------

    @Nonnull
    public static final String TRANSLATION_KEY = SmithingTable.MOD_ID + ".container.smithingTable";

    @Nonnull
    protected String customName = "";
    public void setCustomName(@Nonnull final String name) {
        customName = name;
        markDirty();
    }

    @Nonnull
    @Override
    public String getName() {
        return hasCustomName() ? customName : TRANSLATION_KEY;
    }

    @Override
    public boolean hasCustomName() {
        return !customName.isEmpty();
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName());
    }

    // -------
    // Variant
    // -------

    @Nonnull
    public TableData variant = TableData.DEFAULT;

    @Nonnull
    public static TableData getVariant(@Nonnull final IBlockAccess world, @Nonnull final BlockPos pos) {
        @Nullable final TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileSmithingTable ? ((TileSmithingTable)tile).variant : TableData.DEFAULT;
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        @Nonnull final NBTTagCompound tag = super.getUpdateTag();
        tag.setTag("Variant", variant.serializeNBT());
        return tag;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    // -------------
    // Serialization
    // -------------

    @Override
    public void readFromNBT(@Nonnull final NBTTagCompound compound) {
        if(compound.hasKey("CustomName", Constants.NBT.TAG_STRING)) customName = compound.getString("CustomName");
        if(compound.hasKey("Inventory", Constants.NBT.TAG_COMPOUND)) basicInventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        if(compound.hasKey("Variant", Constants.NBT.TAG_COMPOUND)) variant = TableData.deserialize(compound.getCompoundTag("Variant"));
        super.readFromNBT(compound);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull final NBTTagCompound compound) {
        compound.setString("CustomName", customName);
        compound.setTag("Inventory", basicInventory.serializeNBT());
        compound.setTag("Variant", variant.serializeNBT());
        return super.writeToNBT(compound);
    }
}
