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

package git.jbredwards.smithing_table.mod.common.compat.justenoughitems;

import git.jbredwards.smithing_table.api.SmithingContent;
import git.jbredwards.smithing_table.api.SmithingRecipe;
import git.jbredwards.smithing_table.api.SmithingSlotInfo;
import git.jbredwards.smithing_table.api.SmithingTemplate;
import git.jbredwards.smithing_table.mod.client.gui.GuiSmithingTable;
import git.jbredwards.smithing_table.mod.common.inventory.ContainerSmithingTable;
import git.jbredwards.smithing_table.mod.common.item.ItemSmithingTable;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 *
 * @author jbred
 *
 */
@JEIPlugin
public final class JEIHandler implements IModPlugin
{
    @Override
    public void register(@Nonnull final IModRegistry registry) {
        registry.handleRecipes(SmithingRecipe.class, SmithingJEIRecipeWrapper::new, SmithingJEIRecipeCategory.ID);
        registry.addRecipes(SmithingRecipe.REGISTRY.getValuesCollection(), SmithingJEIRecipeCategory.ID);
        registry.addRecipeClickArea(GuiSmithingTable.class, 68, 49, 22, 15, SmithingJEIRecipeCategory.ID);
        registry.addRecipeClickArea(GuiSmithingTable.Sub.class, 95, 49, 22, 15, SmithingJEIRecipeCategory.ID);
        registry.addRecipeCatalyst(new ItemStack(SmithingContent.SMITHING_TABLE_ITEM, 1, OreDictionary.WILDCARD_VALUE), SmithingJEIRecipeCategory.ID);
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerSmithingTable.class, SmithingJEIRecipeCategory.ID, SmithingSlotInfo.TEMPLATE , 3, SmithingSlotInfo.OUTPUT + 1, 36);
    }

    @Override
    public void registerCategories(@Nonnull final IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new SmithingJEIRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerItemSubtypes(@Nonnull final ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.registerSubtypeInterpreter(SmithingContent.SMITHING_TABLE_ITEM, stack -> ItemSmithingTable.getVariant(stack).toString());
        subtypeRegistry.registerSubtypeInterpreter(SmithingContent.SMITHING_TEMPLATE, stack -> Objects.toString(SmithingTemplate.deserialize(stack)));
    }
}
