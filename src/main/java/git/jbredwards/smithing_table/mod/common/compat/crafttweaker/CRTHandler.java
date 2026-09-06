package git.jbredwards.smithing_table.mod.common.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import org.jetbrains.annotations.ApiStatus;

/**
 *
 * @author jbred
 *
 */
public final class CRTHandler
{
    @ApiStatus.Internal
    public static void postInit() {
        CRTSmithingRecipes.removeActions.forEach(CraftTweakerAPI::apply);
        CRTSmithingRecipes.addActions.forEach(CraftTweakerAPI::apply);
    }
}
