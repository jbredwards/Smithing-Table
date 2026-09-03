package git.jbredwards.smithing_table.mod.common.inventory;

import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public class SlotSmithingInput extends SlotItemHandler
{
    @Nonnull
    protected final ContainerSmithingTable container;
    public SlotSmithingInput(@Nonnull final ContainerSmithingTable containerIn, final int index, final int xPosition, final int yPosition) {
        super(containerIn.smithingTable.basicInventory, index, xPosition, yPosition);
        container = containerIn;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        container.detectAndSendChanges();
    }
}
