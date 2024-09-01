/*
 * Decompiled with CFR 0.151.
 */
package client.event.events;

import client.event.Event;

public class Render3DEventAfterHand
extends Event {
    public float partialTicks;

    public Render3DEventAfterHand(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}

