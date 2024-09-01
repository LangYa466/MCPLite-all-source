/*
 * Decompiled with CFR 0.151.
 */
package client.utils.anim.impl;

import client.utils.anim.Animation;
import client.utils.anim.Direction;

public class DecelerateAnimation
extends Animation {
    public DecelerateAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public DecelerateAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    @Override
    protected double getEquation(double x) {
        return 1.0 - (x - 1.0) * (x - 1.0);
    }
}

