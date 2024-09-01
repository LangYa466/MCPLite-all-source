/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.event.events.MotionEvent;
import client.utils.MinecraftInstance;

public final class FallDistanceComponent
extends MinecraftInstance {
    public static FallDistanceComponent INSTANCE = new FallDistanceComponent();
    public static float distance;
    private float lastDistance;

    public void onMotion(MotionEvent event) {
        if (event.isPre()) {
            if (FallDistanceComponent.mc.thePlayer == null || FallDistanceComponent.mc.theWorld == null) {
                return;
            }
            float fallDistance = FallDistanceComponent.mc.thePlayer.fallDistance;
            if (fallDistance == 0.0f) {
                distance = 0.0f;
            }
            distance += fallDistance - this.lastDistance;
            this.lastDistance = fallDistance;
        }
    }
}

