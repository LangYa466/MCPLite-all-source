/*
 * Decompiled with CFR 0.151.
 */
package client.command.commands;

import client.Client;
import client.command.Command;

public class ReloadConfig
extends Command {
    public ReloadConfig() {
        super("reload");
    }

    @Override
    public void action(String receivedCommand) {
        try {
            Client.configManager.load();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

