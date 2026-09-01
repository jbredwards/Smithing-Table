package git.jbredwards.smithing_table.mod.client.gui;

import git.jbredwards.smithing_table.mod.SmithingTable;
import git.jbredwards.smithing_table.mod.common.block.TileSmithingTable;
import git.jbredwards.smithing_table.mod.common.inventory.ContainerSmithingTable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@SideOnly(Side.CLIENT)
public class GuiSmithingTable extends GuiContainer implements IContainerListener
{
    private static final int INTERNAL_MOUSE_SCALE = 40;
    protected static final float ANGLE_X = 45 * INTERNAL_MOUSE_SCALE, ANGLE_Y = -1 * INTERNAL_MOUSE_SCALE;

    @Nonnull protected final EntityArmorStand armorStand;
    @Nonnull protected final EntityPlayer player;

    public GuiSmithingTable(@Nonnull final EntityPlayer playerIn, @Nonnull final World worldIn, final int x, final int y, final int z) {
        super(new ContainerSmithingTable(playerIn, worldIn, x, y, z));
        armorStand = new EntityArmorStand(worldIn, x, y, z);
        armorStand.setSilent(true);
        armorStand.setShowArms(true);
        armorStand.setNoBasePlate(true);
        player = playerIn;
    }

    @Override
    public void initGui() {
        super.initGui();
        inventorySlots.removeListener(this);
        inventorySlots.addListener(this);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        inventorySlots.removeListener(this);
    }

    @Override
    public void sendAllContents(@Nonnull final Container containerToSend, @Nonnull final NonNullList<ItemStack> itemsList) {
        sendSlotContents(containerToSend, TileSmithingTable.OUTPUT, itemsList.get(TileSmithingTable.OUTPUT));
    }

    @Override
    public void sendSlotContents(@Nonnull final Container containerToSend, final int slotInd, @Nonnull final ItemStack stack) {
        if(slotInd == TileSmithingTable.OUTPUT) {
            for(@Nonnull final EntityEquipmentSlot slot : EntityEquipmentSlot.values()) armorStand.setItemStackToSlot(slot, ItemStack.EMPTY);
            armorStand.setItemStackToSlot(EntityLiving.getSlotForItemStack(stack), stack);
        }
    }

    @Override
    public void sendWindowProperty(@Nonnull final Container containerIn, final int varToUpdate, final int newValue) {
        // NO-OP
    }

    @Override
    public void sendAllWindowProperties(@Nonnull final Container containerIn, @Nonnull final IInventory inventory) {
        // NO-OP
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GuiInventory.drawEntityOnScreen(guiLeft + 121, guiTop + 20, 25, ANGLE_X, ANGLE_Y, armorStand);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        GlStateManager.disableBlend();
        fontRenderer.drawString(I18n.format(SmithingTable.MOD_ID + ".container.smithingTable"), 60, 18, 4210752);
        fontRenderer.drawString(player.inventory.getDisplayName().getUnformattedText(), 8, ySize - 94, 4210752);
    }
}
