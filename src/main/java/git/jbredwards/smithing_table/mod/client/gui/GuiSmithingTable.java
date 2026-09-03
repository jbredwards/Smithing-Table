package git.jbredwards.smithing_table.mod.client.gui;

import git.jbredwards.smithing_table.mod.SmithingTable;
import git.jbredwards.smithing_table.mod.SmithingTableCfg;
import git.jbredwards.smithing_table.mod.common.block.TileSmithingTable;
import git.jbredwards.smithing_table.mod.common.inventory.ContainerSmithingTable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
@SideOnly(Side.CLIENT)
public class GuiSmithingTable extends GuiContainer implements IContainerListener
{
    @Nonnull public static final ResourceLocation TEXTURE = new ResourceLocation(SmithingTable.MOD_ID, "textures/gui/smithing.png");

    private static final int INTERNAL_MOUSE_SCALE = 40;
    protected static final float ANGLE_X = 60 * INTERNAL_MOUSE_SCALE, ANGLE_Y = -1.5f * INTERNAL_MOUSE_SCALE;
    protected int xOffset;

    @Nonnull protected final ITextComponent name;
    @Nullable protected final EntityArmorStand armorStand;
    @Nonnull protected final EntityPlayer player;

    public GuiSmithingTable(@Nonnull final EntityPlayer playerIn, @Nonnull final World worldIn, final int x, final int y, final int z) {
        super(new ContainerSmithingTable(playerIn, worldIn, x, y, z));
        name = ((IWorldNameable)inventorySlots).getDisplayName();
        if(SmithingTableCfg.armorStand) {
            armorStand = new EntityArmorStand(worldIn, x, y, z);
            armorStand.setSilent(true);
            armorStand.setShowArms(true);
            armorStand.setNoBasePlate(true);
        }
        else armorStand = null;
        player = playerIn;
        xOffset = 0;
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
        if(armorStand != null && slotInd == TileSmithingTable.OUTPUT) {
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

        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color(1, 1, 1);
        mc.getTextureManager().bindTexture(TEXTURE);

        // Draw main background.
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        if(SmithingTableCfg.armorStandBackground) drawTexturedModalRect(guiLeft + 115, guiTop, 176, 0, 61, 83);

        // Draw slots background.
        drawTexturedModalRect(guiLeft + 7 + xOffset, guiTop + 7, 176, SmithingTableCfg.hammer.yOffset, 30, 30);
        drawTexturedModalRect(guiLeft + 7 + xOffset, guiTop + 47, 0, 166, 108, 18);
        if((inventorySlots.getSlot(TileSmithingTable.EQUIPMENT).getHasStack()
        || inventorySlots.getSlot(TileSmithingTable.MATERIAL).getHasStack())
        && !inventorySlots.getSlot(TileSmithingTable.OUTPUT).getHasStack()) {
            drawTexturedModalRect(guiLeft + 68 + xOffset, guiTop + 49, 108, 166, 22, 15);
        }

        // Draw armor stand.
        if(armorStand != null) GuiInventory.drawEntityOnScreen(guiLeft + 145, guiTop + 65, 25, ANGLE_X, ANGLE_Y, armorStand);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        GlStateManager.disableBlend();
        final int x = SmithingTableCfg.hammer == SmithingTableCfg.HammerType.NONE ? 8 : 44;
        fontRenderer.drawString(name.getUnformattedText(), x + xOffset, 15, 4210752);
        fontRenderer.drawString(player.inventory.getDisplayName().getUnformattedText(), 8, ySize - 94, 4210752);
    }

    /**
     * This class only exists to fix the JEI "clickable area".
     * @author jbred
     *
     */
    @SideOnly(Side.CLIENT)
    public static class Sub extends GuiSmithingTable
    {
        public Sub(@Nonnull final EntityPlayer playerIn, @Nonnull final World worldIn, final int x, final int y, final int z) {
            super(playerIn, worldIn, x, y, z);
            xOffset = 27;
        }
    }
}
