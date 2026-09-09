package git.jbredwards.smithing_table.mod;

import git.jbredwards.smithing_table.api.SmithingContent;
import git.jbredwards.smithing_table.api.SmithingTemplate;
import git.jbredwards.smithing_table.mod.client.ModelSmithingTable;
import git.jbredwards.smithing_table.mod.client.ModelSmithingTemplate;
import git.jbredwards.smithing_table.mod.client.TextureSmithingSlot;
import git.jbredwards.smithing_table.mod.client.gui.GuiSmithingTable;
import git.jbredwards.smithing_table.mod.common.block.TableData;
import git.jbredwards.smithing_table.mod.common.compat.crafttweaker.CRTSmithingTemplates;
import git.jbredwards.smithing_table.mod.common.compat.groovyscript.GRSSmithingTemplates;
import git.jbredwards.smithing_table.mod.common.item.ItemSmithingTable;
import git.jbredwards.smithing_table.mod.common.block.TileSmithingTable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.commons.lang3.CharUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author jbred
 *
 */
final class RegistryHandler
{
    @SubscribeEvent
    static void registerBlocks(@Nonnull final RegistryEvent.Register<Block> event) {
        event.getRegistry().register(SmithingContent.SMITHING_TABLE.setCreativeTab(SmithingContent.CREATIVE_TAB).setTranslationKey(SmithingTable.MOD_ID + ".table").setRegistryName("table"));
        GameRegistry.registerTileEntity(TileSmithingTable.class, new ResourceLocation(SmithingTable.MOD_ID, "table"));
    }

    @SubscribeEvent
    static void registerItems(@Nonnull final RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemSmithingTable(SmithingContent.SMITHING_TABLE).setRegistryName("table"));
        event.getRegistry().register(SmithingContent.SMITHING_TEMPLATE.setCreativeTab(SmithingContent.CREATIVE_TAB).setTranslationKey(SmithingTable.MOD_ID + ".template").setRegistryName("template"));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    static void registerItemModels(@Nonnull final ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SmithingContent.SMITHING_TABLE), 0, new ModelResourceLocation(SmithingContent.SMITHING_TABLE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(SmithingContent.SMITHING_TEMPLATE, 0, new ModelResourceLocation(SmithingContent.SMITHING_TEMPLATE.getRegistryName(), "inventory"));
        ModelLoaderRegistry.registerLoader(ModelSmithingTable.Loader.INSTANCE);
        ModelLoaderRegistry.registerLoader(ModelSmithingTemplate.Loader.INSTANCE);
    }

    @SubscribeEvent
    static void registerRecipes(@Nonnull final RegistryEvent.Register<IRecipe> event) {
        @Nonnull final JsonContext ctx = new JsonContext(SmithingTable.MOD_ID);
        @Nonnull final Object[] recipe;

        try {
            if(!CraftingHelper.processConditions(SmithingTableCfg.recipe, "conditions", ctx)) return;
            @Nonnull final List<Object> parsed = new ArrayList<>();

            JsonUtils.getJsonArray(SmithingTableCfg.recipe, "pattern").forEach(json -> parsed.add(JsonUtils.getString(json, "pattern")));
            JsonUtils.getJsonObject(SmithingTableCfg.recipe, "key").entrySet().forEach(entry -> {
                parsed.add(CharUtils.toChar(entry.getKey()));
                parsed.add(CraftingHelper.getIngredient(entry.getValue(), ctx));
            });

            recipe = parsed.toArray();
        }
        catch(@Nonnull final Exception ignored) { return; }
        @Nonnull final ItemStack defaultPlanks = new ItemStack(Blocks.PLANKS);
        @Nonnull final Set<TableData> variants = ItemSmithingTable.getVariants();
        variants.remove(TableData.DEFAULT);

        // Register a recipe for each variant.
        @Nonnull final ResourceLocation group = new ResourceLocation(SmithingTable.MOD_ID, "tables");
        for(@Nonnull final TableData variant : variants) {
            @Nonnull final CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(recipe);

            primer.input.replaceAll(i -> i.test(defaultPlanks) ? Ingredient.fromStacks(new ItemStack(variant.item, 1, variant.meta)) : i);
            event.getRegistry().register(new ShapedOreRecipe(group,
                    ItemSmithingTable.setVariant(new ItemStack(SmithingContent.SMITHING_TABLE), variant), primer).setRegistryName(
                    "tables/" + Objects.toString(variant.item.getRegistryName()).replace(':', '/') + '/' + variant.meta));
        }

        // Register a generic recipe for the default variant.
        event.getRegistry().register(new ShapedOreRecipe(group, ItemSmithingTable.setVariant(new ItemStack(SmithingContent.SMITHING_TABLE), TableData.DEFAULT), recipe).setRegistryName("tables/generic"));
    }

    @SubscribeEvent
    static void registerRegistries(@Nonnull final RegistryEvent.NewRegistry event) throws ClassNotFoundException {
        new RegistryBuilder().setType(Class.forName("git.jbredwards.smithing_table.api.SmithingRecipe", false, RegistryHandler.class.getClassLoader())).setName(new ResourceLocation(SmithingTable.MOD_ID, "recipes")).allowModification().disableSaving().create();
        new RegistryBuilder().setType(Class.forName("git.jbredwards.smithing_table.api.SmithingTemplate", false, RegistryHandler.class.getClassLoader())).setName(new ResourceLocation(SmithingTable.MOD_ID, "templates")).create();
    }

    @SubscribeEvent
    static void registerSounds(@Nonnull final RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(SmithingContent.BLOCK_SMITHING_TABLE_USE.setRegistryName("blocks.smithing_table.use"));
    }

    @SubscribeEvent
    static void registerTemplates(@Nonnull final RegistryEvent.Register<SmithingTemplate> event) {
        if(Loader.isModLoaded("crafttweaker")) CRTSmithingTemplates.registerTemplates(event.getRegistry());
        if(Loader.isModLoaded("groovyscript")) GRSSmithingTemplates.registerTemplates(event.getRegistry());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    static void registerTextures(@Nonnull final TextureStitchEvent.Pre event) {
        if(event.getMap() == Minecraft.getMinecraft().getTextureMapBlocks()) {
            if(SmithingTemplate.REGISTRY.getKeys().isEmpty()) event.getMap().registerSprite(GuiSmithingTable.SLOT_BACK);
            else event.getMap().setTextureEntry(new TextureSmithingSlot(
                    SmithingTemplate.REGISTRY.getValuesCollection().stream().map(st -> st.templateSlotTexture).distinct().collect(Collectors.toList()),
                    GuiSmithingTable.getTemplateTexture()));

            @Nonnull final Set<String> cache = new HashSet<>();
            for(@Nonnull final SmithingTemplate template : SmithingTemplate.REGISTRY) {
                if(template.equipmentSlotInfo != null) {
                    @Nonnull final String name = GuiSmithingTable.joinInfoTextures(template.equipmentSlotInfo);
                    if(cache.add(name)) event.getMap().setTextureEntry(new TextureSmithingSlot(template.equipmentSlotInfo.textures(), name));
                }

                if(template.materialSlotInfo != null) {
                    @Nonnull final String name = GuiSmithingTable.joinInfoTextures(template.materialSlotInfo);
                    if(cache.add(name)) event.getMap().setTextureEntry(new TextureSmithingSlot(template.materialSlotInfo.textures(), name));
                }
            }
        }
    }

    @SubscribeEvent
    static void syncConfig(@Nonnull final ConfigChangedEvent.OnConfigChangedEvent event) {
        if(SmithingTable.MOD_ID.equals(event.getModID())) ConfigManager.sync(SmithingTable.MOD_ID, Config.Type.INSTANCE);
    }
}
