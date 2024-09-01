/*
 * Decompiled with CFR 0.151.
 */
package client.event.events;

import client.event.Event;
import client.event.SingleInstance;

public class SlowDownEvent
extends Event
implements SingleInstance {
    public static final SlowDownEvent INSTANCE = new SlowDownEvent();
    public float forward;
    public float strafe;

    private SlowDownEvent() {
    }

    public void slow() {
        this.strafe = 0.2f;
        this.forward = 0.2f;
    }

    public void noSlow() {
        this.strafe = 1.0f;
        this.forward = 1.0f;
    }

    @Override
    public void cleanup() {
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public float getForward() {
        return this.forward;
    }

    public float getStrafe() {
        return this.strafe;
    }
}

