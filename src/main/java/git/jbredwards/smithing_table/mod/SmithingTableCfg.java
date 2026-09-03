package git.jbredwards.smithing_table.mod;

import net.minecraftforge.common.config.Config;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@Config(modid = SmithingTable.MOD_ID)
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

    @Config.LangKey("cfg.smithing_table.gui.materialOverlay")
    public static boolean materialOverlay = false;

    @Config.LangKey("cfg.smithing_table.gui.templateOverlay")
    public static boolean templateOverlay = true;
}
