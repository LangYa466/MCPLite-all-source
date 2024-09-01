/*
 * Decompiled with CFR 0.151.
 */
package client.utils.anim.impl;

import client.utils.anim.Animation;
import client.utils.anim.Direction;

public class EaseOutSine
extends Animation {
    public EaseOutSine(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public EaseOutSine(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    @Override
    protected boolean correctOutput() {
        return true;
    }

    @Override
    protected double getEquation(double x) {
        return Math.sin(x * 1.5707963267948966);
    }
}

