/*
 * Decompiled with CFR 0.151.
 */
package client.utils.anim;

import client.utils.anim.Animation;
import client.utils.anim.Direction;
import client.utils.anim.impl.SmoothStepAnimation;

public class ContinualAnimation {
    private float output;
    private float endpoint;
    private Animation animation = new SmoothStepAnimation(0, 0.0, Direction.BACKWARDS);

    public void animate(float destination, int ms) {
        this.output = this.endpoint - this.animation.getOutput().floatValue();
        this.endpoint = destination;
        if (this.output != this.endpoint - destination) {
            this.animation = new SmoothStepAnimation(ms, this.endpoint - this.output, Direction.BACKWARDS);
        }
    }

    public boolean isDone() {
        return this.output == this.endpoint || this.animation.isDone();
    }

    public float getOutput() {
        this.output = this.endpoint - this.animation.getOutput().floatValue();
        return this.output;
    }

    public Animation getAnimation() {
        return this.animation;
    }
}

