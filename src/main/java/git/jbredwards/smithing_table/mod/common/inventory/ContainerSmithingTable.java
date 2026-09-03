package git.jbredwards.smithing_table.mod.common.inventory;

import git.jbredwards.smithing_table.mod.SmithingTable;
import git.jbredwards.smithing_table.mod.SmithingTableCfg;
import git.jbredwards.smithing_table.mod.common.block.TileSmithingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 *
 * @author jbred
 *
 */
public class ContainerSmithingTable extends Container implements IWorldNameable
{
    @Nonnull public static final ResourceLocation MATERIAL_OVERLAY = new ResourceLocation(SmithingTable.MOD_ID, "gui/smithing_overlay_material");
    @Nonnull public static final ResourceLocation TEMPLATE_OVERLAY = new ResourceLocation(SmithingTable.MOD_ID, "gui/smithing_overlay_template");

    @Nonnull protected final World world;
    @Nonnull protected final TileSmithingTable smithingTable;
    @Nonnull protected final SlotSmithingOutput recipeHandler;

    public ContainerSmithingTable(@Nonnull final EntityPlayer player, @Nonnull final World worldIn, final int x, final int y, final int z) {
        smithingTable = Objects.requireNonNull((TileSmithingTable)worldIn.getTileEntity(new BlockPos(x, y, z)));
        world = worldIn;
        // Container slots.
        final int xOffset = !SmithingTableCfg.armorStand && SmithingTableCfg.armorStandBackground ? 27 : 0;
        addSlotToContainer(new SlotSmithingInput(this, TileSmithingTable.TEMPLATE, 8 + xOffset, 48));
        addSlotToContainer(new SlotSmithingInput(this, TileSmithingTable.EQUIPMENT, 26 + xOffset, 48));
        addSlotToContainer(new SlotSmithingInput(this, TileSmithingTable.MATERIAL, 44 + xOffset, 48));
        addSlotToContainer(recipeHandler = new SlotSmithingOutput(this, TileSmithingTable.OUTPUT, 98 + xOffset, 48));
        if(SmithingTableCfg.materialOverlay) getSlot(TileSmithingTable.MATERIAL).setBackgroundName(MATERIAL_OVERLAY.toString());
        if(SmithingTableCfg.templateOverlay) getSlot(TileSmithingTable.TEMPLATE).setBackgroundName(TEMPLATE_OVERLAY.toString());
        // Player inventory slots.
        for(int i = 0; i < 3; ++i) for(int j = 0; j < 9; ++j) addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for(int k = 0; k < 9; ++k) addSlotToContainer(new Slot(player.inventory, k, 8 + k * 18, 142));
    }

    @Override
    public boolean canInteractWith(@Nonnull final EntityPlayer playerIn) {
        return playerIn.world == world && !smithingTable.isInvalid() && playerIn.getDistanceSqToCenter(smithingTable.getPos()) < 64;
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull final EntityPlayer playerIn, final int index) {
        @Nullable final Slot slot = inventorySlots.get(index);
        if(slot != null) {
            @Nonnull ItemStack slotStack = slot.getStack();
            @Nonnull final ItemStack slotStackOld = slotStack.copy();
            // From inventory to table.
            if(index > TileSmithingTable.OUTPUT) {
                if(mergeItemStack(slotStack, 0, TileSmithingTable.OUTPUT, false)) return ItemStack.EMPTY;
            }
            // From table to inventory.
            else {
                slotStack = slotStack.copy(); // Don't modify slot stack directly, for IItemHandler.
                final boolean merged = mergeItemStack(slotStack, TileSmithingTable.OUTPUT + 1, TileSmithingTable.OUTPUT + 37, true);
                slot.decrStackSize(slotStackOld.getCount() - slotStack.getCount());
                if(!merged) return ItemStack.EMPTY;
            }

            // Vanilla slot stuff.
            if(slotStack.isEmpty()) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();
            if(slotStack.getCount() == slotStackOld.getCount()) return ItemStack.EMPTY;
            slot.onTake(playerIn, slotStack);
        }

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public String getName() {
        return smithingTable.getName();
    }

    @Override
    public boolean hasCustomName() {
        return smithingTable.hasCustomName();
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return smithingTable.getDisplayName();
    }
}
