/*
 * Copyright (C) <2026 to Present> <jbredwards>
 *
 * All rights are reserved, except where explicitly granted by the original
 * copyright holder or where explicitly granted by the Mod Permissions License as
 * published by Jbredwards, either version 1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See the Mod Permissions License for more details
 * <https://www.github.com/jbredwards/mod-permissions-license>.
 */

package git.jbredwards.smithing_table.mod.client;

import git.jbredwards.smithing_table.mod.SmithingTable;
import git.jbredwards.smithing_table.mod.SmithingTableCfg;
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
        if(!world.isRemote) player.openGui(SmithingTable.MOD_ID, 1, world, pos.getX(), pos.getY(), pos.getZ());
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
            case 2: return SmithingTableCfg.armorStand || !SmithingTableCfg.armorStandBackground
                    ? new GuiSmithingTable(player, world, x, y, z) : new GuiSmithingTable.Sub(player, world, x, y, z);
            default: return null;
        }
    }
}
