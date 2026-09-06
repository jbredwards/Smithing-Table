package git.jbredwards.smithing_table.mod.common.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import git.jbredwards.smithing_table.api.SmithingTemplate;
import git.jbredwards.smithing_table.mod.SmithingTable;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.ApiStatus;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 *
 * @author jbred
 *
 */
@ZenRegister
@ZenClass("mods." + SmithingTable.MOD_ID + ".templates")
public final class CRTSmithingTemplates
{
    @Nonnull
    private static final Map<String, SmithingTemplate> TEMPLATES = new Object2ObjectLinkedOpenHashMap<>();
    private static boolean INITIALISED;

    @ApiStatus.Internal
    public static void registerTemplates(@Nonnull final IForgeRegistry<SmithingTemplate> registry) {
        for(@Nonnull final SmithingTemplate template : TEMPLATES.values()) {
            registry.register(template);
            // TODO: Look into how CrT handles its resources.
        }

        INITIALISED = true;
    }

    @ZenMethod
    public static void register(@Nullable final String name, @Optional(valueLong = 64) final int stackSize, @Optional final boolean enchantedEffect) {
        if(name == null) {
            CraftTweakerAPI.logError("The registry name of the smithing template must be non-null!");
            return;
        } else if(INITIALISED) {
            CraftTweakerAPI.logError("Smithing templates must registered in preInit. Tried to register " + name + " too late!");
            return;
        }

        @Nonnull final ResourceLocation loc;
        @Nonnull final ModelResourceLocation model;
        if(name.indexOf('#') == -1) {
            loc = new ResourceLocation("crafttweaker", name);
            model = new ModelResourceLocation(loc, "inventory");
        } else {
            model = new ModelResourceLocation("crafttweaker:" + name);
            loc = new ResourceLocation(model.getNamespace(), model.getPath());
        }

        if(TEMPLATES.containsKey(loc.getPath())) {
            CraftTweakerAPI.logError("The registry name of the smithing template must not match an already registered smithing template!");
            return;
        }

        @Nonnull final SmithingTemplate template = new SmithingTemplate(model, false);
        template.forceEnchantGlint = enchantedEffect;
        template.maxStackSize = MathHelper.clamp(stackSize, 1, 64);
        TEMPLATES.put(loc.getPath(), template.setRegistryName(loc));
    }
}
