package git.jbredwards.smithing_table.api;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import git.jbredwards.smithing_table.mod.SmithingTable;
import io.netty.util.internal.IntegerHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Crafting ingredient wrapper for {@link SmithingTemplate}.
 *
 * @since 1.0.0
 * @author jbred
 *
 */
public class SmithingTemplateIngredient extends Ingredient
{
    @Nonnull public static final IConditionFactory CONDITION = (ctx, json) -> SmithingTable::templateEnabled;
    @Nonnull public static final IIngredientFactory FACTORY = SmithingTemplateIngredient::parse;

    @Nonnull
    protected final SmithingTemplate[] templates;
    public SmithingTemplateIngredient(@Nonnull final SmithingTemplate... templatesIn) {
        super(Arrays.stream(templatesIn).map(SmithingTemplate::serialize).toArray(ItemStack[]::new));
        templates = templatesIn;
    }

    public SmithingTemplateIngredient(@Nonnull final Iterable<SmithingTemplate> templatesIn) {
        this(Iterables.toArray(templatesIn, SmithingTemplate.class));
    }

    @Nonnull
    public List<SmithingTemplate> getSmithingTemplates() {
        return Collections.unmodifiableList(Arrays.asList(templates));
    }

    @Override
    public boolean apply(@Nullable final ItemStack candidate) {
        return candidate != null && !candidate.isEmpty() && ArrayUtils.contains(templates, SmithingTemplate.deserialize(candidate));
    }

    @Nonnull
    private static SmithingTemplateIngredient parse(@Nonnull final JsonContext ctx, @Nonnull final JsonObject json) {
        @Nonnull final IntegerHolder index = new IntegerHolder();
        return new SmithingTemplateIngredient(JsonUtils.isJsonPrimitive(json, "id") ? Collections.singleton(parseTemplate(ctx, "id", key -> JsonUtils.getString(json, key)))
        : Iterables.transform(JsonUtils.getJsonArray(json, "id"), element -> parseTemplate(ctx, "id[" + index.value +++ ']', key -> JsonUtils.getString(element, key))));
    }

    @Nonnull
    private static SmithingTemplate parseTemplate(@Nonnull final JsonContext ctx, @Nonnull final String key, @Nonnull final Function<String, String> reader) {
        @Nonnull final String id = reader.apply(key);
        return Objects.requireNonNull(SmithingTemplate.REGISTRY.getValue(new ResourceLocation(ctx.appendModId(id))), () -> "Expected " + key + " to be a smithing template, was \"" + id + '"');
    }
}
