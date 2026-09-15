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
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import git.jbredwards.smithing_table.api.SmithingContent;
import git.jbredwards.smithing_table.api.SmithingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;

/**
 *
 * @author jbred
 *
 */
public class GRSSmithingRecipes extends ForgeRegistryWrapper<SmithingRecipe>
{
    @Nonnull
    @GroovyBlacklist
    public static final GRSSmithingRecipes INSTANCE = new GRSSmithingRecipes();

    @GroovyBlacklist
    public GRSSmithingRecipes() {
        super(SmithingRecipe.REGISTRY, Collections.singleton("recipes"));
    }

    @Nonnull
    public Builder builder() {
        return new Builder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(@Nonnull final IIngredient template, @Nonnull final IIngredient equipment, @Nonnull final IIngredient material, @Nonnull final ItemStack output) {
        builder()
                .input(template, equipment, material)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(@Nonnull final String name, @Nonnull final IIngredient template, @Nonnull final IIngredient equipment, @Nonnull final IIngredient material, @Nonnull final ItemStack output) {
        builder()
                .name(name)
                .input(template, equipment, material)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(@Nonnull final ResourceLocation name, @Nonnull final IIngredient template, @Nonnull final IIngredient equipment, @Nonnull final IIngredient material, @Nonnull final ItemStack output) {
        builder()
                .name(name)
                .input(template, equipment, material)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    public boolean removeByTemplate(@Nonnull final IIngredient ingredient) {
        @Nonnull final ItemStack[] matching = ingredient.getMatchingStacks();
        return streamRecipes().removeIf(recipe -> {
            for(@Nonnull final ItemStack stack : matching) if(recipe.getTemplateIngredient().test(stack)) return true;
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    public boolean removeByEquipment(@Nonnull final IIngredient ingredient) {
        @Nonnull final ItemStack[] matching = ingredient.getMatchingStacks();
        return streamRecipes().removeIf(recipe -> {
            for(@Nonnull final ItemStack stack : matching) if(recipe.getEquipmentIngredient().test(stack)) return true;
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    public boolean removeByMaterial(@Nonnull final IIngredient ingredient) {
        @Nonnull final ItemStack[] matching = ingredient.getMatchingStacks();
        return streamRecipes().removeIf(recipe -> {
            for(@Nonnull final ItemStack stack : matching) if(recipe.getMaterialIngredient().test(stack)) return true;
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    public boolean removeByInput(@Nonnull final IIngredient ingredient) {
        @Nonnull final ItemStack[] matching = ingredient.getMatchingStacks();
        return streamRecipes().removeIf(recipe -> {
            for(@Nonnull final ItemStack stack : matching) if(recipe.getTemplateIngredient().test(stack)
            || recipe.getEquipmentIngredient().test(stack) || recipe.getMaterialIngredient().test(stack)) return true;
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    public boolean removeByOutput(@Nonnull final IIngredient ingredient) {
        return streamRecipes().removeIf(recipe -> ingredient.isCase(recipe.getResult()));
    }

    public final class Builder extends AbstractRecipeBuilder<SmithingRecipe>
    {
        @Nullable private IIngredient template = null;
        @Nullable private SoundEvent sound = null;

        @Nonnull
        @RecipeBuilderMethodDescription(field = "sound event")
        public Builder sound(@Nullable final SoundEvent soundEvent) {
            sound = soundEvent;
            return this;
        }

        @Nonnull
        @Override
        public String getErrorMsg() {
            return "Error adding smithing recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(@Nonnull final GroovyLog.Msg msg) {
            // Allow empty template ingredient.
            if(template == null && input.size() == 3) template = input.remove(0);
            validateItems(msg, 2, 2, 1, 1);
        }

        @Nullable
        @Override
        @RecipeBuilderRegistrationMethod
        public SmithingRecipe register() {
            if(!validate()) return null;
            validateName();

            @Nonnull final SmithingRecipe recipe = new SmithingRecipe.Impl(
                    sound != null ? sound : SmithingContent.BLOCK_SMITHING_TABLE_USE,
                    template != null ? template.toMcIngredient() : Ingredient.EMPTY,
                    input.get(0).toMcIngredient(),
                    input.get(1).toMcIngredient(),
                    output.get(0));

            GRSSmithingRecipes.this.add(recipe.setRegistryName(name));
            return recipe;
        }
    }
}
