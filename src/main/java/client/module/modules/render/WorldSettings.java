/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.render;

import client.event.events.PacketReceiveSyncEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class WorldSettings
extends Module {
    @Settings(maxValue=24000.0)
    private int time = 0;
    @Settings(maxValue=2.0)
    private float rain = 0.0f;

    public WorldSettings() {
        super("WorldSettings", 0, false, ModuleType.RENDER);
    }

    @Override
    public void onUpdate() {
        WorldSettings.mc.theWorld.setWorldTime(this.time);
        WorldSettings.mc.theWorld.setRainStrength(this.rain);
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            event.cancelEvent();
        }
    }
}

