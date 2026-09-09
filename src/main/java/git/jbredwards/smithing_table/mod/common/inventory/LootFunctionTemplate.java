package git.jbredwards.smithing_table.mod.common.inventory;

import com.google.gson.*;
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
