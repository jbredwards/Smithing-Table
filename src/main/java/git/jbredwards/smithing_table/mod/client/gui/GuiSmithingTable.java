/*
 * Copyright (C) <2026 to Present> <jbredwards>
 *
 * All rights are reserved, except where explicitly granted by the original
 * copyright holder or where explicitly granted by the Mod Permissions License as
 * published by Jbredwards, either version 1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See the Mod Permissions License for more details
 * <https://www.github.com/jbredwards/mod-permissions-license>.
 */

package git.jbredwards.smithing_table.mod.client.gui;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import git.jbredwards.smithing_table.api.SmithingSlotInfo;
import git.jbredwards.smithing_table.api.SmithingTemplate;
import git.jbredwards.smithing_table.mod.SmithingTable;
import git.jbredwards.smithing_table.mod.SmithingTableCfg;
import git.jbredwards.smithing_table.mod.common.inventory.ContainerSmithingTable;
import git.jbredwards.smithing_table.mod.common.inventory.SlotSmithingInput;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
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
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author jbred
 *
 */
@SideOnly(Side.CLIENT)
public class GuiSmithingTable extends GuiContainer implements IContainerListener
{
    @Nonnull public static final ResourceLocation TEXTURE = new ResourceLocation(SmithingTable.MOD_ID, "textures/gui/smithing.png");
    @Nonnull public static final ResourceLocation SLOT_BACK = new ResourceLocation(SmithingTable.MOD_ID, "gui/slot/back");

    @Nullable private static String SLOT_TEMPLATE_CACHE;
    @Nonnull private static final LoadingCache<SmithingSlotInfo, String> JOINED_TEXTURE_CACHE = CacheBuilder.newBuilder()
            .build(CacheLoader.from(info -> joinTextures(info.textures().stream())));

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
        inventorySlots.getSlot(SmithingSlotInfo.TEMPLATE).setBackgroundName(getTemplateTexture());
        inventorySlots.getSlot(SmithingSlotInfo.EQUIPMENT).setBackgroundName(SLOT_BACK.toString());
        inventorySlots.getSlot(SmithingSlotInfo.MATERIAL).setBackgroundName(SLOT_BACK.toString());
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
        sendSlotContents(containerToSend, SmithingSlotInfo.OUTPUT, itemsList.get(SmithingSlotInfo.OUTPUT));
    }

    @Override
    public void sendSlotContents(@Nonnull final Container containerToSend, final int slotInd, @Nonnull final ItemStack stack) {
        if(armorStand != null && slotInd == SmithingSlotInfo.OUTPUT) {
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
        if((inventorySlots.getSlot(SmithingSlotInfo.EQUIPMENT).getHasStack()
        || inventorySlots.getSlot(SmithingSlotInfo.MATERIAL).getHasStack())
        && !inventorySlots.getSlot(SmithingSlotInfo.OUTPUT).getHasStack()) {
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

    @Override
    protected void renderHoveredToolTip(final int x, final int y) {
        @Nullable final Slot slotUnderMouse = getSlotUnderMouse();
        if(mc.player.inventory.getItemStack().isEmpty() && slotUnderMouse != null) {
            if(slotUnderMouse.getHasStack()) renderToolTip(slotUnderMouse.getStack(), x, y);
            else if(slotUnderMouse instanceof SlotSmithingInput && slotUnderMouse.slotNumber != SmithingSlotInfo.TEMPLATE) {
                @Nullable final SmithingTemplate template = SmithingTemplate.deserialize(inventorySlots.getSlot(SmithingSlotInfo.TEMPLATE).getStack());
                if(template != null) switch(slotUnderMouse.slotNumber) {
                    case SmithingSlotInfo.EQUIPMENT:
                        if(SmithingTableCfg.equipmentOverlay && template.equipmentSlotInfo != null)
                            drawHoveringText(Arrays.asList(template.equipmentSlotInfo.tooltip().getFormattedText().split("\n")), x, y);
                        break;
                    case SmithingSlotInfo.MATERIAL:
                        if(SmithingTableCfg.materialOverlay && template.materialSlotInfo != null)
                            drawHoveringText(Arrays.asList(template.materialSlotInfo.tooltip().getFormattedText().split("\n")), x, y);
                        break;
                }
            }
        }
    }

    @Nonnull
    public static String getTemplateTexture() {
        if(SLOT_TEMPLATE_CACHE == null) SLOT_TEMPLATE_CACHE = SmithingTemplate.REGISTRY.getKeys().isEmpty() ? SLOT_BACK.toString() : joinTextures(
                SmithingTemplate.REGISTRY.getValuesCollection().stream().map(st -> st.templateSlotTexture).distinct());
        return SLOT_TEMPLATE_CACHE;
    }

    @Nonnull
    public static String joinInfoTextures(@Nonnull final SmithingSlotInfo info) {
        return info.textures().isEmpty() ? SLOT_BACK.toString() : JOINED_TEXTURE_CACHE.getUnchecked(info);
    }

    @Nonnull
    private static String joinTextures(@Nonnull final Stream<ResourceLocation> textures) {
        return textures
                .flatMap(texture -> Stream.of(texture.getNamespace(), texture.getPath()))
                .collect(Collectors.joining("/", SmithingTable.MOD_ID + ":generated/", "/interpolated"));
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
