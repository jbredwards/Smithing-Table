package git.jbredwards.smithing_table.mod.common.inventory;

import git.jbredwards.smithing_table.mod.SmithingTable;
import git.jbredwards.smithing_table.mod.common.block.TileSmithingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 *
 * @author jbred
 *
 */
public class ContainerSmithingTable extends Container
{
    @Nonnull protected final World world;
    @Nonnull protected final TileSmithingTable smithingTable;
    @Nonnull protected final SlotSmithingOutput recipeHandler;

    public ContainerSmithingTable(@Nonnull final EntityPlayer player, @Nonnull final World worldIn, final int x, final int y, final int z) {
        smithingTable = Objects.requireNonNull((TileSmithingTable)worldIn.getTileEntity(new BlockPos(x, y, z)));
        world = worldIn;
        // New container slots.
        if(SmithingTable.templateEnabled()) {
            addSlotToContainer(new SlotItemHandler(smithingTable.basicInventory, 0, 8, 48));
            addSlotToContainer(new SlotItemHandler(smithingTable.basicInventory, 1, 26, 48));
            addSlotToContainer(new SlotItemHandler(smithingTable.basicInventory, 2, 44, 48));
            addSlotToContainer(recipeHandler = new SlotSmithingOutput(smithingTable.basicInventory, 3, 98, 48));
        }
        // Old container slots.
        else {
            addSlotToContainer(new SlotItemHandler(smithingTable.basicInventory, 1, 27, 47));
            addSlotToContainer(new SlotItemHandler(smithingTable.basicInventory, 2, 76, 47));
            addSlotToContainer(recipeHandler = new SlotSmithingOutput(smithingTable.basicInventory, 3, 134, 47));
        }
        // Player inventory slots.
        for(int i = 0; i < 3; ++i) for(int j = 0; j < 9; ++j) addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for(int k = 0; k < 9; ++k) addSlotToContainer(new Slot(player.inventory, k, 8 + k * 18, 142));
    }

    @Override
    public boolean canInteractWith(@Nonnull final EntityPlayer playerIn) {
        return playerIn.world == world && !smithingTable.isInvalid() && playerIn.getDistanceSqToCenter(smithingTable.getPos()) < 64;
    }
}
