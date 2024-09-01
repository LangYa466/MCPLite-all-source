/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.combat;

import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.BlinkUtils;
import client.utils.ClientUtils;
import client.utils.InventoryUtils;
import client.utils.rotation.RotationSetter;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector2f;

public class AutoPot extends Module {
    @Settings
    private boolean autoSoup = false;
    @Settings(list={"BlinkTwoTicks", "OneTick"})
    private String switchMode = "BlinkTwoTicks";
    @Settings
    private boolean autoPotion = false;
    @Settings(list={"Down", "Back"})
    private String autoPotionRotationMode = "Down";
    @Settings(minValue=70.0, maxValue=90.0)
    private float backPitch = 85.0f;
    @Settings(maxValue=20.0)
    private float health = 20.0f;
    private boolean switchBackSoup;
    private boolean switchBackPotion;
    private int c09Slot = 0;
    private Vector2f rotation = null;
    ItemStack splashPotionStack = null;

    public AutoPot() {
        super("AutoPot", 0, false, ModuleType.COMBAT);
    }

    @Override
    public void onTick() {
        if (ClientUtils.nullCheck()) {
            return;
        }
        int soupInHotbar = InventoryUtils.findItem(36, 45, Items.mushroom_stew);
        if (this.autoSoup) {
            int soupInInventory;
            boolean openInventory;
            if (this.switchBackSoup && this.switchMode.equalsIgnoreCase("blinktwoticks")) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(AutoPot.mc.thePlayer.inventory.currentItem));
                BlinkUtils.stopBlink();
                this.switchBackSoup = false;
                return;
            }
            if (AutoPot.mc.thePlayer.getHealth() <= this.health && soupInHotbar != -1) {
                if (this.switchMode.equalsIgnoreCase("blinktwoticks")) {
                    BlinkUtils.blink();
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(soupInHotbar - 36));
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(AutoPot.mc.thePlayer.inventoryContainer.getSlot(soupInHotbar).getStack()));
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    this.switchBackSoup = true;
                }
                if (this.switchMode.equalsIgnoreCase("onetick")) {
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(soupInHotbar - 36));
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(AutoPot.mc.thePlayer.inventoryContainer.getSlot(soupInHotbar).getStack()));
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(AutoPot.mc.thePlayer.inventory.currentItem));
                }
                if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_17)) {
                    AutoPot.mc.thePlayer.swingItem();
                }
                return;
            }
            int bowlInHotbar = InventoryUtils.findItem(36, 45, Items.bowl);
            if (bowlInHotbar != -1) {
                boolean bowlMovable = false;
                for (int i = 9; i < 36; ++i) {
                    ItemStack itemStack = AutoPot.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (itemStack == null) {
                        bowlMovable = true;
                        break;
                    }
                    if (itemStack.getItem() != Items.bowl || itemStack.stackSize >= 64) continue;
                    bowlMovable = true;
                    break;
                }
                if (bowlMovable) {
                    boolean bl = openInventory = !(AutoPot.mc.currentScreen instanceof GuiInventory);
                    if (openInventory) {
                        mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    }
                    AutoPot.mc.playerController.windowClick(0, bowlInHotbar, 0, 1, AutoPot.mc.thePlayer);
                }
            }
            if ((soupInInventory = InventoryUtils.findItem(9, 36, Items.mushroom_stew)) != -1 && InventoryUtils.hasSpaceHotbar()) {
                boolean bl = openInventory = !(AutoPot.mc.currentScreen instanceof GuiInventory);
                if (openInventory) {
                    mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                }
                AutoPot.mc.playerController.windowClick(0, soupInInventory, 0, 1, AutoPot.mc.thePlayer);
                if (openInventory) {
                    mc.getNetHandler().addToSendQueue(new C0DPacketCloseWindow());
                }
            }
        }
        if (this.autoPotion) {
            if (this.switchBackPotion) {
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(this.splashPotionStack));
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(AutoPot.mc.thePlayer.inventory.currentItem));
                this.switchBackPotion = false;
                BlinkUtils.stopBlink();
                return;
            }
            this.c09Slot = 0;
            block1: for (int i = 36; i < 45; ++i) {
                List<Integer> needPotions = Arrays.asList(Potion.heal.getId(), Potion.nightVision.getId(), Potion.digSpeed.getId(), Potion.healthBoost.getId(), Potion.moveSpeed.getId(), Potion.fireResistance.getId(), Potion.regeneration.getId());
                Slot slot = AutoPot.mc.thePlayer.inventoryContainer.getSlot(i);
                if (slot.getHasStack() && slot.getStack().getItem() instanceof ItemPotion && ItemPotion.isSplash(slot.getStack().getMetadata())) {
                    for (PotionEffect potionEffect : ((ItemPotion)slot.getStack().getItem()).getEffects(slot.getStack())) {
                        if (!needPotions.contains(potionEffect.getPotionID())) continue;
                        this.splashPotionStack = slot.getStack();
                        break block1;
                    }
                }
                ++this.c09Slot;
            }
            if (this.splashPotionStack != null) {
                if (this.autoPotionRotationMode.equalsIgnoreCase("down")) {
                    this.rotation = new Vector2f(AutoPot.mc.thePlayer.rotationYaw, 90.0f);
                }
                if (this.autoPotionRotationMode.equalsIgnoreCase("back")) {
                    this.rotation = new Vector2f(AutoPot.mc.thePlayer.rotationYaw - 180.0f, this.backPitch);
                }
                RotationSetter.setRotation(this.rotation, 0);
            }
        }
    }

    @Override
    public void onUpdate() {
        if (this.autoPotion && this.rotation != null && RotationSetter.targetRotation != null && this.rotation.y == RotationSetter.targetRotation.y && this.rotation.x == RotationSetter.targetRotation.x) {
            BlinkUtils.blink(new Class[0]);
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(this.c09Slot));
            this.splashPotionStack = null;
            this.rotation = null;
            this.switchBackPotion = true;
        }
    }

    @Override
    public String getTag() {
        return this.switchMode;
    }
}

