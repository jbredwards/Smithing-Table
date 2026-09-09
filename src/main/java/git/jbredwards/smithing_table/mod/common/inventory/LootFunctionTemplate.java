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

package git.jbredwards.smithing_table.mod.common.inventory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import git.jbredwards.smithing_table.api.SmithingTemplate;
import git.jbredwards.smithing_table.mod.SmithingTable;
import jeresources.api.conditionals.ICustomLootFunction;
import jeresources.api.drop.LootDrop;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author jbred
 *
 */
@Optional.Interface(modid = "jeresources", iface = "jeresources.api.conditionals.ICustomLootFunction")
public final class LootFunctionTemplate extends LootFunction implements ICustomLootFunction
{
    @Nonnull
    public final SmithingTemplate template;
    public LootFunctionTemplate(@Nonnull final SmithingTemplate templateIn, @Nonnull final LootCondition[] conditionsIn) {
        super(conditionsIn);
        template = templateIn;
    }

    @Nonnull
    @Override
    public ItemStack apply(@Nonnull final ItemStack stack, @Nonnull final Random rand, @Nonnull final LootContext context) {
        return template.serialize(stack);
    }

    @Optional.Method(modid = "jeresources")
    @Override
    public void apply(@Nonnull final LootDrop lootDrop) {
        if(lootDrop.item == null) lootDrop.item = template.serialize();
        else template.serialize(lootDrop.item);
    }

    public static final class Serializer extends LootFunction.Serializer<LootFunctionTemplate>
    {
        public Serializer() {
            super(new ResourceLocation(SmithingTable.MOD_ID, "template"), LootFunctionTemplate.class);
        }

        @Override
        public void serialize(@Nonnull final JsonObject json, @Nonnull final LootFunctionTemplate type, @Nonnull final JsonSerializationContext ctx) {
            json.addProperty("name", Objects.toString(type.template.getRegistryName()));
        }

        @Nonnull
        @Override
        public LootFunctionTemplate deserialize(@Nonnull final JsonObject json, @Nonnull final JsonDeserializationContext ctx, @Nonnull final LootCondition[] conditionsIn) {
            @Nullable final SmithingTemplate template = SmithingTemplate.REGISTRY.getValue(new ResourceLocation(JsonUtils.getString(json, "name")));
            if(template == null) throw new JsonSyntaxException("Expected name to be a smithing template, was unknown string '" + json.getAsJsonPrimitive("name").getAsString() + "'");
            else return new LootFunctionTemplate(template, conditionsIn);
        }
    }
}
