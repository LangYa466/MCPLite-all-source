/*
 * Decompiled with CFR 0.151.
 */
package client.event.events;

import client.event.Event;
import client.event.SingleInstance;

public class KeyEvent
extends Event
implements SingleInstance {
    public static final KeyEvent INSTANCE = new KeyEvent();
    public boolean hasScreen;
    public int keyCode;
    public String keyName;

    private KeyEvent() {
    }

    @Override
    public void cleanup() {
        this.keyName = null;
    }
}

