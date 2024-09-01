/*
 * Decompiled with CFR 0.151.
 */
package client.event.events;

import client.event.Event;

public class MotionEvent
extends Event {
    private float yaw;
    private float pitch;
    public double posY;
    private boolean isPre;
    private boolean isPost;
    private boolean onGround;

    public MotionEvent(boolean isPre, double posY, float yaw, float pitch, boolean onGround) {
        this.isPre = isPre;
        this.isPost = !isPre;
        this.posY = posY;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosY() {
        return this.posY;
    }

    public boolean isPre() {
        return this.isPre;
    }

    public boolean isPost() {
        return this.isPost;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isOnGround() {
        return this.onGround;
    }
}

