package git.jbredwards.smithing_table.mod.client;

import com.google.common.collect.ImmutableList;
import git.jbredwards.smithing_table.api.SmithingTemplate;
import git.jbredwards.smithing_table.mod.SmithingTable;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author jbred
 *
 */
@SideOnly(Side.CLIENT)
public final class ModelSmithingTemplate implements IModel
{
    @Nonnull
    public static final ModelSmithingTemplate INSTANCE = new ModelSmithingTemplate();

    @Nonnull
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return SmithingTemplate.REGISTRY.getValuesCollection().stream().map(template -> template.model).collect(ImmutableList.toImmutableList());
    }

    @Nonnull
    @Override
    public IBakedModel bake(@Nonnull final IModelState state, @Nonnull final VertexFormat format, @Nonnull final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new Baked(ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter), format, bakedTextureGetter);
    }

    @SideOnly(Side.CLIENT)
    public static final class Baked extends BakedModelWrapper<IBakedModel>
    {
        @Nonnull private final VertexFormat format;
        @Nonnull private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

        public Baked(@Nonnull final IBakedModel originalModel, @Nonnull final VertexFormat formatIn, @Nonnull final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetterIn) {
            super(originalModel);
            format = formatIn;
            bakedTextureGetter = bakedTextureGetterIn;
        }

        @Nonnull
        @Override
        public ItemOverrideList getOverrides() {
            return new ItemOverrideList(ImmutableList.of()) {
                @Nonnull
                @Override
                public IBakedModel handleItemState(@Nonnull final IBakedModel originalModel, @Nonnull final ItemStack stack, @Nullable final World world, @Nullable final EntityLivingBase entity) {
                    @Nullable final SmithingTemplate template = SmithingTemplate.deserialize(stack);
                    return template != null ? Loader.INSTANCE.models.computeIfAbsent(template.model, location -> {
                        @Nonnull final IModel model = ModelLoaderRegistry.getModelOrMissing(location);
                        return model.bake(model.getDefaultState(), format, bakedTextureGetter);
                    }) : originalModel;
                }
            };
        }
    }

    @SideOnly(Side.CLIENT)
    public enum Loader implements ICustomModelLoader
    {
        INSTANCE;

        @Nonnull
        final Map<ResourceLocation, IBakedModel> models = new HashMap<>();

        @Override
        public void onResourceManagerReload(@Nonnull final IResourceManager resourceManager) {
            models.clear();
        }

        @Override
        public boolean accepts(@Nonnull final ResourceLocation modelLocation) {
            return modelLocation.getNamespace().equals(SmithingTable.MOD_ID) && modelLocation.getPath().endsWith("template");
        }

        @Nonnull
        @Override
        public IModel loadModel(@Nonnull final ResourceLocation modelLocation) {
            return ModelSmithingTemplate.INSTANCE;
        }
    }
}
