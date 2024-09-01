/*
 * Decompiled with CFR 0.151.
 */
package client.event.events;

import client.event.Event;

public class Render2DEvent
extends Event {
    public float partialTicks;

    public Render2DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}

