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

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import io.netty.util.internal.IntegerHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;

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
 * @author jbred
 *
 */
@ApiStatus.AvailableSince("1.0.0")
public class SmithingTemplateIngredient extends Ingredient
{
    /**
     * An ingredient factory with a type of "smithing_table:template".
     * <br> It has an "id" string/array field that accepts the id(s) of any registered smithing template.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    public static final IIngredientFactory FACTORY = SmithingTemplateIngredient::parse;

    /**
     * The smithing templates that can be matched by this ingredient.
     */
    @ApiStatus.Internal
    @Nonnull
    private final SmithingTemplate[] matchingTemplates;

    @ApiStatus.AvailableSince("1.0.0")
    public SmithingTemplateIngredient(@Nonnull final SmithingTemplate... templates) {
        super(Arrays.stream(templates).map(SmithingTemplate::serialize).toArray(ItemStack[]::new));
        this.matchingTemplates = templates;
    }

    @ApiStatus.AvailableSince("1.0.0")
    public SmithingTemplateIngredient(@Nonnull final Iterable<SmithingTemplate> templates) {
        this(Iterables.toArray(templates, SmithingTemplate.class));
    }

    /**
     * An immutable accessor for {@link SmithingTemplateIngredient#matchingTemplates}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @UnmodifiableView
    @Nonnull
    public final List<SmithingTemplate> getSmithingTemplates() {
        return Collections.unmodifiableList(Arrays.asList(this.matchingTemplates));
    }

    /**
     * The actual logic for this ingredient.
     * @author jbred
     */
    @ApiStatus.Internal
    @Override
    public boolean apply(@Nullable final ItemStack candidate) {
        return candidate != null && !candidate.isEmpty() && ArrayUtils.contains(this.matchingTemplates, SmithingTemplate.deserialize(candidate));
    }

    /**
     * The ingredient parser logic for {@link SmithingTemplateIngredient#FACTORY}.
     * @throws NullPointerException If any parameters are null.
     * @author jbred
     */
    @ApiStatus.Internal
    @Nonnull
    private static SmithingTemplateIngredient parse(@Nonnull final JsonContext ctx, @Nonnull final JsonObject json) {
        @Nonnull final IntegerHolder index = new IntegerHolder();
        return new SmithingTemplateIngredient(JsonUtils.isJsonPrimitive(json, "id") ? Collections.singleton(parseTemplate(ctx, "id", key -> JsonUtils.getString(json, key)))
        : Iterables.transform(JsonUtils.getJsonArray(json, "id"), element -> parseTemplate(ctx, "id[" + index.value +++ ']', key -> JsonUtils.getString(element, key))));
    }

    /**
     * The smithing template parser for {@link SmithingTemplateIngredient#FACTORY}.
     * @throws NullPointerException If any parameters are null.
     * @author jbred
     */
    @ApiStatus.Internal
    @Nonnull
    private static SmithingTemplate parseTemplate(@Nonnull final JsonContext ctx, @Nonnull final String key, @Nonnull final Function<String, String> reader) {
        @Nonnull final String id = reader.apply(key);
        return Objects.requireNonNull(SmithingTemplate.REGISTRY.getValue(new ResourceLocation(ctx.appendModId(id))), () -> "Expected " + key + " to be a smithing template, was \"" + id + '"');
    }
}
