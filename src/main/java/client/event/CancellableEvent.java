/*
 * Decompiled with CFR 0.151.
 */
package client.event;

import client.event.Event;

public abstract class CancellableEvent
extends Event {
    private boolean isCancelled = false;

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void cancelEvent() {
        this.isCancelled = true;
    }

    protected void cleanup() {
        this.isCancelled = false;
    }
}

