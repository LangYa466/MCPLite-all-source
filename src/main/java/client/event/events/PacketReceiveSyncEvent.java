/*
 * Decompiled with CFR 0.151.
 */
package client.event.events;

import client.event.CancellableEvent;
import client.event.SingleInstance;
import net.minecraft.network.Packet;

public class PacketReceiveSyncEvent
extends CancellableEvent
implements SingleInstance {
    private Packet packet;

    public PacketReceiveSyncEvent(Packet packet) {
        this.packet = packet;
    }

    @Override
    public void cleanup() {
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }
}

