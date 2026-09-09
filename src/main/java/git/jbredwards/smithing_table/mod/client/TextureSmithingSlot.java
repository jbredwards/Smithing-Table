package git.jbredwards.smithing_table.mod.client;

import com.google.common.collect.ImmutableList;
import git.jbredwards.smithing_table.mod.client.gui.GuiSmithingTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author jbred
 *
 */
@SideOnly(Side.CLIENT)
public class TextureSmithingSlot extends TextureAtlasSprite
{
    @Nonnull
    public final Collection<ResourceLocation> dependencies;
    public TextureSmithingSlot(@Nonnull final Collection<ResourceLocation> textures, @Nonnull final String spriteName) {
        super(spriteName);
        dependencies = ImmutableList.<ResourceLocation>builder()
                .add(GuiSmithingTable.SLOT_BACK)
                .addAll(textures)
                .build();
    }

    @Nonnull
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return dependencies;
    }

    @Override
    public boolean hasCustomLoader(@Nonnull final IResourceManager manager, @Nonnull final ResourceLocation location) {
        return true;
    }

    @Override
    public boolean load(@Nonnull final IResourceManager manager, @Nonnull final ResourceLocation location, @Nonnull final Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        @Nonnull final TextureAtlasSprite[] textures = dependencies.stream().map(textureGetter).toArray(TextureAtlasSprite[]::new);
        animationMetadata = new AnimationMetadataSection(IntStream.range(0, textures.length - 1).boxed()
                .flatMap(index -> Stream.of(new AnimationFrame(index, 26), new AnimationFrame(index, 4)))
                .collect(Collectors.toList()), -1, -1, 1, true);

        width = Arrays.stream(textures).mapToInt(TextureAtlasSprite::getIconWidth).max().orElse(16);
        height = width; // This texture is animated, set equal dimensions.

        @Nonnull final List<int[][]> frames = new ArrayList<>();
        @Nonnull final BufferedImage back = toBufferedImage(textures[0]);
        for(int i = 1; i < textures.length; i++) {
            @Nonnull final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            @Nonnull final Graphics2D graphics = img.createGraphics();

            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            graphics.drawImage(back, 0, 0, width, height, null);
            graphics.drawImage(toBufferedImage(textures[i]), 0, 0, width, height, null);
            graphics.dispose();

            @Nonnull final int[][] frame = new int[Minecraft.getMinecraft().getTextureMapBlocks().getMipmapLevels() + 1][];
            frame[0] = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
            frames.add(frame);
        }

        setFramesTextureData(frames);
        return false;
    }

    @Nonnull
    protected static BufferedImage toBufferedImage(@Nonnull final TextureAtlasSprite texture) {
        @Nonnull final BufferedImage image = new BufferedImage(texture.getIconWidth(), texture.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, image.getWidth(), image.getHeight(), texture.getFrameTextureData(0)[0], 0, image.getWidth());
        return image;
    }
}
