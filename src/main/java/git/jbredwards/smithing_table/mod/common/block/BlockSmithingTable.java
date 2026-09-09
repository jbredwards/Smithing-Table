package git.jbredwards.smithing_table.mod.common.block;

import git.jbredwards.smithing_table.api.SmithingContent;
import git.jbredwards.smithing_table.api.SmithingSlotInfo;
import git.jbredwards.smithing_table.mod.client.SmithingTableGuiHandler;
import git.jbredwards.smithing_table.mod.common.item.ItemSmithingTable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jbred
 *
 */
public class BlockSmithingTable extends BlockContainer
{
    public BlockSmithingTable(@Nonnull final Material materialIn) { this(materialIn, materialIn.getMaterialMapColor()); }
    public BlockSmithingTable(@Nonnull final Material materialIn, @Nonnull final MapColor color) {
        super(materialIn, color);
        setSoundType(SoundType.WOOD).setCreativeTab(SmithingContent.CREATIVE_TAB).setHardness(2.5f);
    }

    @Override
    public boolean onBlockActivated(@Nonnull final World worldIn, @Nonnull final BlockPos pos, @Nonnull final IBlockState state, @Nonnull final EntityPlayer playerIn, @Nonnull final EnumHand hand, @Nonnull final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
        SmithingTableGuiHandler.openGui(playerIn, worldIn, pos);
        return true;
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(@Nonnull final IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull final World worldIn, final int meta) {
        return new TileSmithingTable();
    }

    @Override
    public boolean hasComparatorInputOverride(@Nonnull final IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(@Nonnull final IBlockState state, @Nonnull final World worldIn, @Nonnull final BlockPos pos) {
        @Nullable final TileEntity tile = worldIn.getTileEntity(pos);
        return ItemHandlerHelper.calcRedstoneFromInventory(tile instanceof TileSmithingTable ? ((TileSmithingTable)tile).basicInventory : null);
    }

    @Override
    public void breakBlock(@Nonnull final World worldIn, @Nonnull final BlockPos pos, @Nonnull final IBlockState state) {
        @Nullable final TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof TileSmithingTable) {
            @Nonnull final IItemHandler inv = ((TileSmithingTable)tile).basicInventory;
            for(int slot = 0; slot < SmithingSlotInfo.OUTPUT; slot++) InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), inv.getStackInSlot(slot));
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void onBlockPlacedBy(@Nonnull final World worldIn, @Nonnull final BlockPos pos, @Nonnull final IBlockState state, @Nonnull final EntityLivingBase placer, @Nonnull final ItemStack stack) {
        @Nullable final TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof TileSmithingTable) {
            if(stack.hasDisplayName()) ((TileSmithingTable)tile).setCustomName(stack.getDisplayName());
            ((TileSmithingTable)tile).variant = ItemSmithingTable.getVariant(stack);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
        }

        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void harvestBlock(@Nonnull final World worldIn, @Nonnull final EntityPlayer player, @Nonnull final BlockPos pos, @Nonnull final IBlockState state, @Nullable final TileEntity te, @Nonnull final ItemStack stack) {
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005f);

        @Nonnull final List<ItemStack> drops = new ArrayList<>();
        if(te instanceof TileSmithingTable) {
            @Nonnull final ItemStack drop = ItemSmithingTable.setVariant(new ItemStack(this), ((TileSmithingTable)te).variant);
            if(((TileSmithingTable)te).hasCustomName()) drop.setStackDisplayName(((TileSmithingTable)te).customName);
            drops.add(drop);
        }

        ForgeEventFactory.fireBlockHarvesting(drops, worldIn, pos, state, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack), 1, EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0, player);
        for(@Nonnull final ItemStack drop : drops) spawnAsEntity(worldIn, pos, drop);
    }

    @Nonnull
    @Override
    public ItemStack getItem(@Nonnull final World worldIn, @Nonnull final BlockPos pos, @Nonnull final IBlockState state) {
        return ItemSmithingTable.setVariant(super.getItem(worldIn, pos, state), TileSmithingTable.getVariant(worldIn, pos));
    }

    @Nonnull
    @Override
    public SoundType getSoundType(@Nonnull final IBlockState state, @Nonnull final World world, @Nonnull final BlockPos pos, @Nullable final Entity entity) {
        return TileSmithingTable.getVariant(world, pos).getBlock().getSoundType();
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(UnlistedVariantProperty.INSTANCE).build();
    }

    @Nonnull
    @Override
    public IBlockState getExtendedState(@Nonnull final IBlockState state, @Nonnull final IBlockAccess world, @Nonnull final BlockPos pos) {
        if(state instanceof IExtendedBlockState) {
            return ((IExtendedBlockState)state).withProperty(UnlistedVariantProperty.INSTANCE, TileSmithingTable.getVariant(world, pos));
        }

        return state;
    }

    @Override
    public void getSubBlocks(@Nonnull final CreativeTabs itemIn, @Nonnull final NonNullList<ItemStack> items) {
        for(@Nonnull final TableData variant : ItemSmithingTable.getVariants()) items.add(ItemSmithingTable.setVariant(new ItemStack(this), variant));
    }

    @Override
    public boolean isOpaqueCube(@Nonnull final IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(@Nonnull final IBlockState state, @Nonnull final IBlockAccess world, @Nonnull final BlockPos pos) {
        return TileSmithingTable.getVariant(world, pos).getBlockState().isNormalCube();
    }

    @Override
    public boolean doesSideBlockRendering(@Nonnull final IBlockState state, @Nonnull final IBlockAccess world, @Nonnull final BlockPos pos, @Nonnull final EnumFacing face) {
        return TileSmithingTable.getVariant(world, pos).getBlockState().isOpaqueCube();
    }

    @Override
    public boolean canRenderInLayer(@Nonnull final IBlockState state, @Nonnull final BlockRenderLayer layer) {
        return true; // Handled by model.
    }
}
