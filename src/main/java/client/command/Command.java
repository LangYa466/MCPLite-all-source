/*
 * Decompiled with CFR 0.151.
 */
package client.command;

import client.Client;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;

public class Command {
    public String commandName;
    public int actionInt = 0;
    public boolean isEmptyAction = false;

    public void action(String receivedCommand) {
    }

    public void onKey(int key) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
            GuiChat guichat = (GuiChat)Minecraft.getMinecraft().currentScreen;
            GuiTextField input = guichat.inputField;
            if (input.getText().startsWith(".") && key == 15) {
                String[] split = input.getText().split(" ");
                String halfName = null;
                String completeName = null;
                Command completeCommand = null;
                halfName = split[0].substring(1);
                ArrayList<Command> theCommands = Client.commandManager.commands;
                if (halfName.isEmpty() || this.isEmptyAction) {
                    if (this.actionInt >= theCommands.size()) {
                        this.actionInt = 0;
                    }
                    if (halfName.isEmpty()) {
                        this.isEmptyAction = true;
                    }
                    completeName = theCommands.get((int)this.actionInt).commandName;
                    ++this.actionInt;
                } else {
                    ArrayList<Command> completeCommands = new ArrayList<Command>();
                    for (Command command : Client.commandManager.commands) {
                        if (command.commandName.equalsIgnoreCase(halfName) && this.actionInt == 0) {
                            completeCommand = command;
                            break;
                        }
                        if (!command.commandName.toLowerCase().startsWith(halfName.toLowerCase())) continue;
                        completeCommands.add(command);
                    }
                    if (completeCommand == null) {
                        if (this.actionInt >= completeCommands.size()) {
                            this.actionInt = 0;
                        }
                        if (completeCommands.size() > 0) {
                            completeName = ((Command)completeCommands.get((int)this.actionInt)).commandName;
                        }
                        ++this.actionInt;
                    }
                }
                if (completeName != null) {
                    input.setText("." + completeName);
                }
                if (completeCommand != null) {
                    completeCommand.onKey(key);
                }
            } else {
                this.isEmptyAction = false;
                this.actionInt = 0;
                Client.commandManager.commands.forEach(m -> {
                    m.isEmptyAction = false;
                    m.actionInt = 0;
                });
            }
        }
    }

    public Command(String commandName) {
        this.commandName = commandName;
    }

    public Command() {
    }
}

