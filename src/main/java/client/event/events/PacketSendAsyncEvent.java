/*
 * Decompiled with CFR 0.151.
 */
package client.event.events;

import client.event.CancellableEvent;
import net.minecraft.network.Packet;

public class PacketSendAsyncEvent
extends CancellableEvent {
    private Packet packet;

    public PacketSendAsyncEvent(Packet packet) {
        this.packet = packet;
    }

    public void _setPacket(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }
}

