/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.utils.TimeUtil;
import net.minecraft.network.Packet;

public class DelayedPacket {
    private final Packet<?> packet;
    private final TimeUtil timer;

    public DelayedPacket(Packet<?> packet) {
        this.packet = packet;
        this.timer = new TimeUtil();
    }

    public <T extends Packet<?>> T getPacket() {
        return (T)this.packet;
    }

    public TimeUtil getTimer() {
        return this.timer;
    }
}

