package git.jbredwards.smithing_table.api;

import git.jbredwards.smithing_table.mod.SmithingTable;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A convenient way to register custom smithing templates.
 *
 * @see SmithingTemplateIngredient
 * @author jbred
 *
 */
@ApiStatus.AvailableSince("1.0.0")
public class SmithingTemplate extends IForgeRegistryEntry.Impl<SmithingTemplate>
{
    /**
     * Holds all smithing templates. <b>This field cannot be initialized before fml pre-init!</b>
     * <br> This registry is not an {@link net.minecraftforge.registries.IForgeRegistryModifiable}.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    public static final IForgeRegistry<SmithingTemplate> REGISTRY = Objects.requireNonNull(GameRegistry.findRegistry(SmithingTemplate.class), "Registry was loaded too early!");

    /**
     * Gui template slot texture. This texture is stitched to other non-duplicate template slot textures.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    public final ResourceLocation templateSlotTexture;

    /**
     * Gui render info for the equipment slot, while this smithing template is in the template slot.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nullable
    public SmithingSlotInfo equipmentSlotInfo = null;

    /**
     * Gui render info for the material slot, while this smithing template is in the template slot.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nullable
    public SmithingSlotInfo materialSlotInfo = null;

    /**
     * The creative tabs for this smithing template.
     * <br> Note: This smithing template will always appear in the "Smitning Table" and "Search" creative tabs.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nullable
    public CreativeTabs[] creativeTabs = null;

    /**
     * True if this always has an enchantment glint.
     */
    @ApiStatus.AvailableSince("1.0.0")
    public boolean forceEnchantGlint = false;

    /**
     * The maximum item durability of this smithing template.
     */
    @ApiStatus.AvailableSince("1.0.0")
    public int maxDurability = 0;

    /**
     * The maximum item stack size of this smithing template.
     */
    @ApiStatus.AvailableSince("1.0.0")
    public int maxStackSize = 64;

    /**
     * The item rarity for this smithing template.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    public IRarity rarity = EnumRarity.UNCOMMON;

    /**
     * The model location used by this smithing template.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    public final ModelResourceLocation model;

    /**
     * @param model The model location for this smithing template.
     * @param generateRegistryName True to generate a registry name from the model location.
     * @throws NullPointerException If model is null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    public SmithingTemplate(@Nonnull final ModelResourceLocation model, final boolean generateRegistryName) {
        this(model, SmithingSlotInfo.EMPTY_SLOT_TEMPLATE, generateRegistryName);
    }

    /**
     * @param model The model location for this smithing template.
     * @param templateSlotTexture Gui template slot texture.
     * @param generateRegistryName True to generate a registry name from the model location.
     * @throws NullPointerException If model is null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    public SmithingTemplate(@Nonnull final ModelResourceLocation model, @Nonnull final ResourceLocation templateSlotTexture, final boolean generateRegistryName) {
        if(generateRegistryName) {
            if(model.getVariant().equals("inventory")) this.setRegistryName(model);
            else this.setRegistryName(model.getNamespace(), model.getVariant());
        }

        this.model = Objects.requireNonNull(model);
        this.templateSlotTexture = Objects.requireNonNull(templateSlotTexture);
    }

    /**
     * @return This smithing template as an ItemStack.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    public final ItemStack serialize() {
        @Nonnull final ItemStack stack = new ItemStack(SmithingContent.SMITHING_TEMPLATE);
        stack.getOrCreateSubCompound(SmithingTable.MOD_ID).setString("TemplateId", Objects.toString(this.getRegistryName()));
        return stack;
    }

    /**
     * @return A smithing template from an ItemStack.
     * @throws NullPointerException If any parameters are null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nullable
    public static SmithingTemplate deserialize(@Nonnull final ItemStack stack) {
        @Nullable final NBTTagCompound nbt = stack.getSubCompound(SmithingTable.MOD_ID);
        return nbt == null ? null : REGISTRY.getValue(new ResourceLocation(nbt.getString("TemplateId")));
    }
}
