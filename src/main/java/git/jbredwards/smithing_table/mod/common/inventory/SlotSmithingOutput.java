package git.jbredwards.smithing_table.mod.common.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public class SlotSmithingOutput extends SlotItemHandler
{
    @Nonnull
    public ItemStack craftingResult = ItemStack.EMPTY;
    public SlotSmithingOutput(@Nonnull final IItemHandler itemHandler, final int index, final int xPosition, final int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }


}
