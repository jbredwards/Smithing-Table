package git.jbredwards.smithing_table.mod.client.gui;

import git.jbredwards.smithing_table.mod.common.inventory.ContainerSmithingTable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@SideOnly(Side.CLIENT)
public abstract class GuiSmithingTable extends GuiContainer
{
    @Nonnull
    protected final EntityPlayer player;
    public GuiSmithingTable(@Nonnull final EntityPlayer playerIn, @Nonnull final World worldIn, final int x, final int y, final int z) {
        super(new ContainerSmithingTable(playerIn, worldIn, x, y, z));
        player = playerIn;
    }

    public static class New extends GuiSmithingTable
    {
        public New(@Nonnull EntityPlayer playerIn, @Nonnull World worldIn, int x, int y, int z) {
            super(playerIn, worldIn, x, y, z);
        }

        @Override
        protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

        }
    }

    public static class Old extends GuiSmithingTable
    {
        public Old(@Nonnull EntityPlayer playerIn, @Nonnull World worldIn, int x, int y, int z) {
            super(playerIn, worldIn, x, y, z);
        }

        @Override
        protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

        }
    }
}
