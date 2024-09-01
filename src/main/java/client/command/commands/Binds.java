/*
 * Decompiled with CFR 0.151.
 */
package client.command.commands;

import client.Client;
import client.command.Command;
import client.module.Module;
import client.utils.ClientUtils;
import org.lwjgl.input.Keyboard;

public class Binds
extends Command {
    public Binds() {
        super("Binds");
    }

    @Override
    public void action(String receivedCommand) {
        for (Module module : Client.moduleManager.modules) {
            if (module.key == 0) continue;
            ClientUtils.displayChatMessage(module.name + " \u7ed1\u5b9a\u4e86 " + Keyboard.getKeyName(module.key) + " \u952e");
        }
    }

    @Override
    public void onKey(int key) {
    }
}

