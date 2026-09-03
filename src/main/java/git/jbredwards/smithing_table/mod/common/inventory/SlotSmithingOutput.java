package git.jbredwards.smithing_table.mod.common.inventory;

import git.jbredwards.smithing_table.mod.SmithingTableCfg;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
    protected final ContainerSmithingTable container;
    public SlotSmithingOutput(@Nonnull final ContainerSmithingTable containerIn, final int index, final int xPosition, final int yPosition) {
        super(containerIn.smithingTable.processor, index, xPosition, yPosition);
        container = containerIn;
    }

    @Override
    public void putStack(@Nonnull final ItemStack stack) {
        // NO-OP
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        container.detectAndSendChanges();
    }

    @Nonnull
    @Override
    public ItemStack onTake(@Nonnull final EntityPlayer thePlayer, @Nonnull final ItemStack stack) {
        if(!SmithingTableCfg.automationSound) container.smithingTable.playSound();
        return super.onTake(thePlayer, stack);
    }
}
