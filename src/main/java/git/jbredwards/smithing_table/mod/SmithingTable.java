package git.jbredwards.smithing_table.mod;

import git.jbredwards.smithing_table.Tags;
import git.jbredwards.smithing_table.api.SmithingContent;
import git.jbredwards.smithing_table.api.SmithingTemplateIngredient;
import git.jbredwards.smithing_table.mod.client.SmithingTableGuiHandler;
import git.jbredwards.smithing_table.mod.common.ItemSmithingTemplate;
import git.jbredwards.smithing_table.mod.common.compat.AssetMoverHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@Mod.EventBusSubscriber
@Mod(modid = SmithingTable.MOD_ID, name = SmithingTable.MOD_NAME, version = SmithingTable.VERSION)
public final class SmithingTable
{
    @Nonnull
    public static final String MOD_ID = Tags.MOD_ID, MOD_NAME = Tags.MOD_NAME, VERSION = Tags.VERSION;
    public static boolean templateEnabled() {
        return true;
    }

    @Mod.EventHandler
    static void construct(@Nonnull final FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(RegistryHandler.class);
        MinecraftForge.EVENT_BUS.register(SmithingContent.class);
        NetworkRegistry.INSTANCE.registerGuiHandler(MOD_ID, SmithingTableGuiHandler.INSTANCE);
    }

    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    static void constructClient(@Nonnull final FMLConstructionEvent event) {
        if(Loader.isModLoaded("assetmover")) AssetMoverHandler.construct();
    }

    @Mod.EventHandler
    static void preInit(@Nonnull final FMLPreInitializationEvent event) {
        CraftingHelper.register(new ResourceLocation(MOD_ID, "template_enabled"), SmithingTemplateIngredient.CONDITION);
        CraftingHelper.register(new ResourceLocation(MOD_ID, "template"), SmithingTemplateIngredient.FACTORY);
    }

    @Mod.EventHandler
    static void init(@Nonnull final FMLInitializationEvent event) {
        ItemSmithingTemplate.initCreativeTabs();
    }
}
