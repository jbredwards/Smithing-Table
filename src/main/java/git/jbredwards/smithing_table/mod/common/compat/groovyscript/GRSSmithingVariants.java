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

package git.jbredwards.smithing_table.mod.common.compat.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import git.jbredwards.smithing_table.mod.common.block.TableData;
import git.jbredwards.smithing_table.mod.common.item.ItemSmithingTable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author jbred
 *
 */
public class GRSSmithingVariants implements IScriptReloadable
{
    @Nonnull
    @GroovyBlacklist
    public static final GRSSmithingVariants INSTANCE = new GRSSmithingVariants();

    @Nonnull
    @Override
    @GroovyBlacklist
    public Collection<String> getAliases() {
        return Collections.singleton("variants");
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        ItemSmithingTable.CUSTOM_VARIANTS.clear();
        ItemSmithingTable.REMOVED_VARIANTS.clear();
    }

    @Override
    @GroovyBlacklist
    public void afterScriptLoad() {
        // NO-OP
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(@Nonnull final ItemStack variant) {
        if(variant.getItem() instanceof ItemBlock) {
            if(variant.isEmpty()) GroovyLog.get().errorMC("Cannot add empty variant to smithing table");
            else if(variant.getMetadata() == OreDictionary.WILDCARD_VALUE) {
                @Nonnull final NonNullList<ItemStack> variants = NonNullList.create();
                variant.getItem().getSubItems(CreativeTabs.SEARCH, variants);
                variants.forEach(this::add);
            }

            else ItemSmithingTable.CUSTOM_VARIANTS.add(new TableData(variant.getItem(), variant.getMetadata()));
        }

        else GroovyLog.get().errorMC("Cannot add empty or non-block variant to smithing table");
    }

    /**
     * Note: Cannot remove the default oak smithing table.
     */
    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    public void remove(@Nonnull final ItemStack variant) {
        if(variant.isEmpty()) GroovyLog.get().errorMC("Cannot remove empty variant from smithing table");
        else if(variant.getMetadata() == OreDictionary.WILDCARD_VALUE) {
            @Nonnull final NonNullList<ItemStack> variants = NonNullList.create();
            variant.getItem().getSubItems(CreativeTabs.SEARCH, variants);
            variants.forEach(this::remove);
        }

        else ItemSmithingTable.REMOVED_VARIANTS.add(new TableData(variant.getItem(), variant.getMetadata()));
    }

    /**
     * Note: Does not remove the default oak smithing table.
     */
    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    public void removeAll() {
        ItemSmithingTable.REMOVED_VARIANTS.clear();
        ItemSmithingTable.REMOVED_VARIANTS.add(null);
    }
}
