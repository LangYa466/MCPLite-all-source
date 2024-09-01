/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.world;

import client.event.events.PacketSendEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Nofall
extends Module {
    @Settings(list={"Hypixel", "Test"})
    private String mode = "Hypixel";

    public Nofall() {
        super("NoFall", 0, false, ModuleType.WORLD);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (this.mode.equalsIgnoreCase("Test")) {
            if (Nofall.mc.thePlayer.fallDistance > 3.0f) {
                ((C03PacketPlayer)event.getPacket()).onGround = false;
                if (Nofall.mc.thePlayer.onGround) {
                    Nofall.mc.thePlayer.jump();
                }
            }
        } else {
            ((C03PacketPlayer)event.getPacket()).onGround = true;
        }
        if (this.mode.equalsIgnoreCase("Hypixel")) {
            ((C03PacketPlayer)event.getPacket()).onGround = false;
        }
    }
}

