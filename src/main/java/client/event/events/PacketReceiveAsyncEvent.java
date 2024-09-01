/*
 * Decompiled with CFR 0.151.
 */
package client.event.events;

import client.event.CancellableEvent;
import client.event.SingleInstance;
import net.minecraft.network.Packet;

public class PacketReceiveAsyncEvent
extends CancellableEvent
implements SingleInstance {
    private Packet packet;

    public PacketReceiveAsyncEvent(Packet packet) {
        this.packet = packet;
    }

    @Override
    public void cleanup() {
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}

