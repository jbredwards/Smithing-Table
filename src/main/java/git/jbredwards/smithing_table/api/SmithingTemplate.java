package git.jbredwards.smithing_table.api;

import git.jbredwards.smithing_table.mod.SmithingTable;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 *
 * @since 1.0.0
 * @author jbred
 *
 */
public interface SmithingTemplate extends IForgeRegistryEntry<SmithingTemplate>
{
    /**
     * Holds all smithing templates. <b>This field cannot be initialized before fml pre-init!</b>
     * <br> This registry is not an {@link net.minecraftforge.registries.IForgeRegistryModifiable}.
     * @since 1.0.0
     */
    @Nonnull
    IForgeRegistry<SmithingTemplate> REGISTRY = Objects.requireNonNull(GameRegistry.findRegistry(SmithingTemplate.class), "Registry was loaded too early!");

    /**
     * @return This smithing template as an ItemStack.
     * @since 1.0.0
     */
    @Nonnull
    default ItemStack serialize() {
        @Nonnull final ItemStack stack = new ItemStack(SmithingContent.SMITHING_TEMPLATE);
        stack.getOrCreateSubCompound(SmithingTable.MOD_ID).setString("TemplateId", Objects.toString(getRegistryName()));
        return stack;
    }

    /**
     * @return A smithing template from an ItemStack.
     * @throws NullPointerException If any parameters are null.
     * @since 1.0.0
     */
    @Nullable
    static SmithingTemplate deserialize(@Nonnull final ItemStack stack) {
        return REGISTRY.getValue(new ResourceLocation(stack.getOrCreateSubCompound(SmithingTable.MOD_ID).getString("TemplateId")));
    }

    /**
     * @return A new smithing template ready to be registered.
     * @throws NullPointerException If any parameters are null.
     * @since 1.0.0
     */
    @Nonnull
    static SmithingTemplate create(@Nonnull final ResourceLocation id, @Nonnull final ModelResourceLocation model) {
        return new Impl(model).setRegistryName(id);
    }

    /**
     * @return A new smithing template ready to be registered,
     * with an auto-generated item model using the provided texture.
     * @throws NullPointerException If any parameters are null.
     * @since 1.0.0
     */
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
     * @since 1.0.0
     */
    @Nonnull
    static SmithingTemplate create(@Nonnull final ResourceLocation id) {
        return create(id, new ResourceLocation(id.getNamespace(), "smithing_templates/" + id.getPath()));
    }

    /**
     * @return The model location used by this smithing template.
     * @since 1.0.0
     */
    @Nonnull
    ModelResourceLocation getModelLocation();

    /**
     * @return The creative tabs for this smithing template.
     * @since 1.0.0
     */
    @Nullable
    CreativeTabs[] getCreativeTabs();

    /**
     * Setter for {@link SmithingTemplate#getCreativeTabs()}.
     * @since 1.0.0
     */
    void setCreativeTabs(@Nullable final CreativeTabs[] tabs);

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

        public Impl(@Nonnull final ModelResourceLocation modelIn) {
            model = modelIn;
        }

        @Nonnull
        @Override
        public ModelResourceLocation getModelLocation() {
            return model;
        }

        @Nullable
        @Override
        public CreativeTabs[] getCreativeTabs() {
            return creativeTabs;
        }

        @Override
        public void setCreativeTabs(@Nullable final CreativeTabs[] tabs) {
            creativeTabs = tabs;
        }
    }
}
