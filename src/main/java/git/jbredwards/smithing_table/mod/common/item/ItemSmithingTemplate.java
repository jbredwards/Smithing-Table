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

package git.jbredwards.smithing_table.mod.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import git.jbredwards.smithing_table.api.SmithingTemplate;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 *
 * @author jbred
 *
 */
public final class ItemSmithingTemplate extends Item
{
    @Nonnull
    private static final Multimap<CreativeTabs, SmithingTemplate> TAB_LOOKUP = HashMultimap.create();
    public static void initCreativeTabs() {
        TAB_LOOKUP.clear();
        SmithingTemplate.REGISTRY.forEach(template -> {
            @Nullable final CreativeTabs[] tabs = template.creativeTabs;
            if(tabs != null) for(@Nullable final CreativeTabs tab : tabs) if(tab != null) TAB_LOOKUP.put(tab, template);
        });
    }

    @Override
    public void getSubItems(@Nonnull final CreativeTabs tab, @Nonnull final NonNullList<ItemStack> items) {
        if(tab == getCreativeTab() || tab == CreativeTabs.SEARCH)
            SmithingTemplate.REGISTRY.forEach(template -> items.add(template.serialize()));
        else TAB_LOOKUP.get(tab).forEach(template -> items.add(template.serialize()));
    }

    @Nullable
    @Override
    public String getCreatorModId(@Nonnull final ItemStack stack) {
        @Nullable final SmithingTemplate template = SmithingTemplate.deserialize(stack);
        if(template != null) {
            @Nullable final ResourceLocation loc = template.getRegistryName();
            if(loc != null) return loc.getNamespace();
        }

        return super.getCreatorModId(stack);
    }

    @Nonnull
    @Override
    public CreativeTabs[] getCreativeTabs() {
        return TAB_LOOKUP.keySet().toArray(new CreativeTabs[0]);
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(@Nonnull final ItemStack stack) {
        @Nonnull final IRarity rarity = getForgeRarity(stack);
        return rarity instanceof EnumRarity ? (EnumRarity)rarity: EnumRarity.UNCOMMON;
    }

    @Nonnull
    @Override
    public IRarity getForgeRarity(@Nonnull final ItemStack stack) {
        @Nullable final SmithingTemplate template = SmithingTemplate.deserialize(stack);
        return template != null ? template.rarity : EnumRarity.UNCOMMON;
    }

    @Nonnull
    @Override
    public String getTranslationKey(@Nonnull final ItemStack stack) {
        @Nullable final SmithingTemplate template = SmithingTemplate.deserialize(stack);
        return template != null ? "smithing_template." + template.getRegistryName().getNamespace() + '.' + template.getRegistryName().getPath() : getTranslationKey();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull final ItemStack stack, @Nullable final World worldIn, @Nonnull final List<String> tooltip, @Nonnull final ITooltipFlag flagIn) {
        @Nonnull final String tooltipKey = getTranslationKey(stack) + ".tooltip";
        if(I18n.hasKey(tooltipKey)) for(@Nonnull final String line : I18n.format(tooltipKey).split("\\n")) tooltip.add(line.trim());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(@Nonnull final ItemStack stack) {
        if(super.hasEffect(stack)) return true; // Always apply enchantment glint if this has enchantments.
        @Nullable final SmithingTemplate template = SmithingTemplate.deserialize(stack);
        return template != null && template.forceEnchantGlint;
    }

    @Override
    public int getMaxDamage(@Nonnull final ItemStack stack) {
        @Nullable final SmithingTemplate template = SmithingTemplate.deserialize(stack);
        return template != null ? Math.max(0, template.maxDurability) : super.getMaxDamage(stack);
    }

    @Override
    public int getItemStackLimit(@Nonnull final ItemStack stack) {
        @Nullable final SmithingTemplate template = SmithingTemplate.deserialize(stack);
        if(template == null) return super.getItemStackLimit(stack);
        else if(template.maxDurability > 0 || super.getMaxDamage(stack) != 0) return 1;
        else return Math.min(super.getItemStackLimit(stack), template.maxStackSize);
    }
}
