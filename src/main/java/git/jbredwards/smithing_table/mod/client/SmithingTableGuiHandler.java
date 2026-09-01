package git.jbredwards.smithing_table.mod.client;

import git.jbredwards.smithing_table.mod.SmithingTable;
import git.jbredwards.smithing_table.mod.client.gui.GuiSmithingTable;
import git.jbredwards.smithing_table.mod.common.inventory.ContainerSmithingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public enum SmithingTableGuiHandler implements IGuiHandler
{
    INSTANCE;

    public static void openGui(@Nonnull final EntityPlayer player, @Nonnull final World world, @Nonnull final BlockPos pos) {
        if(!world.isRemote) player.openGui(SmithingTable.MOD_ID, SmithingTable.templateEnabled() ? 1 : 2, world, pos.getX(), pos.getY(), pos.getZ());
    }

    @Nullable
    @Override
    public Object getServerGuiElement(final int ID, @Nonnull final EntityPlayer player, @Nonnull final World world, final int x, final int y, final int z) {
        switch(ID) {
            case 1:
            case 2: return new ContainerSmithingTable(player, world, x, y, z);
            default: return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(final int ID, @Nonnull final EntityPlayer player, @Nonnull final World world, final int x, final int y, final int z) {
        switch(ID) {
            case 1:
            case 2: return new GuiSmithingTable(player, world, x, y, z);
            default: return null;
        }
    }
}
