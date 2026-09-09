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

import com.google.common.collect.ImmutableList;
import git.jbredwards.smithing_table.api.SmithingRecipe;
import mezz.jei.Internal;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public class SmithingJEIRecipeWrapper implements ICraftingRecipeWrapper
{
    @Nonnull
    protected final SmithingRecipe recipe;
    public SmithingJEIRecipeWrapper(@Nonnull final SmithingRecipe recipeIn) {
        recipe = recipeIn;
    }

    @Override
    public void getIngredients(@Nonnull final IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, Internal.getStackHelper().expandRecipeItemStackInputs(ImmutableList
                .of(recipe.getTemplateIngredient(), recipe.getEquipmentIngredient(), recipe.getMaterialIngredient())));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResult());
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return recipe.getRegistryName();
    }
}
