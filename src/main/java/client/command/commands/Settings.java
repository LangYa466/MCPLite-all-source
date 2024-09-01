/*
 * Decompiled with CFR 0.151.
 */
package client.command.commands;

import client.Client;
import client.command.Command;
import client.module.Module;
import client.utils.ClientUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;

public class Settings
extends Command {
    private Module targetModule = null;

    public Settings() {
        super("settings");
    }

    @Override
    public void action(String receivedCommand) {
        String moduleName = null;
        String fieldName = null;
        String fieldValue = null;
        try {
            moduleName = receivedCommand.split(" ")[1];
            fieldName = receivedCommand.split(" ")[2];
            fieldValue = receivedCommand.split(" ")[3];
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (moduleName == null) {
            ClientUtils.displayChatMessage("\u6a21\u5757\u672a\u627e\u5230");
            return;
        }
        for (Module mod : Client.moduleManager.modules) {
            if (!mod.name.equalsIgnoreCase(moduleName)) continue;
            Field field = null;
            try {
                field = mod.getClass().getDeclaredField(fieldName);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (field == null) {
                ClientUtils.displayChatMessage("\u627e\u4e0d\u5230\u8981\u8bbe\u7f6e\u7684\u503c");
                ClientUtils.displayChatMessage("\u4ee5\u4e0b\u662f\u8be5\u6a21\u5757\u6240\u6709\u7684\u503c:");
                try {
                    for (Field f : mod.getClass().getDeclaredFields()) {
                        ClientUtils.displayChatMessage("Name: " + f.getName() + " Type: " + f.getType());
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            try {
                if (Modifier.isPrivate(field.getModifiers())) {
                    field.setAccessible(true);
                }
                if (fieldValue != null) {
                    if (field.getType() == Boolean.class || field.getType() == Boolean.TYPE) {
                        this.setField(field, Boolean.valueOf(fieldValue), mod);
                    }
                    if (field.getType() == Integer.TYPE || field.getType() == Integer.class) {
                        this.setField(field, Integer.valueOf(fieldValue), mod);
                    }
                    if (field.getType() == Double.class || field.getType() == Double.TYPE) {
                        this.setField(field, Double.valueOf(fieldValue), mod);
                    }
                    if (field.getType() == Float.class || field.getType() == Float.TYPE) {
                        this.setField(field, Float.valueOf(fieldValue), mod);
                    }
                }
                ClientUtils.displayChatMessage(field.getName() + "\u7684\u5f53\u524d\u503c\u4e3a:" + field.get(mod) + " \u7c7b\u578b\u4e3a:" + field.getType());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setField(Field field, Object value, Module module) {
        try {
            field.set(module, value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onKey(int key) {
        GuiChat guichat = (GuiChat)Minecraft.getMinecraft().currentScreen;
        GuiTextField input = guichat.inputField;
        String[] split = input.getText().split(" ");
        String moduleName = null;
        List<Module> modules = Client.moduleManager.modules;
        ArrayList<Module> completeModules = new ArrayList<Module>();
        if (split.length > 1) {
            moduleName = split[1];
        }
        String completeModuleName = null;
        if (moduleName == null || this.isEmptyAction && this.targetModule == null) {
            if (moduleName == null) {
                this.targetModule = null;
                this.isEmptyAction = true;
            } else {
                System.out.println(moduleName);
            }
            if (this.actionInt >= modules.size()) {
                this.actionInt = 0;
            }
            completeModuleName = modules.get((int)this.actionInt).name;
            ++this.actionInt;
        } else {
            for (Module module : modules) {
                if (module.name.equalsIgnoreCase(moduleName)) {
                    this.targetModule = module;
                    break;
                }
                if (!module.name.toLowerCase().startsWith(moduleName.toLowerCase())) continue;
                completeModules.add(module);
            }
            if (!completeModules.isEmpty()) {
                if (this.actionInt >= completeModules.size()) {
                    this.actionInt = 0;
                }
                this.targetModule = (Module)completeModules.get(this.actionInt);
                completeModuleName = this.targetModule.name;
                ++this.actionInt;
            }
        }
        if (completeModuleName != null) {
            if (split.length > 1) {
                split[1] = completeModuleName;
            }
            input.setText(split[0] + " " + completeModuleName);
            return;
        }
        if (this.targetModule == null) {
            return;
        }
        String fieldName = null;
        if (split.length > 2) {
            fieldName = split[2];
        }
        String completeFieldName = null;
        Field[] fields = this.targetModule.getClass().getDeclaredFields();
        if (fieldName == null || this.isEmptyAction) {
            if (fieldName == null) {
                this.isEmptyAction = true;
            }
            if (fields.length > 0) {
                Field get;
                if (this.actionInt >= fields.length) {
                    this.actionInt = 0;
                }
                if (!(get = fields[this.actionInt]).isAccessible()) {
                    get.setAccessible(true);
                }
                completeFieldName = get.getName();
                ++this.actionInt;
            }
        } else {
            ArrayList<Field> fieldList = new ArrayList<Field>();
            if (fields.length > 0) {
                if (this.actionInt >= fields.length) {
                    this.actionInt = 0;
                }
                for (Field get : fields) {
                    if (!get.isAccessible()) {
                        get.setAccessible(true);
                    }
                    if (!get.getName().toLowerCase().startsWith(fieldName.toLowerCase())) continue;
                    fieldList.add(get);
                }
                if (!fieldList.isEmpty()) {
                    if (this.actionInt >= fieldList.size()) {
                        this.actionInt = 0;
                    }
                    completeFieldName = ((Field)fieldList.get(this.actionInt)).getName();
                    ++this.actionInt;
                }
            }
        }
        if (completeFieldName != null) {
            input.setText(split[0] + " " + split[1] + " " + completeFieldName);
        }
    }
}

