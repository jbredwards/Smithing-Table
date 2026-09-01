package git.jbredwards.smithing_table.api;

import git.jbredwards.smithing_table.mod.SmithingTable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;

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
     * @since 1.0.0
     */
    @Nonnull
    IForgeRegistry<SmithingRecipe> REGISTRY = Objects.requireNonNull(GameRegistry.findRegistry(SmithingRecipe.class), "Registry was loaded too early!");

    /**
     * @return The smithing template ingredient for this recipe. May be empty.
     * @since 1.0.0
     */
    @Nonnull
    Collection<SmithingTemplate> getTemplateIngredient();

    /**
     * @return True if this recipe requires {@link SmithingRecipe#getTemplateIngredient()}
     * while smithing templates are disabled in the server config.
     * @since 1.0.0
     */
    boolean alwaysRequireSmithingTemplate();

    /**
     * @return The equipment ingredient for this recipe.
     * @since 1.0.0
     */
    @Nonnull
    Ingredient getEquipmentIngredient();

    /**
     * @return The material ingredient for this recipe.
     * @since 1.0.0
     */
    @Nonnull
    Ingredient getMaterialIngredient();

    /**
     * @return The raw result of this recipe. Used by recipe viewing mods and recipe logic.
     * @since 1.0.0
     */
    @Nonnull
    ItemStack getResult();

    /**
     * @return The actual ItemStack given to the player when this recipe is performed.
     * @throws NullPointerException If any parameters are null.
     * @since 1.0.0
     */
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
     * @return The first recipe found that matches the input ingredients.
     * @throws NullPointerException If any parameters are null.
     * @since 1.0.0
     */
    @Nullable
    static SmithingRecipe lookupResult(@Nonnull final ItemStack template, @Nonnull final ItemStack equipment, @Nonnull final ItemStack material) {
        if(equipment.isEmpty() || material.isEmpty()) return null;

        @Nullable final SmithingTemplate st = SmithingTemplate.deserialize(template);
        return REGISTRY.getValuesCollection().stream()
                .filter(recipe
                        -> (ignoreTemplate(recipe) || !template.isEmpty() && recipe.getTemplateIngredient().contains(st))
                        && recipe.getEquipmentIngredient().test(equipment) && recipe.getMaterialIngredient().test(material))
                .findFirst()
                .orElse(null);
    }

    /**
     * @return True if the input ingredients form part of a recipe.
     * @throws NullPointerException If any parameters are null.
     * @since 1.0.0
     */
    static boolean partialMatch(@Nonnull final ItemStack template, @Nonnull final ItemStack equipment, @Nonnull final ItemStack material, @Nonnull final Predicate<SmithingRecipe> ignoreTemplate) {
        if(equipment.isEmpty() && material.isEmpty()) return true;

        @Nullable final SmithingTemplate st = SmithingTemplate.deserialize(template);
        return REGISTRY.getValuesCollection().stream().parallel()
                .anyMatch(recipe
                        -> (template.isEmpty() || recipe.getTemplateIngredient().contains(st) || ignoreTemplate.test(recipe))
                        && (equipment.isEmpty() || recipe.getEquipmentIngredient().test(equipment))
                        && (material.isEmpty() || recipe.getMaterialIngredient().test(material)));
    }

    /**
     * @return True if the provided recipe should ignore its smithing template ingredient.
     * @throws NullPointerException If any parameters are null.
     * @since 1.0.0
     */
    static boolean ignoreTemplate(@Nonnull final SmithingRecipe recipe) {
        return recipe.getTemplateIngredient().isEmpty() || !SmithingTable.templateEnabled() && !recipe.alwaysRequireSmithingTemplate();
    }

    /**
     * Default {@code SmithingRecipe} implementation.
     * @author jbred
     *
     */
    @ApiStatus.AvailableSince("1.0.0")
    class Impl extends IForgeRegistryEntry.Impl<SmithingRecipe> implements SmithingRecipe
    {
        @Nonnull private final Collection<SmithingTemplate> template;
        @Nonnull private final Ingredient equipment, material;
        @Nonnull private final ItemStack result;

        private final boolean alwaysRequireSmithingTemplate;
        public Impl(@Nonnull final Collection<SmithingTemplate> template, @Nonnull final Object equipment, @Nonnull final Object material, @Nonnull final ItemStack result, final boolean alwaysRequireSmithingTemplate) {
            this.template = Collections.unmodifiableCollection(template);
            this.equipment = Objects.requireNonNull(CraftingHelper.getIngredient(equipment), "Cannot parse equipment ingredient: " + equipment);
            this.material = Objects.requireNonNull(CraftingHelper.getIngredient(material), "Cannot parse material ingredient: " + equipment);
            this.result = Objects.requireNonNull(result);
            this.alwaysRequireSmithingTemplate = alwaysRequireSmithingTemplate;
        }

        @Nonnull
        @Override
        public Collection<SmithingTemplate> getTemplateIngredient() {
            return this.template;
        }

        @Override
        public boolean alwaysRequireSmithingTemplate() {
            return this.alwaysRequireSmithingTemplate;
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
    }
}
