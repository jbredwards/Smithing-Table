package git.jbredwards.smithing_table.mod.common.compat.justenoughitems;

import com.google.common.collect.ImmutableList;
import git.jbredwards.smithing_table.api.SmithingRecipe;
import git.jbredwards.smithing_table.api.SmithingTemplate;
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
        ingredients.setInputLists(VanillaTypes.ITEM, Internal.getStackHelper().expandRecipeItemStackInputs(ImmutableList.of(
                        recipe.getTemplateIngredient().stream().map(SmithingTemplate::serialize).collect(ImmutableList.toImmutableList()),
                        recipe.getEquipmentIngredient(), recipe.getMaterialIngredient())));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResult());
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return recipe.getRegistryName();
    }
}
