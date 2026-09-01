package git.jbredwards.smithing_table.mod.common.item;

import git.jbredwards.smithing_table.mod.SmithingTable;
import git.jbredwards.smithing_table.mod.common.block.TableData;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author jbred
 *
 */
public class ItemSmithingTable extends ItemBlock
{
    public ItemSmithingTable(@Nonnull final Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull final ItemStack stack) {
        @Nonnull final TableData variant = getVariant(stack);
        @Nonnull final String root = stack.getTranslationKey();
        // Use special name, if present.
        @Nonnull final String variantKey = root +
                '.' + Objects.toString(variant.item.getRegistryName()).replace(':', '.') +
                '.' + variant.meta + ".name";
        if(I18n.canTranslate(variantKey)) return I18n.translateToLocal(variantKey);
        // Generate name using regex.
        else return I18n.translateToLocalFormatted(root + ".parts",
                TextFormatting.getTextWithoutFormattingCodes(new ItemStack(variant.item, 1, variant.meta).getDisplayName())
                .replaceAll(I18n.translateToLocal(root + ".regex"), "").trim(), super.getItemStackDisplayName(stack));
    }

    @Nonnull
    public static Set<TableData> getVariants() {
        @Nonnull final Set<TableData> variants = new LinkedHashSet<>();
        variants.add(TableData.DEFAULT);

        for(@Nonnull final ItemStack ore : OreDictionary.getOres("plankWood", false)) {
            @Nonnull final NonNullList<ItemStack> subItems = NonNullList.create();

            if(ore.getMetadata() != OreDictionary.WILDCARD_VALUE) subItems.add(ore);
            else ore.getItem().getSubItems(CreativeTabs.SEARCH, subItems);

            for(@Nonnull final ItemStack item : subItems) {
                @Nullable final Block block = Block.getBlockFromItem(ore.getItem());
                if(block != null && block != Blocks.AIR) variants.add(new TableData(item.getItem(), item.getMetadata()));
            }
        }

        return variants;
    }

    @Nonnull
    public static TableData getVariant(@Nonnull final ItemStack stack) {
        if(stack.hasTagCompound() && stack.getTagCompound().hasKey(SmithingTable.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
            return TableData.deserialize(stack.getTagCompound().getCompoundTag(SmithingTable.MOD_ID));
        }

        return TableData.DEFAULT;
    }

    @Nonnull
    public static ItemStack setVariant(@Nonnull final ItemStack stack, @Nonnull final TableData variant) {
        // Ensure the default case has no NBT, so items will always stack in-game.
        if(TableData.DEFAULT.equals(variant)) {
            stack.removeSubCompound(SmithingTable.MOD_ID);
            if(stack.hasTagCompound() && stack.getTagCompound().isEmpty()) stack.setTagCompound(null);
            return stack;
        }

        stack.setTagInfo(SmithingTable.MOD_ID, variant.serializeNBT());
        return stack;
    }

    @Nonnull
    public static ItemStack setVariant(@Nonnull final ItemStack stack, @Nonnull final ItemStack variant) {
        return setVariant(stack, new TableData(variant.getItem(), variant.getMetadata()));
    }
}
