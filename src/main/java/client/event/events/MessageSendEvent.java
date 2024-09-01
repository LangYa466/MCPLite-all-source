/*
 * Decompiled with CFR 0.151.
 */
package client.event.events;

import client.event.CancellableEvent;
import client.event.SingleInstance;

public class MessageSendEvent
extends CancellableEvent
implements SingleInstance {
    public String message;
    public boolean addToChat;

    public MessageSendEvent() {
    }

    public MessageSendEvent(String msg, boolean addToChat) {
        this.message = msg;
        this.addToChat = addToChat;
    }

    @Override
    public void cleanup() {
    }
}

