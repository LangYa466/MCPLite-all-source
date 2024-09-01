/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.sun.istack.internal.NotNull
 */
package client.module.modules.movement;

import client.Client;
import client.event.events.MotionEvent;
import client.event.events.PacketSendEvent;
import client.event.events.SlowDownEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.combat.KillAura;
import client.module.modules.misc.ClientSettings;
import client.module.modules.misc.Gapple;
import client.utils.PacketUtils;
import client.utils.PlayerUtils;
import com.sun.istack.internal.NotNull;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlow
extends Module {
    @Settings
    private boolean bowNoSlow = false;
    @Settings
    private boolean swordNoSlow = true;
    @Settings
    private boolean foodNoSlow = false;
    @Settings
    private boolean swordPreC07 = false;
    @Settings
    private boolean swordPostC08 = true;
    @Settings
    private boolean swordPreDoubleC09 = false;
    @Settings
    private boolean swordPreC09postC09 = false;
    @Settings
    private boolean bowPreC09 = false;
    @Settings
    private boolean foodPreC09 = false;
    @Settings
    private boolean hypPacket = false;
    private boolean postPlace;
    private int offGroundTicks = 0;
    private boolean send = false;

    public NoSlow() {
        super("NoSlow", 0, true, ModuleType.MOVEMENT);
    }

    @Override
    public void onPreUpdate() {
    }

    @Override
    public void onDisable() {
        this.offGroundTicks = 0;
        this.send = false;
    }

    @Override
    public void onMotion(MotionEvent event) {
        Gapple gapple = (Gapple)Client.moduleManager.moduleMap.get(Gapple.class);
        if (event.isPre()) {
            if (ClientSettings.INSTANCE.onHyt && (Gapple.eating || Gapple.pulsing || gapple.getState())) {
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(NoSlow.mc.thePlayer.getHeldItem()));
                return;
            }
            if (this.hypPacket) {
                this.offGroundTicks = NoSlow.mc.thePlayer.onGround ? 0 : ++this.offGroundTicks;
                ItemStack item = NoSlow.mc.thePlayer.getHeldItem();
                if (this.offGroundTicks == 2 && this.send) {
                    this.send = false;
                    PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, item, 0.0f, 0.0f, 0.0f));
                } else if (item != null && NoSlow.mc.thePlayer.isUsingItem() && (PlayerUtils.isRest(item.getItem()) || item.getItem() instanceof ItemBow)) {
                    event.setPosY(event.getPosY() + 1.0E-14);
                }
            }
            KillAura killAura = (KillAura)Client.moduleManager.moduleMap.get(KillAura.class);
            if (PacketUtils.packetBlocking && !NoSlow.mc.thePlayer.isBlocking() || NoSlow.mc.thePlayer.isBlocking() || killAura.isBlocking || NoSlow.mc.thePlayer.getHeldItem() != null && NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && (NoSlow.mc.thePlayer.isUsingItem() || GameSettings.isKeyDown(NoSlow.mc.gameSettings.keyBindUseItem))) {
                if (this.swordPreC07) {
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(NoSlow.mc.thePlayer.getHeldItem()));
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(NoSlow.mc.thePlayer.getHeldItem()));
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                }
                if (this.swordPreDoubleC09) {
                    this.switchC09(false);
                }
                if (this.swordPreC09postC09) {
                    this.switchOut();
                }
            }
            if ((NoSlow.mc.thePlayer.isUsingItem() || NoSlow.mc.gameSettings.keyBindUseItem.pressed) && NoSlow.mc.thePlayer.getHeldItem() != null) {
                if (NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && this.bowPreC09) {
                    this.switchC09(true);
                }
                if (NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && this.foodPreC09) {
                    this.switchC09(false);
                }
            }
        } else if (event.isPost()) {
            KillAura killAura = (KillAura)Client.moduleManager.moduleMap.get(KillAura.class);
            if (this.swordPreC09postC09) {
                this.switchBack();
            }
            if ((NoSlow.mc.thePlayer.isBlocking() || killAura.isBlocking || NoSlow.mc.thePlayer.getHeldItem() != null && NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && (NoSlow.mc.thePlayer.isUsingItem() || GameSettings.isKeyDown(NoSlow.mc.gameSettings.keyBindUseItem))) && this.swordPostC08) {
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(NoSlow.mc.thePlayer.getHeldItem()));
            }
            if (this.postPlace) {
                if (NoSlow.mc.thePlayer.ticksExisted % 3 == 0) {
                    NoSlow.mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(NoSlow.mc.thePlayer.getHeldItem()));
                }
                this.postPlace = false;
            }
        }
    }

    @Override
    public void onUpdate() {
        this.postPlace = false;
        if (!NoSlow.mc.thePlayer.isUsingItem() || PlayerUtils.holdingSword()) {
            return;
        }
        if (this.hypPacket && NoSlow.mc.thePlayer.ticksExisted % 3 == 0) {
            NoSlow.mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 1, null, 0.0f, 0.0f, 0.0f));
        }
    }

    @Override
    public void onPacketSend(@NotNull PacketSendEvent event) {
        if (this.hypPacket && event.getPacket() instanceof C08PacketPlayerBlockPlacement && !NoSlow.mc.thePlayer.isUsingItem()) {
            C08PacketPlayerBlockPlacement blockPlacement = (C08PacketPlayerBlockPlacement)event.getPacket();
            if (NoSlow.mc.thePlayer.getHeldItem() != null && blockPlacement.getPlacedBlockDirection() == 255 && PlayerUtils.isRest(NoSlow.mc.thePlayer.getHeldItem().getItem()) && this.offGroundTicks < 2) {
                if (NoSlow.mc.thePlayer.onGround) {
                    NoSlow.mc.thePlayer.setJumping(false);
                    NoSlow.mc.thePlayer.jump();
                }
                this.send = true;
                event.cancelEvent();
            }
        }
    }

    @Override
    public void onPostUpdate() {
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onSlowDown(SlowDownEvent event) {
        if (NoSlow.mc.thePlayer.getHeldItem() == null || Gapple.eating) {
            return;
        }
        if (this.swordNoSlow && NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            event.setForward(1.0f);
            event.setStrafe(1.0f);
        }
        if (this.foodNoSlow && NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) {
            event.setForward(1.0f);
            event.setStrafe(1.0f);
        }
        if (this.bowNoSlow && NoSlow.mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) {
            event.setForward(1.0f);
            event.setStrafe(1.0f);
        }
    }

    private void switchC09(boolean middlePacket) {
        this.switchOut();
        this.switchBack();
    }

    private void switchOut() {
        ItemStack item;
        int slot;
        for (slot = 0; slot < 9 && (item = NoSlow.mc.thePlayer.inventory.getStackInSlot(slot)) != null && (item.getItem() instanceof ItemBow || item.getItem() instanceof ItemFood || item.getItem() instanceof ItemSword || item.getItem() instanceof ItemEnderPearl); ++slot) {
            if (slot != 8) continue;
            slot = (NoSlow.mc.thePlayer.inventory.currentItem + 1) % 9;
            break;
        }
        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(slot));
    }

    private void switchBack() {
        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(NoSlow.mc.thePlayer.inventory.currentItem));
    }
}

