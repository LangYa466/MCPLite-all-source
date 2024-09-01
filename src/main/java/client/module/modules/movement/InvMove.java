/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.movement;

import client.event.events.PacketReceiveSyncEvent;
import client.event.events.PacketSendEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.BlinkUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

public class InvMove
extends Module {
    @Settings(list={"Normal", "Hypixel"})
    private String mode = "Normal";
    private boolean invOpened = false;
    private boolean started = false;

    public InvMove() {
        super("InvMove", 0, true, ModuleType.MOVEMENT);
    }

    @Override
    public void onTick() {
        KeyBinding[] keyBindings = new KeyBinding[]{InvMove.mc.gameSettings.keyBindJump, InvMove.mc.gameSettings.keyBindForward, InvMove.mc.gameSettings.keyBindBack, InvMove.mc.gameSettings.keyBindLeft, InvMove.mc.gameSettings.keyBindRight};
        if (InvMove.mc.currentScreen != null && this.mode.equalsIgnoreCase("normal")) {
            if (InvMove.mc.currentScreen instanceof GuiChat) {
                return;
            }
            for (KeyBinding key : keyBindings) {
                key.pressed = GameSettings.isKeyDown(key);
            }
        }
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        Packet packet = event.getPacket();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (this.mode.equalsIgnoreCase("hypixel")) {
            if (event.getPacket() instanceof C16PacketClientStatus && ((C16PacketClientStatus)event.getPacket()).getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                this.invOpened = true;
                BlinkUtils.blink(C0EPacketClickWindow.class, C0DPacketCloseWindow.class, C16PacketClientStatus.class);
            }
            if (this.invOpened && event.getPacket() instanceof C0DPacketCloseWindow) {
                this.invOpened = false;
                BlinkUtils.stopBlink();
            }
        }
    }

    @Override
    public void onDisable() {
        if (!GameSettings.isKeyDown(InvMove.mc.gameSettings.keyBindForward) || InvMove.mc.currentScreen != null) {
            InvMove.mc.gameSettings.keyBindForward.pressed = false;
        }
        if (!GameSettings.isKeyDown(InvMove.mc.gameSettings.keyBindBack) || InvMove.mc.currentScreen != null) {
            InvMove.mc.gameSettings.keyBindBack.pressed = false;
        }
        if (!GameSettings.isKeyDown(InvMove.mc.gameSettings.keyBindRight) || InvMove.mc.currentScreen != null) {
            InvMove.mc.gameSettings.keyBindRight.pressed = false;
        }
        if (!GameSettings.isKeyDown(InvMove.mc.gameSettings.keyBindLeft) || InvMove.mc.currentScreen != null) {
            InvMove.mc.gameSettings.keyBindLeft.pressed = false;
        }
        if (!GameSettings.isKeyDown(InvMove.mc.gameSettings.keyBindJump) || InvMove.mc.currentScreen != null) {
            InvMove.mc.gameSettings.keyBindJump.pressed = false;
        }
        if (!GameSettings.isKeyDown(InvMove.mc.gameSettings.keyBindSprint) || InvMove.mc.currentScreen != null) {
            InvMove.mc.gameSettings.keyBindSprint.pressed = false;
        }
        this.started = false;
    }
}

