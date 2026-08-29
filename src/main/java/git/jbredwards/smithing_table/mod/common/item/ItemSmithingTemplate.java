package git.jbredwards.smithing_table.mod.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import git.jbredwards.smithing_table.api.SmithingTemplate;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
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
            @Nullable final CreativeTabs[] tabs = template.getCreativeTabs();
            if(tabs != null) for(@Nonnull final CreativeTabs tab : tabs) TAB_LOOKUP.put(tab, template);
        });
    }

    @Override
    public void getSubItems(@Nonnull final CreativeTabs tab, @Nonnull final NonNullList<ItemStack> items) {
        if(tab == getCreativeTab() || tab == CreativeTabs.SEARCH)
            SmithingTemplate.REGISTRY.forEach(template -> items.add(template.serialize()));
        else TAB_LOOKUP.get(tab).forEach(template -> items.add(template.serialize()));
    }

    @Nonnull
    @Override
    public CreativeTabs[] getCreativeTabs() {
        return TAB_LOOKUP.keySet().toArray(new CreativeTabs[0]);
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
}
