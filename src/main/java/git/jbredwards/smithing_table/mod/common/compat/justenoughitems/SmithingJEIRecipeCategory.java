package git.jbredwards.smithing_table.mod.common.compat.justenoughitems;

import git.jbredwards.smithing_table.Tags;
import git.jbredwards.smithing_table.api.SmithingContent;
import git.jbredwards.smithing_table.mod.common.block.TileSmithingTable;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.config.Constants;
import mezz.jei.startup.ForgeModIdHelper;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public class SmithingJEIRecipeCategory implements IRecipeCategory<SmithingJEIRecipeWrapper>
{
    @Nonnull
    public static final String ID = Tags.MOD_ID + ":jei_category";

    @Nonnull
    protected final IDrawable background, icon, slot, arrow;
    public SmithingJEIRecipeCategory(@Nonnull final IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(Constants.RECIPE_GUI_VANILLA, 148, 0, 108, 28); // Blank.
        icon = guiHelper.createDrawableIngredient(Item.getItemFromBlock(SmithingContent.SMITHING_TABLE).getDefaultInstance());
        slot = guiHelper.getSlotDrawable();
        arrow = guiHelper.createDrawable(Constants.RECIPE_GUI_VANILLA, 75, 169, 24, 17);
    }

    @Nonnull
    @Override
    public String getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public String getModName() {
        return Tags.MOD_NAME;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return Translator.translateToLocal(ID);
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void drawExtras(@Nonnull final Minecraft minecraft) {
        slot.draw(minecraft, 0, 6);
        slot.draw(minecraft, 18, 6);
        slot.draw(minecraft, 36, 6);
        slot.draw(minecraft, 90, 6);
        arrow.draw(minecraft, 60, 6);
    }

    @Override
    public void setRecipe(@Nonnull final IRecipeLayout recipeLayout, @Nonnull final SmithingJEIRecipeWrapper recipeWrapper, @Nonnull final IIngredients ingredients) {
        @Nullable final ResourceLocation name = recipeWrapper.getRegistryName();
        if(name != null) recipeLayout.getItemStacks().addTooltipCallback((slot, input, ingredient, tooltip) -> {
            if(slot == TileSmithingTable.OUTPUT) {
                @Nullable final ResourceLocation itemName = ingredient.getItem().getRegistryName();
                @Nonnull final String recipeModId = name.getNamespace();

                if(itemName != null && !recipeModId.equals(itemName.getNamespace())) {
                    @Nullable final String modName = ForgeModIdHelper.getInstance().getFormattedModNameForModId(recipeModId);
                    if(modName != null) tooltip.add(TextFormatting.GRAY + Translator.translateToLocalFormatted("jei.tooltip.recipe.by", modName));
                }

                if(Minecraft.getMinecraft().gameSettings.advancedItemTooltips) {
                    tooltip.add(TextFormatting.DARK_GRAY + Translator.translateToLocalFormatted("jei.tooltip.recipe.id", name.toString()));
                }
            }
        });

        recipeLayout.getItemStacks().init(TileSmithingTable.TEMPLATE, true, 0, 6);
        recipeLayout.getItemStacks().init(TileSmithingTable.EQUIPMENT, true, 18, 6);
        recipeLayout.getItemStacks().init(TileSmithingTable.MATERIAL, true, 36, 6);
        recipeLayout.getItemStacks().init(TileSmithingTable.OUTPUT, false, 90, 6);
        recipeLayout.getItemStacks().set(ingredients);
    }
}
