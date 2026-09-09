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

package git.jbredwards.smithing_table.api;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * The recipe object used by Smithing Tables.
 *
 * @since 1.0.0
 * @author jbred
 *
 */
public interface SmithingRecipe extends IForgeRegistryEntry<SmithingRecipe>
{
    /**
     * Holds all smithing recipes. <b>This field cannot be initialized before fml pre-init!</b>
     * <br> This registry is an {@link net.minecraftforge.registries.IForgeRegistryModifiable}.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    IForgeRegistry<SmithingRecipe> REGISTRY = Objects.requireNonNull(GameRegistry.findRegistry(SmithingRecipe.class), "Registry was loaded too early!");

    /**
     * @return The smithing template ingredient for this recipe. May be empty.
     * @see SmithingTemplateIngredient
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    Ingredient getTemplateIngredient();

    /**
     * @return The equipment ingredient for this recipe.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    Ingredient getEquipmentIngredient();

    /**
     * @return The material ingredient for this recipe.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    Ingredient getMaterialIngredient();

    /**
     * @return The raw result of this recipe. Used by recipe viewing mods and recipe logic.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ItemStack getResult();

    /**
     * @return The actual ItemStack given to the player when this recipe is performed.
     * @throws NullPointerException If any parameters are null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    default ItemStack getCraftedResult(@Nonnull final IItemHandler smithingInventory) {
        @Nonnull final ItemStack equipment = smithingInventory.getStackInSlot(1);
        @Nonnull final ItemStack result = getResult().copy();

        if(equipment.hasTagCompound()) {
            @Nonnull final NBTTagCompound nbt = equipment.getTagCompound().copy();
            if(result.hasTagCompound()) nbt.merge(result.getTagCompound());
            result.setTagCompound(nbt);
        }

        return result;
    }

    /**
     * @return The sound to be played when this recipe is performed.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    default SoundEvent getSound(@Nonnull final ItemStack result) {
        return SmithingContent.BLOCK_SMITHING_TABLE_USE;
    }

    /**
     * @return The first recipe found that matches the input ingredients.
     * @throws NullPointerException If any parameters are null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nullable
    static SmithingRecipe lookupResult(@Nonnull final ItemStack template, @Nonnull final ItemStack equipment, @Nonnull final ItemStack material) {
        if(equipment.isEmpty() || material.isEmpty()) return null;

        return REGISTRY.getValuesCollection().stream()
                .filter(recipe -> testResult(recipe, template, equipment, material))
                .findFirst()
                .orElse(null);
    }

    /**
     * @return True if the recipe can be performed with the provided ingredients.
     * @throws NullPointerException If any parameters are null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    static boolean testResult(@Nonnull final SmithingRecipe recipe, @Nonnull final ItemStack template, @Nonnull final ItemStack equipment, @Nonnull final ItemStack material) {
        return !equipment.isEmpty() && !material.isEmpty() && recipe.getTemplateIngredient().test(template)
                && recipe.getEquipmentIngredient().test(equipment) && recipe.getMaterialIngredient().test(material);
    }

    /**
     * @return True if the input ingredients form part of a recipe.
     * @throws NullPointerException If any parameters are null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    static boolean partialMatch(@Nonnull final ItemStack template, @Nonnull final ItemStack equipment, @Nonnull final ItemStack material) {
        return REGISTRY.getValuesCollection().stream().parallel()
                .anyMatch(recipe
                        -> (template.isEmpty() || recipe.getTemplateIngredient().test(template))
                        && (equipment.isEmpty() || recipe.getEquipmentIngredient().test(equipment))
                        && (material.isEmpty() || recipe.getMaterialIngredient().test(material)));
    }

    /**
     * Default {@code SmithingRecipe} implementation.
     * @author jbred
     *
     */
    @ApiStatus.AvailableSince("1.0.0")
    class Impl extends IForgeRegistryEntry.Impl<SmithingRecipe> implements SmithingRecipe
    {
        @ApiStatus.Internal @Nonnull private final Ingredient template, equipment, material;
        @ApiStatus.Internal @Nonnull private final ItemStack result;
        @ApiStatus.Internal @Nonnull private final SoundEvent sound;

        @ApiStatus.AvailableSince("1.0.0")
        public Impl(@Nonnull final Object template, @Nonnull final Object equipment, @Nonnull final Object material, @Nonnull final ItemStack result) {
            this(SmithingContent.BLOCK_SMITHING_TABLE_USE, template, equipment, material, result);
        }

        @ApiStatus.AvailableSince("1.0.0")
        public Impl(@Nonnull final SoundEvent sound, @Nonnull final Object template, @Nonnull final Object equipment, @Nonnull final Object material, @Nonnull final ItemStack result) {
            if(template instanceof SmithingTemplate) this.template = new SmithingTemplateIngredient((SmithingTemplate)template);
            else this.template = Objects.requireNonNull(CraftingHelper.getIngredient(template), "Cannot parse template ingredient: " + template);
            this.equipment = Objects.requireNonNull(CraftingHelper.getIngredient(equipment), "Cannot parse equipment ingredient: " + equipment);
            this.material = Objects.requireNonNull(CraftingHelper.getIngredient(material), "Cannot parse material ingredient: " + equipment);
            this.result = Objects.requireNonNull(result);
            this.sound = Objects.requireNonNull(sound);
        }

        @Nonnull
        @Override
        public Ingredient getTemplateIngredient() {
            return this.template;
        }

        @Nonnull
        @Override
        public Ingredient getEquipmentIngredient() {
            return this.equipment;
        }

        @Nonnull
        @Override
        public Ingredient getMaterialIngredient() {
            return this.material;
        }

        @Nonnull
        @Override
        public ItemStack getResult() {
            return this.result;
        }

        @Nonnull
        @Override
        public SoundEvent getSound(@Nonnull final ItemStack result) {
            return this.sound;
        }
    }
}
