package git.jbredwards.smithing_table.mod.common.compat.justenoughitems;

import git.jbredwards.smithing_table.api.SmithingContent;
import git.jbredwards.smithing_table.api.SmithingRecipe;
import git.jbredwards.smithing_table.mod.client.gui.GuiSmithingTable;
import git.jbredwards.smithing_table.mod.common.item.ItemSmithingTable;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;

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
        registry.addRecipeCatalyst(new ItemStack(SmithingContent.SMITHING_TABLE, 1, OreDictionary.WILDCARD_VALUE), SmithingJEIRecipeCategory.ID);
    }

    @Override
    public void registerCategories(@Nonnull final IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new SmithingJEIRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerItemSubtypes(@Nonnull final ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.registerSubtypeInterpreter(Item.getItemFromBlock(SmithingContent.SMITHING_TABLE),
                stack -> ItemSmithingTable.getVariant(stack).toString());
    }
}
