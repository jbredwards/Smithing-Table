package git.jbredwards.smithing_table.mod.common.compat.justenoughitems;

import git.jbredwards.smithing_table.api.SmithingContent;
import git.jbredwards.smithing_table.mod.common.item.ItemSmithingTable;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@JEIPlugin
public final class JEIHandler implements IModPlugin
{
    @Override
    public void registerItemSubtypes(@Nonnull final ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.registerSubtypeInterpreter(Item.getItemFromBlock(SmithingContent.SMITHING_TABLE),
                stack -> ItemSmithingTable.getVariant(stack).toString());
    }
}
