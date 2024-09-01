/*
 * Decompiled with CFR 0.151.
 */
package client.event.events;

import client.event.CancellableEvent;
import client.event.SingleInstance;
import net.minecraft.network.Packet;

public class PacketSendEvent
extends CancellableEvent
implements SingleInstance {
    private Packet packet;

    public Packet getPacket() {
        return this.packet;
    }

    public PacketSendEvent(Packet packet) {
        this.packet = packet;
    }

    @Override
    public void cleanup() {
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}

