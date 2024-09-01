/*
 * Decompiled with CFR 0.151.
 */
package client;

import client.command.CommandManager;
import client.event.EventManager;
import client.file.ConfigManager;
import client.module.ModuleManager;
import client.utils.TipSoundManager;

public class Client {
    public static final String clientName = "MCP LITE";
    public static final String version = "2.0";
    public static EventManager eventManager;
    public static CommandManager commandManager;
    public static ConfigManager configManager;
    public static ModuleManager moduleManager;
    public static TipSoundManager soundManager;

    public static void startClient() {
        eventManager = EventManager.INSTANCE;
        moduleManager = ModuleManager.INSTANCE;
        moduleManager.onModuleInited();
        commandManager = new CommandManager();
        configManager = new ConfigManager();
        soundManager = new TipSoundManager();
    }

    public static void stopClient() {
        configManager.saveConfig();
    }
}

