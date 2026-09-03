package git.jbredwards.smithing_table.mod.common.inventory;

import git.jbredwards.smithing_table.api.SmithingContent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

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
        super(Objects.requireNonNull(containerIn.smithingTable.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)), index, xPosition, yPosition);
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
        if(!thePlayer.world.isRemote) thePlayer.world.playSound(null, container.smithingTable.getPos(),
                SmithingContent.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS,
                1, MathHelper.nextFloat(thePlayer.getRNG(), 0.9f, 1));

        return super.onTake(thePlayer, stack);
    }
}
