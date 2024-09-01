/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.utils.anim.Animation;
import client.utils.anim.Direction;
import client.utils.anim.impl.SmoothStepAnimation;
import org.lwjgl.input.Mouse;

public class Scroll {
    private float maxScroll = Float.MAX_VALUE;
    private float minScroll = 0.0f;
    private float rawScroll = 0.0f;
    private float scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0.0, Direction.BACKWARDS);

    public void onScroll(int ms) {
        this.scroll = this.rawScroll - this.scrollAnimation.getOutput().floatValue();
        this.rawScroll += (float)Mouse.getDWheel() / 4.0f;
        this.rawScroll = Math.max(Math.min(this.minScroll, this.rawScroll), -this.maxScroll);
        this.scrollAnimation = new SmoothStepAnimation(ms, this.rawScroll - this.scroll, Direction.BACKWARDS);
    }

    public boolean isScrollAnimationDone() {
        return this.scrollAnimation.isDone();
    }

    public float getScroll() {
        this.scroll = this.rawScroll - this.scrollAnimation.getOutput().floatValue();
        return this.scroll;
    }

    public float getMaxScroll() {
        return this.maxScroll;
    }

    public float getMinScroll() {
        return this.minScroll;
    }

    public float getRawScroll() {
        return this.rawScroll;
    }

    public void setMaxScroll(float maxScroll) {
        this.maxScroll = maxScroll;
    }

    public void setMinScroll(float minScroll) {
        this.minScroll = minScroll;
    }

    public void setRawScroll(float rawScroll) {
        this.rawScroll = rawScroll;
    }

    public Animation getScrollAnimation() {
        return this.scrollAnimation;
    }
}

