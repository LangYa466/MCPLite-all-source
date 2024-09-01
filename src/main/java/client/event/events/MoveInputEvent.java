/*
 * Decompiled with CFR 0.151.
 */
package client.event.events;

import client.event.CancellableEvent;
import client.event.SingleInstance;

public class MoveInputEvent
extends CancellableEvent
implements SingleInstance {
    public static final MoveInputEvent INSTANCE = new MoveInputEvent();
    private float forward;
    private float strafe;
    private boolean jump;
    private boolean sneak;
    private double sneakSlowDownMultiplier;

    private MoveInputEvent() {
    }

    public MoveInputEvent(float moveForward, float moveStrafe, boolean jump, boolean sneak, double v) {
        this.forward = moveForward;
        this.strafe = moveStrafe;
        this.jump = jump;
        this.sneak = sneak;
        this.sneakSlowDownMultiplier = v;
    }

    @Override
    public void cleanup() {
    }

    public float getForward() {
        return this.forward;
    }

    public float getStrafe() {
        return this.strafe;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public boolean isJump() {
        return this.jump;
    }

    public boolean isSneak() {
        return this.sneak;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    public double getSneakSlowDownMultiplier() {
        return this.sneakSlowDownMultiplier;
    }

    public void setSneakSlowDownMultiplier(double sneakSlowDownMultiplier) {
        this.sneakSlowDownMultiplier = sneakSlowDownMultiplier;
    }
}

