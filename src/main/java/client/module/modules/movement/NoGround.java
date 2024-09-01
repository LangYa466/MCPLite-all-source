/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.movement;

import client.event.events.PacketSendEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoGround
extends Module {
    @Settings(list={"Vanilla", "GrimTest"})
    private String mode = "Vanilla";

    public NoGround() {
        super("NoGround", 0, false, ModuleType.MOVEMENT);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer && ((C03PacketPlayer)event.getPacket()).onGround) {
            ((C03PacketPlayer)event.getPacket()).onGround = false;
            if (this.mode.equalsIgnoreCase("grimtest")) {
                ((C03PacketPlayer)event.getPacket()).x += 1000.0;
            }
        }
    }
}

