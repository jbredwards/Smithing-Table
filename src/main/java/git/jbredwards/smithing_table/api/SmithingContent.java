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

import git.jbredwards.smithing_table.mod.SmithingTable;
import git.jbredwards.smithing_table.mod.common.block.BlockSmithingTable;
import git.jbredwards.smithing_table.mod.common.item.ItemSmithingTemplate;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;

/**
 * All content added by this mod.
 *
 * @author jbred
 *
 */
@ApiStatus.AvailableSince("1.0.0")
public final class SmithingContent
{
    /**
     * The sound played when a smithing table smiths something.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    public static final SoundEvent BLOCK_SMITHING_TABLE_USE = new SoundEvent(new ResourceLocation(SmithingTable.MOD_ID, "blocks.smithing_table.use"));

    /**
     * The smithing table block instance.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    public static final Block SMITHING_TABLE = new BlockSmithingTable(Material.WOOD, MapColor.BROWN_STAINED_HARDENED_CLAY);

    /**
     * The smithing template item instance.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    public static final Item SMITHING_TEMPLATE = new ItemSmithingTemplate();

    /**
     * A searchable creative tab that holds all smithing templates and smithing tables.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(SmithingTable.MOD_ID + ":tab") {
        @Override
        public boolean hasSearchBar() {
            return true;
        }

        @Nonnull
        @SideOnly(Side.CLIENT)
        @Override
        public ItemStack createIcon() {
            return Item.getItemFromBlock(SmithingContent.SMITHING_TABLE).getDefaultInstance();
        }
    }.setBackgroundImageName("item_search.png");
}
