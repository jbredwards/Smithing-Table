package git.jbredwards.smithing_table.api;

import git.jbredwards.smithing_table.mod.SmithingTable;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 *
 * @author jbred
 *
 */
@ApiStatus.AvailableSince("1.0.0")
public interface SmithingTemplate extends IForgeRegistryEntry<SmithingTemplate>
{
    /**
     * Holds all smithing templates. <b>This field cannot be initialized before fml pre-init!</b>
     * <br> This registry is not an {@link net.minecraftforge.registries.IForgeRegistryModifiable}.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    IForgeRegistry<SmithingTemplate> REGISTRY = Objects.requireNonNull(GameRegistry.findRegistry(SmithingTemplate.class), "Registry was loaded too early!");

    /**
     * @return This smithing template as an ItemStack.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    default ItemStack serialize() {
        @Nonnull final ItemStack stack = new ItemStack(SmithingContent.SMITHING_TEMPLATE);
        stack.getOrCreateSubCompound(SmithingTable.MOD_ID).setString("TemplateId", Objects.toString(getRegistryName()));
        return stack;
    }

    /**
     * @return A smithing template from an ItemStack.
     * @throws NullPointerException If any parameters are null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nullable
    static SmithingTemplate deserialize(@Nonnull final ItemStack stack) {
        return REGISTRY.getValue(new ResourceLocation(stack.getOrCreateSubCompound(SmithingTable.MOD_ID).getString("TemplateId")));
    }

    /**
     * @return A new smithing template ready to be registered.
     * @throws NullPointerException If any parameters are null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    static SmithingTemplate create(@Nonnull final ResourceLocation id, @Nonnull final ModelResourceLocation model) {
        return new Impl(model).setRegistryName(id);
    }

    /**
     * @return A new smithing template ready to be registered,
     * with an auto-generated item model using the provided texture.
     * @throws NullPointerException If any parameters are null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    static SmithingTemplate create(@Nonnull final ResourceLocation id, @Nonnull final ResourceLocation texture) {
        if(texture instanceof ModelResourceLocation) return create(id, (ModelResourceLocation)texture);
        SmithingContent.GENERATE.add(texture); // Register texture for model generation.
        return create(id, new ModelResourceLocation(texture, "builtin/generated"));
    }

    /**
     * @return A new smithing template ready to be registered,
     * with an auto-generated item model using a texture determined by the provided registry id.
     * @throws NullPointerException If any parameters are null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    static SmithingTemplate create(@Nonnull final ResourceLocation id) {
        return create(id, new ResourceLocation(id.getNamespace(), "smithing_templates/" + id.getPath()));
    }

    /**
     * @return The model location used by this smithing template.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nonnull
    ModelResourceLocation getModelLocation();

    /**
     * @return The creative tabs for this smithing template.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nullable
    CreativeTabs[] getCreativeTabs();

    /**
     * Setter for {@link SmithingTemplate#getCreativeTabs()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    void setCreativeTabs(@Nullable final CreativeTabs[] tabs);

    /**
     * @return True if this always has an enchantment glint.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    @SideOnly(Side.CLIENT)
    boolean hasEffect();

    /**
     * Setter for {@link SmithingTemplate#hasEffect()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    void setEffect(final boolean hasEffect);

    /**
     * Basic {@link SmithingTemplate} implementation.
     *
     * @since 1.0.0
     * @author jbred
     */
    class Impl extends IForgeRegistryEntry.Impl<SmithingTemplate> implements SmithingTemplate
    {
        @Nonnull protected final ModelResourceLocation model;
        @Nullable protected CreativeTabs[] creativeTabs;
        protected boolean hasEffect;

        public Impl(@Nonnull final ModelResourceLocation model) {
            this.model = model;
        }

        @Nonnull
        @Override
        public ModelResourceLocation getModelLocation() {
            return this.model;
        }

        @Nullable
        @Override
        public CreativeTabs[] getCreativeTabs() {
            return this.creativeTabs;
        }

        @Override
        public void setCreativeTabs(@Nullable final CreativeTabs[] tabs) {
            this.creativeTabs = tabs;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public boolean hasEffect() {
            return this.hasEffect;
        }

        @Override
        public void setEffect(final boolean hasEffect) {
            this.hasEffect = hasEffect;
        }
    }
}
