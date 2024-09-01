/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.movement;

import client.event.events.PacketReceiveSyncEvent;
import client.event.events.PacketSendEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.MovementUtils;
import client.utils.PacketUtils;
import client.utils.rotation.RotationSetter;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import org.lwjgl.util.vector.Vector2f;

public class Stuck
extends Module {
    public static Stuck INSTANCE = new Stuck();
    @Settings
    public boolean autoClose = true;

    public Stuck() {
        super("Stuck", 0, true, ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
        MovementUtils.cancelMove();
    }

    @Override
    public void onDisable() {
        MovementUtils.resetMove();
        Stuck.mc.thePlayer.positionUpdateTicks = PacketUtils.noMovePackets;
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            this.setState(false);
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            if (event.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook || event.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook || event.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
                return;
            }
            event.cancelEvent();
        }
    }

    @Override
    public void onUpdate() {
        Stuck.mc.thePlayer.positionUpdateTicks = 0;
    }

    public static void throwPearl(Vector2f current) {
        if (!INSTANCE.getState()) {
            return;
        }
        Stuck.mc.thePlayer.rotationYaw = current.x;
        Stuck.mc.thePlayer.rotationPitch = current.y;
        float f = Stuck.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float gcd = f * f * f * 1.2f;
        current.x -= current.x % gcd;
        current.y -= current.y % gcd;
        RotationSetter.setRotation(new Vector2f(current.x, current.y), 0);
        PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(Stuck.mc.thePlayer.getHeldItem()));
    }
}

