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
import java.util.function.UnaryOperator;

/**
 *
 * @author jbred
 *
 */
@SideOnly(Side.CLIENT)
public final class ModelSmithingTable implements IModel
{
    @Nonnull private static final ResourceLocation MISSING = new ResourceLocation("builtin/missing");
    @Nonnull public static final ModelSmithingTable INSTANCE = new ModelSmithingTable(MISSING, MISSING, MISSING);

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
                builder.add(Loader.INSTANCE.cache.getUnchecked(Triple.of(wood, side, getVariant(state))));
            }

            else {
                if(layer == BlockRenderLayer.SOLID) builder.addAll(originalModel.getQuads(state, side, rand));
                else if(layer == BlockRenderLayer.CUTOUT) builder.addAll(tools.getQuads(state, side, rand));

                @Nonnull final TableData variant = getVariant(state);
                @Nonnull final IBlockState woodState = variant.getBlockState();
                if(woodState.getBlock().canRenderInLayer(woodState, layer)) builder.add(Loader.INSTANCE.cache.getUnchecked(Triple.of(wood, side, variant)));
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
        private TableData getVariant(@Nullable final IBlockState state) {
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

            @Nullable final TextureAtlasSprite tex = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(variant.item, variant.meta);
            return model.getQuads(null, side, 0).stream().map(tex != null ? quad -> new BakedQuadRetextured(quad, tex) : UnaryOperator.identity()).toArray(BakedQuad[]::new);
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
