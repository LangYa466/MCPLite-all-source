/*
 * Decompiled with CFR 0.151.
 */
package client.event.events;

import client.event.Event;
import client.event.SingleInstance;

public class StrafeEvent
extends Event
implements SingleInstance {
    public static final StrafeEvent INSTANCE = new StrafeEvent();
    public float strafe;
    public float forward;
    public float friction;
    public float yaw;

    private StrafeEvent() {
    }

    public StrafeEvent(float rotationYaw) {
        this.yaw = rotationYaw;
    }

    @Override
    public void cleanup() {
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getStrafe() {
        return this.strafe;
    }

    public float getForward() {
        return this.forward;
    }

    public float getFriction() {
        return this.friction;
    }

    public float getYaw() {
        return this.yaw;
    }
}

