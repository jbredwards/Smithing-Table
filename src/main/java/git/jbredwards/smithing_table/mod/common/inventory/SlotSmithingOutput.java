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

package git.jbredwards.smithing_table.mod.common.inventory;

import git.jbredwards.smithing_table.mod.SmithingTableCfg;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public class SlotSmithingOutput extends SlotItemHandler
{
    @Nonnull
    protected final ContainerSmithingTable container;
    public SlotSmithingOutput(@Nonnull final ContainerSmithingTable containerIn, final int index, final int xPosition, final int yPosition) {
        super(containerIn.smithingTable.processor, index, xPosition, yPosition);
        container = containerIn;
    }

    @Override
    public void putStack(@Nonnull final ItemStack stack) {
        // NO-OP
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        container.detectAndSendChanges();
    }

    @Nonnull
    @Override
    public ItemStack onTake(@Nonnull final EntityPlayer thePlayer, @Nonnull final ItemStack stack) {
        if(!SmithingTableCfg.automationSound) container.smithingTable.playSound(stack);
        return super.onTake(thePlayer, stack);
    }
}
