package git.jbredwards.smithing_table.mod.common.compat;

import com.cleanroommc.assetmover.AssetMoverAPI;
import com.google.common.collect.ImmutableMap;
import git.jbredwards.smithing_table.Tags;
import git.jbredwards.smithing_table.api.SmithingContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jbred
 *
 */
@SideOnly(Side.CLIENT)
public final class AssetMoverHandler
{
    public static void construct() {
        AssetMoverAPI.fromMinecraft("1.18.2", ImmutableMap.<String, String>builder()
                .put("assets/minecraft/sounds/block/smithing_table/smithing_table1.ogg", String.format("assets/%s/sounds/use1.ogg", Tags.MOD_ID))
                .put("assets/minecraft/sounds/block/smithing_table/smithing_table2.ogg", String.format("assets/%s/sounds/use2.ogg", Tags.MOD_ID))
                .put("assets/minecraft/sounds/block/smithing_table/smithing_table3.ogg", String.format("assets/%s/sounds/use3.ogg", Tags.MOD_ID))
                .build());
        // TODO: Add config to disable AssetMover sound override.
        MinecraftForge.EVENT_BUS.register(AssetMoverHandler.class);
    }

    @SubscribeEvent
    static void modifySound(@Nonnull final SoundLoadEvent event) {
        @Nullable final SoundEventAccessor accessor = event.getManager().sndHandler.getAccessor(SmithingContent.BLOCK_SMITHING_TABLE_USE.getSoundName());
        if(accessor != null) { // Should never pass, but let's be safe.
            @Nonnull final List<ISoundEventAccessor<Sound>> sounds = ObfuscationReflectionHelper.getPrivateValue(SoundEventAccessor.class, accessor, "field_188716_a");
            @Nonnull final List<ISoundEventAccessor<Sound>> newSounds = new ArrayList<>();

            addAccessorIfPresent(newSounds, new ResourceLocation(Tags.MOD_ID, "use1"));
            addAccessorIfPresent(newSounds, new ResourceLocation(Tags.MOD_ID, "use2"));
            addAccessorIfPresent(newSounds, new ResourceLocation(Tags.MOD_ID, "use3"));

            if(!newSounds.isEmpty()) {
                sounds.clear();
                sounds.addAll(newSounds);
            }
        }
    }

    private static void addAccessorIfPresent(@Nonnull final List<ISoundEventAccessor<Sound>> sounds, @Nonnull final ResourceLocation location) {
        @Nullable IResource resource = null;
        try {
            @Nonnull final Sound sound = new Sound(location.toString(), 1, 1, 1, Sound.Type.FILE, false);
            resource = Minecraft.getMinecraft().getResourceManager().getResource(sound.getSoundAsOggLocation());
            resource.getInputStream(); // Validate input stream.
            sounds.add(new ISoundEventAccessor<Sound>() {
                @Override
                public int getWeight() {
                    return sound.getWeight();
                }

                @Nonnull
                @Override
                public Sound cloneEntry() {
                    return sound.cloneEntry();
                }
            });
        }
        catch(@Nonnull final IOException ignored) {
            // NO-OP
        }
        finally {
            IOUtils.closeQuietly(resource);
        }
    }
}
