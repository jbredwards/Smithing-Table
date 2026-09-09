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

import com.google.common.collect.ImmutableList;
import git.jbredwards.smithing_table.mod.SmithingTable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Used by the smithing table gui to render extra recipe information.
 * @author jbred
 *
 */
@ApiStatus.AvailableSince("1.0.0")
public interface SmithingSlotInfo
{
    /**
     * Inventory slot index for the template item slot in a smithing table.
     */
    @ApiStatus.AvailableSince("1.0.0")
    int TEMPLATE = 0;

    /**
     * Inventory slot index for the equipment item slot in a smithing table.
     */
    @ApiStatus.AvailableSince("1.0.0")
    int EQUIPMENT = 1;

    /**
     * Inventory slot index for the material item slot in a smithing table.
     */
    @ApiStatus.AvailableSince("1.0.0")
    int MATERIAL = 2;

    /**
     * Inventory slot index for the output item slot in a smithing table.
     */
    @ApiStatus.AvailableSince("1.0.0")
    int OUTPUT = 3;

    /**
     * Empty template texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_TEMPLATE = new ResourceLocation(SmithingTable.MOD_ID, "gui/slot/template");

    /**
     * Empty helmet texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_HELMET = new ResourceLocation("items/empty_armor_slot_helmet");

    /**
     * Empty chestplate texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_CHESTPLATE = new ResourceLocation("items/empty_armor_slot_chestplate");

    /**
     * Empty leggings texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_LEGGINGS = new ResourceLocation("items/empty_armor_slot_leggings");

    /**
     * Empty boots texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_BOOTS = new ResourceLocation("items/empty_armor_slot_boots");

    /**
     * Empty shield texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_SHIELD = new ResourceLocation("items/empty_armor_slot_shield");

    /**
     * Empty axe texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_AXE = new ResourceLocation(SmithingTable.MOD_ID, "gui/slot/tool_axe");

    /**
     * Empty hoe texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_HOE = new ResourceLocation(SmithingTable.MOD_ID, "gui/slot/tool_hoe");

    /**
     * Empty pickaxe texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_PICKAXE = new ResourceLocation(SmithingTable.MOD_ID, "gui/slot/tool_pickaxe");

    /**
     * Empty shovel texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_SHOVEL = new ResourceLocation(SmithingTable.MOD_ID, "gui/slot/tool_shovel");

    /**
     * Empty sword texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_SWORD = new ResourceLocation(SmithingTable.MOD_ID, "gui/slot/tool_sword");

    /**
     * Empty ingot texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_INGOT = new ResourceLocation(SmithingTable.MOD_ID, "gui/slot/ingot");

    /**
     * Empty dust texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_DUST = new ResourceLocation(SmithingTable.MOD_ID, "gui/slot/dust");

    /**
     * Empty diamond gem texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_GEM_DIAMOND = new ResourceLocation(SmithingTable.MOD_ID, "gui/slot/gem_diamond");

    /**
     * Empty emerald gem texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_GEM_EMERALD = new ResourceLocation(SmithingTable.MOD_ID, "gui/slot/gem_emerald");

    /**
     * Empty lapis gem texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_GEM_LAPIS = new ResourceLocation(SmithingTable.MOD_ID, "gui/slot/gem_lapis");

    /**
     * Empty quartz gem texture location.
     * @see SmithingSlotInfo#textures()
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ResourceLocation EMPTY_SLOT_GEM_QUARTZ = new ResourceLocation(SmithingTable.MOD_ID, "gui/slot/gem_quartz");

    /**
     * Empty equipment texture locations.
     * @see SmithingSlotInfo#textures()
     */
    @Nonnull
    List<ResourceLocation> EMPTY_SLOT_EQUIPMENT = ImmutableList.of(EMPTY_SLOT_HELMET, EMPTY_SLOT_SWORD, EMPTY_SLOT_CHESTPLATE, EMPTY_SLOT_PICKAXE, EMPTY_SLOT_LEGGINGS, EMPTY_SLOT_AXE, EMPTY_SLOT_BOOTS, EMPTY_SLOT_HOE, EMPTY_SLOT_SHOVEL);

    /**
     * @return The tooltip to be rendered (while no item is in the slot).
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ITextComponent tooltip();

    /**
     * @return The textures to be rendered over the slot (while no item is in the slot).
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    Set<ResourceLocation> textures();

    /**
     * May be useful for anyone that adds recipes for a new tool type or a new weapon type.
     * @param textures The slot textures to add.
     * @throws NullPointerException If textures is null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    default void addTextures(@Nonnull final Collection<ResourceLocation> textures) {
        textures().addAll(textures);
    }

    /**
     * Default {@code SmithingSlotInfo} implementation.
     * @author jbred
     *
     */
    @ApiStatus.AvailableSince("1.0.0")
    class Impl implements SmithingSlotInfo
    {
        @ApiStatus.Internal @Nonnull private final ITextComponent tooltip;
        @ApiStatus.Internal @Nonnull private final Set<ResourceLocation> textures;

        @ApiStatus.AvailableSince("1.0.0")
        public Impl(@Nonnull final ITextComponent tooltip, @Nonnull final ResourceLocation... textures) {
            this(tooltip, Arrays.asList(Objects.requireNonNull(textures)));
        }

        @ApiStatus.AvailableSince("1.0.0")
        public Impl(@Nonnull final ITextComponent tooltip, @Nonnull final Collection<ResourceLocation> textures) {
            this.tooltip = Objects.requireNonNull(tooltip);
            this.textures = new LinkedHashSet<>(Objects.requireNonNull(textures));
        }

        @Nonnull
        @Override
        public ITextComponent tooltip() {
            return this.tooltip;
        }

        @Nonnull
        @Override
        public Set<ResourceLocation> textures() {
            return this.textures;
        }
    }
}
