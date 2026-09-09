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

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.mapper.ObjectMappers;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import com.cleanroommc.groovyscript.sandbox.FileUtil;
import com.google.gson.JsonParser;
import git.jbredwards.smithing_table.api.SmithingSlotInfo;
import git.jbredwards.smithing_table.api.SmithingTemplate;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author jbred
 *
 */
public class GRSSmithingTemplates extends NamedRegistry
{
    @Nonnull
    @GroovyBlacklist
    public static final GRSSmithingTemplates INSTANCE = new GRSSmithingTemplates();

    @Nonnull
    private static final Map<String, SmithingTemplate> TEMPLATES = new Object2ObjectLinkedOpenHashMap<>();
    private static boolean INITIALISED;

    @GroovyBlacklist
    public GRSSmithingTemplates() {
        super(Collections.singleton("templates"));
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public static void registerTemplates(@Nonnull final IForgeRegistry<SmithingTemplate> registry) {
        for(@Nonnull final SmithingTemplate template : TEMPLATES.values()) {
            registry.register(template);

            @Nonnull final ModelResourceLocation loc = template.model;
            if(template.model.getVariant().equals("inventory")) {
                @Nonnull final File modelFile = FileUtil.makeFile(GroovyScript.getResourcesFile().getPath(), loc.getNamespace(), "blockstates", loc.getPath() + ".json");
                if(!modelFile.exists()) JsonHelper.saveJson(modelFile, new JsonParser().parse(
                        "{" +
                        "\"forge_marker\": 1," +
                        "\"variants\": {" +
                        "\"inventory\":[" +
                        "{" +
                        "\"model\": \"forge:item-layer\"," +
                        "\"transform\": \"forge:default-item\"," +
                        "\"textures\": {" +
                        "\"layer0\": \"" + loc.getNamespace() + ":items/" + loc.getPath() + '"' +
                        "}" +
                        "}" +
                        "]" +
                        "}" +
                        "}"));
            }
        }

        INITIALISED = true;
    }

    @GroovyBlacklist
    public static void register(@Nonnull final SmithingTemplate template) {
        if(INITIALISED) {
            GroovyLog.get().errorMC("Smithing templates must registered in preInit. Tried to register {} too late!", template.getRegistryName());
            return;
        }

        @Nullable final ResourceLocation key = template.getRegistryName();
        if(key == null || TEMPLATES.containsKey(key.getPath())) {
            GroovyLog.get().exception(new IllegalArgumentException("The registry name of the smithing template must be non-null and not match an already registered smithing template!"));
            return;
        }

        TEMPLATES.put(key.getPath(), template);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void register(@Nullable final String name, @Nonnull final SmithingTemplate template) {
        if(name != null) template.setRegistryName(GroovyScript.getRunConfig().getPackId(), name);
        else if(template.getRegistryName() == null) {
            GroovyLog.get().errorMC("Can't register smithing template without a name!");
            return;
        }

        register(template);
    }

    @Nonnull
    public Builder builder() {
        return new Builder();
    }

    public class Builder implements IRecipeBuilder<SmithingTemplate>
    {
        @Nonnull private final SmithingTemplate propertyHolder = new SmithingTemplate(new ModelResourceLocation("missingno"), false);
        @Nullable private ResourceLocation guiSlotTex = SmithingSlotInfo.EMPTY_SLOT_TEMPLATE;
        @Nullable private ModelResourceLocation model;
        @Nullable private ResourceLocation name;

        @Nonnull
        @RecipeBuilderMethodDescription(field = "creative tabs")
        public Builder creativeTabs(@Nullable final CreativeTabs... tabs) {
            propertyHolder.creativeTabs = tabs;
            return this;
        }

        @Nonnull
        @RecipeBuilderMethodDescription(field = "durability")
        public Builder durability(final int durability) {
            propertyHolder.maxDurability = durability;
            return this;
        }

        @Nonnull
        @RecipeBuilderMethodDescription(field = "effect")
        public Builder enchantedEffect() {
            propertyHolder.forceEnchantGlint = true;
            return this;
        }

        @Nonnull
        @RecipeBuilderMethodDescription(field = "stack size")
        public Builder stackSize(final int stackSize) {
            propertyHolder.maxStackSize = stackSize;
            return this;
        }

        @Nonnull
        @RecipeBuilderMethodDescription(field = "rarity")
        public Builder rarity(@Nonnull final IRarity rarity) {
            propertyHolder.rarity = rarity;
            return this;
        }

        @Nonnull
        @RecipeBuilderMethodDescription(field = "template slot texture")
        public Builder templateSlotTexture(@Nonnull final ResourceLocation texture) {
            guiSlotTex = texture;
            return this;
        }

        @Nonnull
        @RecipeBuilderMethodDescription(field = "equipment slot info")
        public Builder equipmentSlotInfo(@Nonnull final String hoverText, @Nonnull final Collection<ResourceLocation> textures) {
            propertyHolder.equipmentSlotInfo = new SmithingSlotInfo.Impl(new TextComponentTranslation(hoverText), textures);
            return this;
        }

        @Nonnull
        @RecipeBuilderMethodDescription(field = "material slot info")
        public Builder materialSlotInfo(@Nonnull final String hoverText, @Nonnull final Collection<ResourceLocation> textures) {
            propertyHolder.materialSlotInfo = new SmithingSlotInfo.Impl(new TextComponentTranslation(hoverText), textures);
            return this;
        }

        @Nonnull
        @RecipeBuilderMethodDescription(field = "model")
        public Builder model(@Nonnull final String model) {
            @Nonnull final Result<ResourceLocation> result = ObjectMappers.parseResourceLocation(model);
            if(result.hasError()) GroovyLog.get().error(result.getError());
            return model(result.hasError() ? new ResourceLocation(GroovyScript.getRunConfig().getPackId(), model) : result.getValue());
        }

        @Nonnull
        @RecipeBuilderMethodDescription(field = "model")
        public Builder model(@Nonnull final ResourceLocation model) {
            return model(model, "inventory");
        }

        @Nonnull
        @RecipeBuilderMethodDescription(field = "model")
        public Builder model(@Nonnull final ResourceLocation location, @Nonnull final String variant) {
            model = new ModelResourceLocation(location, variant);
            return this;
        }

        @Nonnull
        @RecipeBuilderMethodDescription(field = "name")
        public Builder name(@Nonnull final String path) {
            name = new ResourceLocation(GroovyScript.getRunConfig().getPackId(), path);
            return this;
        }

        @Override
        public boolean validate() {
            @Nonnull final GroovyLog.Msg msg = GroovyLog.msg("Error adding smithing template").error();
            validate(msg);
            return !msg.postIfNotEmpty();
        }

        @GroovyBlacklist
        @ApiStatus.OverrideOnly
        protected void validate(@Nonnull final GroovyLog.Msg msg) {
            AbstractRecipeBuilder.validateCustom(msg, propertyHolder.maxDurability, 0, OreDictionary.WILDCARD_VALUE - 1, "durability");
            AbstractRecipeBuilder.validateCustom(msg, propertyHolder.maxStackSize, 0, 64, "stack size");
            if(propertyHolder.rarity == null) msg.add("Rarity cannot be null");
            if(guiSlotTex == null) msg.add("Template slot texture cannot be null");

            final boolean missingModel = model == null;
            final boolean missingName = name == null;

            if(missingModel && missingName) msg.add("Must have a model or name");
            else if(missingModel) model = new ModelResourceLocation(name, "inventory");
            else if(missingName) name = new ResourceLocation(GroovyScript.getRunConfig().getPackId(), model.getPath());
        }

        @Nullable
        @Override
        @RecipeBuilderRegistrationMethod
        public SmithingTemplate register() {
            if(!validate()) return null;

            @Nonnull final SmithingTemplate template = build().setRegistryName(name);
            template.creativeTabs = propertyHolder.creativeTabs;
            template.forceEnchantGlint = propertyHolder.forceEnchantGlint;
            template.maxDurability = propertyHolder.maxDurability;
            template.maxStackSize = propertyHolder.maxStackSize;
            template.rarity = propertyHolder.rarity;
            template.equipmentSlotInfo = propertyHolder.equipmentSlotInfo;
            template.materialSlotInfo = propertyHolder.materialSlotInfo;

            GRSSmithingTemplates.this.register(null, template);
            return template;
        }

        @Nonnull
        @GroovyBlacklist
        @ApiStatus.OverrideOnly
        protected SmithingTemplate build() {
            return new SmithingTemplate(model, guiSlotTex, false);
        }
    }
}
