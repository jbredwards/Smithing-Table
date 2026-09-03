package git.jbredwards.smithing_table.mod;

import git.jbredwards.smithing_table.Tags;
import git.jbredwards.smithing_table.api.SmithingTemplateIngredient;
import git.jbredwards.smithing_table.mod.client.SmithingTableGuiHandler;
import git.jbredwards.smithing_table.mod.common.item.ItemSmithingTemplate;
import git.jbredwards.smithing_table.mod.common.compat.AssetMoverHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 *
 * @author jbred
 *
 */
@Mod.EventBusSubscriber
@Mod(modid = SmithingTable.MOD_ID, name = SmithingTable.MOD_NAME, version = SmithingTable.VERSION,
guiFactory = "git.jbredwards.smithing_table.mod.client.gui.SmithingTableGuiFactory",
updateJSON = "https://api.modrinth.com/updates/smithing-table/forge_updates.json",
dependencies = "after-client:assetmover@[2.5,)")
public final class SmithingTable
{
    @Nonnull
    public static final String MOD_ID = Tags.MOD_ID, MOD_NAME = Tags.MOD_NAME, VERSION = Tags.VERSION;

    @Mod.EventHandler
    static void construct(@Nonnull final FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(RegistryHandler.class);
        NetworkRegistry.INSTANCE.registerGuiHandler(MOD_ID, SmithingTableGuiHandler.INSTANCE);
    }

    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    static void constructClient(@Nonnull final FMLConstructionEvent event) {
        if(Loader.isModLoaded("assetmover")) AssetMoverHandler.construct();
    }

    @Mod.EventHandler
    static void preInit(@Nonnull final FMLPreInitializationEvent event) {
        CraftingHelper.register(new ResourceLocation(MOD_ID, "template"), SmithingTemplateIngredient.FACTORY);
    }

    @Mod.EventHandler
    static void init(@Nonnull final FMLInitializationEvent event) {
        ItemSmithingTemplate.initCreativeTabs();
    }

    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    static void initClient(@Nonnull final FMLInitializationEvent event) {
        // Remove "disable" button in mod gui.
        @Nonnull final ModContainer mod = Objects.requireNonNull(Loader.instance().activeModContainer());
        ReflectionHelper.setPrivateValue(FMLModContainer.class, (FMLModContainer)mod, ModContainer.Disableable.NEVER, "disableability");
        // Allow this mod's description and credits to be translated.
        @Nonnull final ModMetadata metadata = mod.getMetadata();
        @Nonnull final String credits = metadata.credits, description = metadata.description;
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener((ISelectiveResourceReloadListener)(manager, predicate) -> {
            if(predicate.test(VanillaResourceType.LANGUAGES)) {
                metadata.credits = I18n.hasKey("mod." + MOD_ID + ".credits") ? I18n.format("mod." + MOD_ID + ".credits").replace("\\n", "\n") : credits;
                metadata.description = I18n.hasKey("mod." + MOD_ID + ".description") ? I18n.format("mod." + MOD_ID + ".description") : description;
            }
        });
    }
}
