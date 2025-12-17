package git.jbredwards.smithing_table.mod;

import com.cleanroommc.assetmover.AssetMoverAPI;
import com.google.common.collect.ImmutableMap;
import git.jbredwards.smithing_table.Tags;
import git.jbredwards.smithing_table.api.SmithingContent;
import git.jbredwards.smithing_table.api.SmithingTemplateIngredient;
import git.jbredwards.smithing_table.mod.client.SmithingTableGuiHandler;
import git.jbredwards.smithing_table.mod.common.ItemSmithingTemplate;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
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
        AssetMoverAPI.fromMinecraft("1.20.1", ImmutableMap
                .of("assets/minecraft/textures/gui/container/smithing.png", String.format("assets/%s/textures/gui.png", MOD_ID)));
        AssetMoverAPI.fromMinecraft("1.18.2", ImmutableMap.<String, String>builder()
                .put("assets/minecraft/textures/gui/container/smithing.png", String.format("assets/%s/textures/gui_old.png", MOD_ID))
                // Registry objects.
                .put("assets/minecraft/sounds/block/smithing_table/smithing_table1.ogg", String.format("assets/%s/sounds/use1.ogg", MOD_ID))
                .put("assets/minecraft/sounds/block/smithing_table/smithing_table2.ogg", String.format("assets/%s/sounds/use2.ogg", MOD_ID))
                .put("assets/minecraft/sounds/block/smithing_table/smithing_table3.ogg", String.format("assets/%s/sounds/use3.ogg", MOD_ID))
                .put("assets/minecraft/textures/block/smithing_table_bottom.png", String.format("assets/%s/textures/blocks/bottom.png", MOD_ID))
                .put("assets/minecraft/textures/block/smithing_table_front.png", String.format("assets/%s/textures/blocks/front.png", MOD_ID))
                .put("assets/minecraft/textures/block/smithing_table_side.png", String.format("assets/%s/textures/blocks/side.png", MOD_ID))
                .put("assets/minecraft/textures/block/smithing_table_top.png", String.format("assets/%s/textures/blocks/top.png", MOD_ID))
                .build());
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
