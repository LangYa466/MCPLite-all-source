/*
 * Decompiled with CFR 0.151.
 */
package client.command.commands;

import client.Client;
import client.command.Command;
import client.utils.ClientUtils;

public class Toggles
extends Command {
    public Toggles() {
        super("toggles");
    }

    @Override
    public void action(String receivedCommand) {
        ClientUtils.displayChatMessage("\u60a8\u5df2\u7ecf\u6253\u5f00\u4e86:");
        if (Client.moduleManager.modules != null) {
            Client.moduleManager.modules.forEach(e -> {
                if (e.getState()) {
                    ClientUtils.displayChatMessage(e.name);
                }
            });
        }
    }

    @Override
    public void onKey(int key) {
    }
}

