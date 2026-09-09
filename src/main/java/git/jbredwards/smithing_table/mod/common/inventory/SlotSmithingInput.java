package git.jbredwards.smithing_table.mod.common.inventory;

import git.jbredwards.smithing_table.api.SmithingSlotInfo;
import git.jbredwards.smithing_table.api.SmithingTemplate;
import git.jbredwards.smithing_table.mod.SmithingTableCfg;
import git.jbredwards.smithing_table.mod.client.gui.GuiSmithingTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    @Nullable
    @SideOnly(Side.CLIENT)
    @Override
    public String getSlotTexture() {
        if(slotNumber != SmithingSlotInfo.TEMPLATE) {
            @Nullable final SmithingTemplate template = SmithingTemplate.deserialize(getItemHandler().getStackInSlot(SmithingSlotInfo.TEMPLATE));
            if(template != null) switch(slotNumber) {
                case SmithingSlotInfo.EQUIPMENT:
                    return !SmithingTableCfg.equipmentOverlay || template.equipmentSlotInfo == null
                            ? null : GuiSmithingTable.joinInfoTextures(template.equipmentSlotInfo);
                case SmithingSlotInfo.MATERIAL:
                    return !SmithingTableCfg.materialOverlay || template.materialSlotInfo == null
                            ? null : GuiSmithingTable.joinInfoTextures(template.materialSlotInfo);
            }
        }
        else if(!SmithingTableCfg.templateOverlay) return null;
        return super.getSlotTexture();
    }
}
