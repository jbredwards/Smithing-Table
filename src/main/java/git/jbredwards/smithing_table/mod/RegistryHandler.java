package git.jbredwards.smithing_table.mod;

import git.jbredwards.smithing_table.Tags;
import git.jbredwards.smithing_table.api.SmithingContent;
import git.jbredwards.smithing_table.mod.client.ModelSmithingTable;
import git.jbredwards.smithing_table.mod.client.ModelSmithingTemplate;
import git.jbredwards.smithing_table.mod.common.block.BlockSmithingTable;
import git.jbredwards.smithing_table.mod.common.block.TableData;
import git.jbredwards.smithing_table.mod.common.item.ItemSmithingTable;
import git.jbredwards.smithing_table.mod.common.item.ItemSmithingTemplate;
import git.jbredwards.smithing_table.mod.common.block.TileSmithingTable;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author jbred
 *
 */
final class RegistryHandler
{
    @SubscribeEvent
    static void registerBlocks(@Nonnull final RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new BlockSmithingTable(Material.WOOD, MapColor.BROWN_STAINED_HARDENED_CLAY).setTranslationKey(SmithingTable.MOD_ID + ".table").setRegistryName("table"));
        GameRegistry.registerTileEntity(TileSmithingTable.class, new ResourceLocation(SmithingTable.MOD_ID, "table"));
    }

    @SubscribeEvent
    static void registerItems(@Nonnull final RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemSmithingTable(SmithingContent.SMITHING_TABLE).setRegistryName("table"));
        event.getRegistry().register(new ItemSmithingTemplate().setHasSubtypes(true).setCreativeTab(SmithingContent.CREATIVE_TAB).setTranslationKey(SmithingTable.MOD_ID + ".template").setRegistryName("template"));
    }

    @SubscribeEvent
    static void registerItemModels(@Nonnull final ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SmithingContent.SMITHING_TABLE), 0, new ModelResourceLocation(SmithingContent.SMITHING_TABLE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(SmithingContent.SMITHING_TEMPLATE, 0, new ModelResourceLocation(SmithingContent.SMITHING_TEMPLATE.getRegistryName(), "inventory"));
        ModelLoaderRegistry.registerLoader(ModelSmithingTable.Loader.INSTANCE);
        ModelLoaderRegistry.registerLoader(ModelSmithingTemplate.Loader.INSTANCE);
    }

    @SubscribeEvent
    static void registerRecipes(@Nonnull final RegistryEvent.Register<IRecipe> event) {
        @Nonnull final Set<TableData> variants = ItemSmithingTable.getVariants();
        variants.remove(TableData.DEFAULT);

        // Register a recipe for each variant.
        @Nonnull final ResourceLocation group = new ResourceLocation(Tags.MOD_ID, "tables");
        for(@Nonnull final TableData variant : variants) event.getRegistry().register(new ShapedOreRecipe(group,
                ItemSmithingTable.setVariant(new ItemStack(SmithingContent.SMITHING_TABLE), variant),
                "II", "##", "##", 'I', "ingotIron", '#', new ItemStack(variant.item, 1, variant.meta)).setRegistryName(
                        "tables/" + Objects.toString(variant.item.getRegistryName()).replace(':', '/') + '/' + variant.meta));

        // Register a generic recipe for the default variant.
        event.getRegistry().register(new ShapedOreRecipe(group,
                ItemSmithingTable.setVariant(new ItemStack(SmithingContent.SMITHING_TABLE), TableData.DEFAULT),
                "II", "##", "##", 'I', "ingotIron", '#', "plankWood").setRegistryName("tables/generic"));
    }

    @SubscribeEvent
    static void registerRegistries(@Nonnull final RegistryEvent.NewRegistry event) throws ClassNotFoundException {
        new RegistryBuilder().setType(Class.forName("git.jbredwards.smithing_table.api.SmithingRecipe", false, RegistryHandler.class.getClassLoader())).setName(new ResourceLocation(SmithingTable.MOD_ID, "recipes")).allowModification().disableSaving().create();
        new RegistryBuilder().setType(Class.forName("git.jbredwards.smithing_table.api.SmithingTemplate", false, RegistryHandler.class.getClassLoader())).setName(new ResourceLocation(SmithingTable.MOD_ID, "templates")).create();
    }

    @SubscribeEvent
    static void registerSounds(@Nonnull final RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(new SoundEvent(new ResourceLocation(SmithingTable.MOD_ID, "blocks.smithing_table.use")).setRegistryName("blocks.smithing_table.use"));
    }
}
