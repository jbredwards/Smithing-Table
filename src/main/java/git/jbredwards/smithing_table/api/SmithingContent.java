package git.jbredwards.smithing_table.api;

import com.google.common.collect.ImmutableList;
import git.jbredwards.smithing_table.mod.SmithingTable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ForgeBlockStateV1;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * All content added by this mod. Fields are populated during Forge registry events.
 *
 * @since 1.0.0
 * @author jbred
 *
 */
@GameRegistry.ObjectHolder(SmithingTable.MOD_ID)
public final class SmithingContent
{
    @GameRegistry.ObjectHolder("blocks.smithing_table.use")
    public static final SoundEvent BLOCK_SMITHING_TABLE_USE = holder();

    @GameRegistry.ObjectHolder("table")
    public static final Block SMITHING_TABLE = holder();

    @GameRegistry.ObjectHolder("template")
    public static final Item SMITHING_TEMPLATE = holder();

    @Nonnull
    public static CreativeTabs CREATIVE_TAB = new CreativeTabs(SmithingTable.MOD_ID + ":tab") {
        @Nonnull
        @SideOnly(Side.CLIENT)
        @Override
        public ItemStack createIcon() { return new ItemStack(SMITHING_TABLE); }

        @Override
        public boolean hasSearchBar() { return true; }
    }.setBackgroundImageName("item_search.png");

    /**
     * Internal. Holds all textures without a dedicated model.
     */
    @Nonnull
    static Set<ResourceLocation> GENERATE = new HashSet<>();

    /**
     * Internal. Registers all textures without a dedicated model.
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    static void registerGeneratedTextures(@Nonnull final TextureStitchEvent.Pre event) {
        GENERATE.forEach(event.getMap()::registerSprite);
    }

    /**
     * Internal. Generates and registers models for all textures without a dedicated model.
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    static void registerGeneratedModels(@Nonnull final ModelBakeEvent event) {
        @Nonnull final IModelState state = ForgeBlockStateV1.Transforms.get("forge:default-item").orElseGet(TRSRTransformation::identity);
        GENERATE.forEach(texture -> event.getModelRegistry().putObject(new ModelResourceLocation(texture, "builtin/generated"),
                new ItemLayerModel(ImmutableList.of(texture)).bake(state, DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter())));
    }

    /**
     * Internal. Tricks Intellij into not showing warnings when setting final fields to null.
     */
    @Nonnull
    static <T> T holder() { return null; }
}
