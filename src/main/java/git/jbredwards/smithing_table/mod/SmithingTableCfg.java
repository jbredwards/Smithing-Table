package git.jbredwards.smithing_table.mod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.common.config.Config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author jbred
 *
 */
@Config(modid = SmithingTable.MOD_ID, name = SmithingTable.MOD_ID + "/general")
public final class SmithingTableCfg
{
    @Config.LangKey("cfg.smithing_table.automation")
    public static boolean automation = true;

    @Config.LangKey("cfg.smithing_table.automationSound")
    public static boolean automationSound = true;

    @Config.LangKey("cfg.smithing_table.gui.hammer")
    public static HammerType hammer = HammerType.SMITHING;
    public enum HammerType
    {
        SMITHING(83),
        ANVIL(113),
        NONE(143);

        public final int yOffset;
        HammerType(final int yOffsetIn) {
            yOffset = yOffsetIn;
        }

        @Nonnull
        @Override
        public String toString() {
            return "cfg.smithing_table.gui.hammer." + name();
        }
    }

    @Config.LangKey("cfg.smithing_table.gui.armorStand")
    public static boolean armorStand = true;

    @Config.LangKey("cfg.smithing_table.gui.armorStandBackground")
    public static boolean armorStandBackground = true;

    @Config.LangKey("cfg.smithing_table.gui.equipmentOverlay")
    public static boolean equipmentOverlay = true;

    @Config.LangKey("cfg.smithing_table.gui.materialOverlay")
    public static boolean materialOverlay = true;

    @Config.LangKey("cfg.smithing_table.gui.templateOverlay")
    public static boolean templateOverlay = true;

    @Nullable
    @Config.Ignore
    static JsonObject recipe = null;
    static void initRecipe(@Nonnull final File configFolder) {
        configFolder.mkdirs();

        @Nonnull final File recipeFile = new File(configFolder, "recipe.json");
        if(!recipeFile.exists()) {
            @Nonnull final String recipeText =
                    "{\n" +
                    "    \"pattern\": [\n" +
                    "        \"II\",\n" +
                    "        \"PP\",\n" +
                    "        \"PP\"\n" +
                    "    ],\n" +
                    "    \"key\": {\n" +
                    "        \"I\": {\n" +
                    "            \"type\": \"forge:ore_dict\",\n" +
                    "            \"ore\": \"ingotIron\"\n" +
                    "        },\n" +
                    "        \"P\": {\n" +
                    "            \"type\": \"forge:ore_dict\",\n" +
                    "            \"ore\": \"plankWood\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";

            try(@Nonnull final FileWriter writer = new FileWriter(recipeFile)) { writer.write(recipeText); }
            catch(@Nonnull final IOException e) { throw new RuntimeException(e); }
            recipe = new JsonParser().parse(recipeText).getAsJsonObject();
        }

        else {
            try { recipe = new JsonParser().parse(new FileReader(recipeFile)).getAsJsonObject(); }
            catch(@Nonnull final Exception ignored) {}
        }
    }
}
