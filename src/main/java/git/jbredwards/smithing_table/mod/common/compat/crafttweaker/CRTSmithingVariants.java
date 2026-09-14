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

package git.jbredwards.smithing_table.mod.common.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import git.jbredwards.smithing_table.mod.SmithingTable;
import git.jbredwards.smithing_table.mod.common.block.TableData;
import git.jbredwards.smithing_table.mod.common.item.ItemSmithingTable;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@ZenRegister
@ZenClass("mods." + SmithingTable.MOD_ID + ".variants")
public final class CRTSmithingVariants
{
    @ZenMethod
    public static void add(@Nonnull final IItemStack variant) {
        if(variant.isItemBlock()) {
            @Nonnull final ItemStack stack = CraftTweakerMC.getItemStack(variant);
            if(!stack.isEmpty()) ItemSmithingTable.CUSTOM_VARIANTS.add(new TableData(stack.getItem(), stack.getMetadata()));
            else CraftTweakerAPI.logError("Cannot add empty variant to smithing table");
        }

        else CraftTweakerAPI.logError("Cannot add empty or non-block variant to smithing table");
    }

    /**
     * Note: Cannot remove the default oak smithing table.
     */
    @ZenMethod
    public static void remove(@Nonnull final IItemStack variant) {
        @Nonnull final ItemStack stack = CraftTweakerMC.getItemStack(variant);
        if(!stack.isEmpty()) ItemSmithingTable.REMOVED_VARIANTS.add(new TableData(stack.getItem(), stack.getMetadata()));
        else CraftTweakerAPI.logError("Cannot remove empty variant from smithing table");
    }

    /**
     * Note: Does not remove the default oak smithing table.
     */
    @ZenMethod
    public static void removeAll() {
        ItemSmithingTable.REMOVED_VARIANTS.clear();
        ItemSmithingTable.REMOVED_VARIANTS.add(null);
    }
}
