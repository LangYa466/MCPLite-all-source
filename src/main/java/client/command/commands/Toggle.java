/*
 * Decompiled with CFR 0.151.
 */
package client.command.commands;

import client.Client;
import client.command.Command;
import client.module.Module;
import client.utils.ClientUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;

public class Toggle
extends Command {
    public Toggle() {
        super("toggle");
    }

    @Override
    public void action(String s) {
        List<Module> mods = Client.moduleManager.modules;
        boolean find = false;
        String name = "";
        for (Module mod : mods) {
            if (!s.endsWith(mod.name)) continue;
            mod.toggle();
            find = true;
            name = mod.name;
            ClientUtils.displayChatMessage("Toggled" + name + "" + mod.getState());
        }
        if (!find) {
            ClientUtils.displayChatMessage("Module Not Found");
        }
    }

    @Override
    public void onKey(int key) {
        GuiChat guichat = (GuiChat)Minecraft.getMinecraft().currentScreen;
        GuiTextField input = guichat.inputField;
        String[] split = input.getText().split(" ");
        String moduleName = null;
        Module targetModule = null;
        List<Module> modules = Client.moduleManager.modules;
        ArrayList<Module> completeModules = new ArrayList<Module>();
        if (split.length > 1) {
            moduleName = split[1];
        }
        String completeModuleName = null;
        if (moduleName == null || this.isEmptyAction) {
            if (moduleName == null) {
                this.isEmptyAction = true;
            }
            if (this.actionInt >= modules.size()) {
                this.actionInt = 0;
            }
            completeModuleName = modules.get((int)this.actionInt).name;
            ++this.actionInt;
        } else {
            for (Module module : modules) {
                if (module.name.equalsIgnoreCase(moduleName)) {
                    targetModule = module;
                    break;
                }
                if (!module.name.toLowerCase().startsWith(moduleName.toLowerCase())) continue;
                completeModules.add(module);
            }
            if (!completeModules.isEmpty()) {
                if (this.actionInt >= completeModules.size()) {
                    this.actionInt = 0;
                }
                completeModuleName = ((Module)completeModules.get((int)this.actionInt)).name;
                ++this.actionInt;
            }
        }
        if (completeModuleName != null && targetModule == null) {
            input.setText(split[0] + " " + completeModuleName);
        }
    }
}

