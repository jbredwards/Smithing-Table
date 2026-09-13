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

package git.jbredwards.smithing_table.mod.common.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.creativetabs.ICreativeTab;
import crafttweaker.api.minecraft.CraftTweakerMC;
import git.jbredwards.smithing_table.api.SmithingSlotInfo;
import git.jbredwards.smithing_table.api.SmithingTemplate;
import git.jbredwards.smithing_table.mod.SmithingTable;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.ApiStatus;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

/**
 *
 * @author jbred
 *
 */
@ZenRegister
@ZenClass("mods." + SmithingTable.MOD_ID + ".templates")
public final class CRTSmithingTemplates
{
    @Nonnull
    private static final Map<String, SmithingTemplate> TEMPLATES = new Object2ObjectLinkedOpenHashMap<>();
    private static boolean INITIALISED;

    @ApiStatus.Internal
    public static void registerTemplates(@Nonnull final IForgeRegistry<SmithingTemplate> registry) {
        for(@Nonnull final SmithingTemplate template : TEMPLATES.values()) {
            registry.register(template);
            // TODO: Look into how CrT handles its resources.
        }

        INITIALISED = true;
    }

    @ZenMethod
    public static Builder builder() {
        return new Builder();
    }

    @ZenClass
    public static class Builder
    {
        @Nonnull private final SmithingTemplate propertyHolder = new SmithingTemplate(new ModelResourceLocation("missingno"), false);
        @Nullable private ResourceLocation guiSlotTex = SmithingSlotInfo.EMPTY_SLOT_TEMPLATE;
        @Nullable private ModelResourceLocation model;
        @Nullable private ResourceLocation name;

        @Nonnull
        @ZenMethod
        public Builder creativeTabs(@Nullable final ICreativeTab[] tabs) {
            propertyHolder.creativeTabs = tabs == null || tabs.length == 0 ? null :
                    Arrays.stream(tabs).map(CraftTweakerMC::getCreativeTabs).toArray(CreativeTabs[]::new);
            return this;
        }
        
        /*
        // CrT doesn't allow for custom ingredients, and its nbt ingredient doesn't allow for "anyDamage()".
        // To work around this, apply the smithing template nbt to a generic custom item, and give that durability.
        // The smithing table gui will read any smithing template instance from the nbt!
        @Nonnull
        @ZenMethod
        public Builder durability(final int durability) {
            propertyHolder.maxDurability = durability;
            return this;
        }
        */

        @Nonnull
        @ZenMethod
        public Builder enchantedEffect() {
            propertyHolder.forceEnchantGlint = true;
            return this;
        }

        @Nonnull
        @ZenMethod
        public Builder stackSize(final int stackSize) {
            propertyHolder.maxStackSize = stackSize;
            return this;
        }

        /**
         * Looks like CrT can only use EnumRarity?
         * <a href="https://docs.blamejared.com/1.12/en/Mods/ContentTweaker/Vanilla/Creatable_Content/Item/#zenproperties">Source</a>
         */
        @Nonnull
        @ZenMethod
        public Builder rarity(@Nonnull final EnumRarity rarity) {
            propertyHolder.rarity = rarity;
            return this;
        }

        @Nonnull
        @ZenMethod
        public Builder templateSlotTexture(@Nonnull final String texture) {
            guiSlotTex = new ResourceLocation(texture);
            return this;
        }
        
        @Nonnull
        @ZenMethod
        public Builder equipmentSlotInfo(@Nonnull final String hoverText, @Nonnull final String[] textures) {
            propertyHolder.equipmentSlotInfo = new SmithingSlotInfo.Impl(new TextComponentTranslation(hoverText),
                    Arrays.stream(textures).map(ResourceLocation::new).toArray(ResourceLocation[]::new));
            return this;
        }

        @Nonnull
        @ZenMethod
        public Builder materialSlotInfo(@Nonnull final String hoverText, @Nonnull final String[] textures) {
            propertyHolder.materialSlotInfo = new SmithingSlotInfo.Impl(new TextComponentTranslation(hoverText),
                    Arrays.stream(textures).map(ResourceLocation::new).toArray(ResourceLocation[]::new));
            return this;
        }

        @Nonnull
        @ZenMethod
        public Builder model(@Nonnull final String location) {
            model = location.indexOf('#') != -1 ? new ModelResourceLocation(location) : new ModelResourceLocation(location, "inventory");
            return this;
        }

        @Nonnull
        @ZenMethod
        public Builder name(@Nonnull final String path) {
            name = new ResourceLocation("crafttweaker", path);
            return this;
        }

        @ZenMethod
        public boolean validate() {
            if(propertyHolder.maxDurability < 0 || propertyHolder.maxDurability >= OreDictionary.WILDCARD_VALUE) return error(
                    "Must have 0 - " + OreDictionary.WILDCARD_VALUE + " durability, but found " + propertyHolder.maxDurability);
            if(propertyHolder.maxStackSize < 0 || propertyHolder.maxStackSize > 64) return error(
                    "Must have 0 - " + OreDictionary.WILDCARD_VALUE + " stack size, but found " + propertyHolder.maxStackSize);
            if(propertyHolder.rarity == null) return error("Rarity cannot be null");
            if(guiSlotTex == null) return error("Template slot texture cannot be null");

            final boolean missingModel = model == null;
            final boolean missingName = name == null;
            
            if(missingModel && missingName) return error("Must have a model or name");
            else if(missingModel) model = new ModelResourceLocation(name, "inventory");
            else if(missingName) name = new ResourceLocation("crafttweaker", model.getPath());

            if(INITIALISED) return error("Smithing templates must registered in preInit. Tried to register " + name + " too late!");
            else if(TEMPLATES.containsKey(name.getPath())) return error("The registry name of " + name + " must not match an already registered smithing template!");
            return true;
        }

        protected static boolean error(@Nonnull final String msg) {
            CraftTweakerAPI.logError(msg);
            return false;
        }

        @ZenMethod
        public void register() {
            if(!validate()) return;

            @Nonnull final SmithingTemplate template = build().setRegistryName(name);
            template.creativeTabs = propertyHolder.creativeTabs;
            template.forceEnchantGlint = propertyHolder.forceEnchantGlint;
            template.maxDurability = propertyHolder.maxDurability;
            template.maxStackSize = propertyHolder.maxStackSize;
            template.rarity = propertyHolder.rarity;
            template.equipmentSlotInfo = propertyHolder.equipmentSlotInfo;
            template.materialSlotInfo = propertyHolder.materialSlotInfo;

            TEMPLATES.put(name.getPath(), template);
        }

        @Nonnull
        @ApiStatus.OverrideOnly
        protected SmithingTemplate build() {
            return new SmithingTemplate(model, guiSlotTex, false);
        }
    }
}
