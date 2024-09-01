/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

public class MSTimer {
    long ms = System.currentTimeMillis();

    public void reset() {
        this.ms = System.currentTimeMillis();
    }

    public boolean hasPassed(long time) {
        return System.currentTimeMillis() - this.ms > time;
    }

    public long getPassed() {
        return System.currentTimeMillis() - this.ms;
    }

    public void setTime(long l) {
        this.ms = l;
    }
}

