package git.jbredwards.smithing_table.mod.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;
import git.jbredwards.smithing_table.mod.SmithingTable;
import git.jbredwards.smithing_table.mod.common.block.TableData;
import git.jbredwards.smithing_table.mod.common.block.UnlistedVariantProperty;
import git.jbredwards.smithing_table.mod.common.item.ItemSmithingTable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author jbred
 *
 */
@SideOnly(Side.CLIENT)
public final class ModelSmithingTable implements IModel
{
    @Nonnull
    public static final ModelSmithingTable INSTANCE = new ModelSmithingTable(ModelLoader.MODEL_MISSING, ModelLoader.MODEL_MISSING, ModelLoader.MODEL_MISSING);

    @Nonnull
    public final ResourceLocation solid, tools, wood;
    public ModelSmithingTable(@Nonnull final ResourceLocation solidIn, @Nonnull final ResourceLocation toolsIn, @Nonnull final ResourceLocation woodIn) {
        solid = solidIn;
        tools = toolsIn;
        wood = woodIn;
    }

    @Nonnull
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.of(solid, tools, wood);
    }

    @Nonnull
    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public IBakedModel bake(@Nonnull final IModelState state, @Nonnull final VertexFormat format, @Nonnull final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new Baked(
                bakeModel(solid, state, format, bakedTextureGetter),
                bakeModel(tools, state, format, bakedTextureGetter),
                bakeModel(wood, state, format, bakedTextureGetter),
                PerspectiveMapWrapper.getTransforms(state), bakedTextureGetter, null);
    }

    @Nonnull
    private static IBakedModel bakeModel(@Nonnull final ResourceLocation location,
                                         @Nonnull final IModelState state, @Nonnull final VertexFormat format, @Nonnull final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        @Nonnull final IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Couldn't load ModelSmithingTable dependency: " + location);
        return model.bake(new ModelStateComposition(state, model.getDefaultState()), format, bakedTextureGetter);
    }

    @SideOnly(Side.CLIENT)
    public static final class Baked extends BakedModelWrapper<IBakedModel>
    {
        @Nonnull final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;
        @Nonnull final Function<ResourceLocation, TextureAtlasSprite> textureGetter;

        @Nonnull final IBakedModel tools, wood;
        @Nullable final TableData variant;

        public Baked(@Nonnull final IBakedModel solidIn, @Nonnull final IBakedModel toolsIn, @Nonnull final IBakedModel woodIn,
                     @Nonnull final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformsIn,
                     @Nonnull final Function<ResourceLocation, TextureAtlasSprite> textureGetterIn,
                     @Nullable final TableData variantIn) {
            super(solidIn);
            tools = toolsIn;
            wood = woodIn;
            transforms = transformsIn;
            textureGetter = textureGetterIn;
            variant = variantIn;
        }

        @Nonnull
        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(@Nonnull final ItemCameraTransforms.TransformType cameraTransformType) {
            return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType);
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable final IBlockState state, @Nullable final EnumFacing side, final long rand) {
            @Nonnull final ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
            @Nullable final BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();

            if(layer == null) {
                builder.addAll(originalModel.getQuads(state, side, rand));
                builder.addAll(tools.getQuads(state, side, rand));
                builder.add(Loader.INSTANCE.cache.getUnchecked(Triple.of(wood, side, getTrueVariant(state))));
            }

            else {
                if(layer == BlockRenderLayer.SOLID) builder.addAll(originalModel.getQuads(state, side, rand));
                else if(layer == BlockRenderLayer.CUTOUT) builder.addAll(tools.getQuads(state, side, rand));

                @Nonnull final TableData trueVariant = getTrueVariant(state);
                if(Block.getBlockFromItem(trueVariant.item).getRenderLayer() == layer) builder.add(Loader.INSTANCE.cache.getUnchecked(Triple.of(wood, side, trueVariant)));
            }

            return builder.build();
        }

        @Nonnull
        @Override
        public ItemOverrideList getOverrides() {
            return new ItemOverrideList(ImmutableList.of()) {
                @Nonnull
                @Override
                public IBakedModel handleItemState(@Nonnull final IBakedModel originalModelIn, @Nonnull final ItemStack stack, @Nullable final World world, @Nullable final EntityLivingBase entity) {
                    return new Baked(originalModel, tools, wood, transforms, textureGetter, ItemSmithingTable.getVariant(stack));
                }
            };
        }

        @Nonnull
        private TableData getTrueVariant(@Nullable final IBlockState state) {
            if(variant != null) return variant;
            else if(state instanceof IExtendedBlockState) {
                @Nullable final TableData prop = ((IExtendedBlockState)state).getValue(UnlistedVariantProperty.INSTANCE);
                if(prop != null) return prop;
            }

            return ItemSmithingTable.getVariant(ItemStack.EMPTY);
        }
    }

    @Nonnull
    @Override
    public IModel process(@Nonnull final ImmutableMap<String, String> customData) {
        return new ModelSmithingTable(
                new ModelResourceLocation(JsonUtils.getString(new JsonParser().parse(customData.get("solid")), "solid")),
                new ModelResourceLocation(JsonUtils.getString(new JsonParser().parse(customData.get("tools")), "tools")),
                new ModelResourceLocation(JsonUtils.getString(new JsonParser().parse(customData.get("wood")),  "wood"))
        );
    }

    @SideOnly(Side.CLIENT)
    public enum Loader implements ICustomModelLoader
    {
        INSTANCE;

        @Nonnull
        final LoadingCache<Triple<IBakedModel, EnumFacing, TableData>, BakedQuad[]> cache = CacheBuilder.newBuilder().build(CacheLoader.from(info -> {
            @Nonnull final IBakedModel model = info.getLeft();
            @Nullable final EnumFacing side = info.getMiddle();
            @Nonnull final TableData variant = info.getRight();

            @Nonnull final TextureAtlasSprite tex = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(variant.item, variant.meta);
            return model.getQuads(null, side, 0).stream().map(quad -> new BakedQuadRetextured(quad, tex)).toArray(BakedQuad[]::new);
        }));

        @Override
        public void onResourceManagerReload(@Nonnull final IResourceManager resourceManager) {
            cache.invalidateAll();
        }

        @Override
        public boolean accepts(@Nonnull final ResourceLocation modelLocation) {
            return modelLocation.getNamespace().equals(SmithingTable.MOD_ID) && modelLocation.getPath().endsWith("builtin/table");
        }

        @Nonnull
        @Override
        public IModel loadModel(@Nonnull final ResourceLocation modelLocation) {
            return ModelSmithingTable.INSTANCE;
        }
    }
}
