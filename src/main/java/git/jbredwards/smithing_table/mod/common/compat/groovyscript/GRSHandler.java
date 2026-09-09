package git.jbredwards.smithing_table.mod.common.compat.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientBase;
import git.jbredwards.smithing_table.api.SmithingTemplate;
import git.jbredwards.smithing_table.api.SmithingTemplateIngredient;
import git.jbredwards.smithing_table.mod.SmithingTable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 *
 * @author jbred
 *
 */
@SuppressWarnings("unused")
public final class GRSHandler implements GroovyPlugin
{
    @Nonnull
    @Override
    public String getModId() {
        return SmithingTable.MOD_ID;
    }

    @Nonnull
    @Override
    public String getContainerName() {
        return SmithingTable.MOD_NAME;
    }

    @Override
    public void onCompatLoaded(@Nonnull final GroovyContainer<?> groovyContainer) {
        groovyContainer.addProperty(GRSSmithingRecipes.INSTANCE);
        groovyContainer.addProperty(GRSSmithingTemplates.INSTANCE);
        groovyContainer.objectMapperBuilder("smithing_template_instance", SmithingTemplate.class)
                .parser(IObjectParser.wrapForgeRegistry(SmithingTemplate.REGISTRY))
                .completer(SmithingTemplate.REGISTRY)
                // .textureBinder(SmithingTemplate::serialize, TextureBinder.ofItem())
                .register();
        groovyContainer.objectMapperBuilder("smithing_template", Wrapper.class)
                .parser((s, args) -> {
                    @Nullable final SmithingTemplate template = SmithingTemplate.REGISTRY.getValue(new ResourceLocation(s));
                    return template != null ? Result.some(new Wrapper(template)) : Result.error();
                })
                .completerOfNamed(SmithingTemplate.REGISTRY::getValuesCollection, template -> Objects.toString(template.getRegistryName()))
                // .textureBinder(TextureBinder.ofArray(IIngredient::getMatchingStacks, TextureBinder.ofItem()))
                .register();
    }

    /**
     * Smithing template ingredient wrapped as a GrS ingredient.
     * @author jbred
     *
     */
    private static final class Wrapper extends IngredientBase
    {
        private int amount;

        @Nonnull
        public final SmithingTemplateIngredient ingredient;
        public Wrapper(@Nonnull final SmithingTemplate... template) {
            ingredient = new SmithingTemplateIngredient(template);
        }

        @Nonnull
        @Override
        public IIngredient exactCopy() {
            @Nonnull final Wrapper wrapper = new Wrapper(ingredient.getSmithingTemplates().toArray(new SmithingTemplate[0]));
            wrapper.transformer = transformer;
            wrapper.matchCondition = matchCondition;
            wrapper.amount = amount;
            return wrapper;
        }

        @Nonnull
        @Override
        public Ingredient toMcIngredient() {
            return ingredient;
        }

        @Nonnull
        @Override
        public ItemStack[] getMatchingStacks() {
            @Nonnull final ItemStack[] stacks = new ItemStack[ingredient.getMatchingStacks().length];
            for(int i = 0; i < stacks.length; i++) stacks[i] = getAt(i);
            return stacks;
        }

        @Nonnull
        @Override
        public ItemStack getAt(final int index) {
            return ItemHandlerHelper.copyStackWithSize(ingredient.getMatchingStacks()[index], amount);
        }

        @Override
        public int getAmount() {
            return amount;
        }

        @Override
        public void setAmount(final int newAmount) {
            amount = Math.max(0, newAmount);
        }

        @Override
        public boolean matches(@Nonnull final ItemStack itemStack) {
            return ingredient.test(itemStack);
        }

        /**
         * Use {@code smithing_template_instance('').serialize()} instead.
         * This method only exists just in case someone forgets the "_instance" part of the method name.
         */
        @Nonnull
        public ItemStack serialize() {
            return ingredient.getSmithingTemplates().isEmpty() ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(
                    ingredient.getSmithingTemplates().get(0).serialize(), Math.max(amount, 1));
        }
    }
}
