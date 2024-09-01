/*
 * Decompiled with CFR 0.151.
 */
package client.command;

import client.command.Command;
import client.command.commands.Bind;
import client.command.commands.Binds;
import client.command.commands.ReloadConfig;
import client.command.commands.Settings;
import client.command.commands.Toggle;
import client.command.commands.Toggles;
import client.event.events.MessageSendEvent;
import client.utils.ClientUtils;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandManager {
    public ArrayList<Command> commands = new ArrayList();
    private Command commandMain;

    public void commandReceive(MessageSendEvent event) {
        if (event.message.startsWith(".")) {
            event.cancelEvent();
            AtomicBoolean b = new AtomicBoolean(false);
            this.commands.forEach(e -> {
                if (e.commandName.length() + 1 <= event.message.length() && (event.message + " ").toLowerCase().startsWith("." + e.commandName.toLowerCase() + " ")) {
                    e.action(event.message);
                    b.set(true);
                }
            });
            if (!b.get()) {
                ClientUtils.displayChatMessage("Unknown Command!");
            }
        }
    }

    public CommandManager() {
        this.commands.add(new Toggle());
        this.commands.add(new Toggles());
        this.commands.add(new Settings());
        this.commands.add(new Binds());
        this.commands.add(new Bind());
        this.commands.add(new ReloadConfig());
        this.commandMain = new Command();
    }

    public void onKey(int key) {
        this.commandMain.onKey(key);
    }
}

