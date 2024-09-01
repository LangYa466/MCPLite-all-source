/*
 * Decompiled with CFR 0.151.
 */
package client.file;

import client.file.Config;
import client.file.configs.ElementsConfig;
import client.file.configs.ModuleConfig;
import client.utils.MinecraftInstance;
import java.io.File;
import java.io.IOException;

public class ConfigManager
extends MinecraftInstance {
    public final File configDir;
    public final Config moduleConfig;
    public final Config elementsConfig;

    public void load() throws IOException {
        if (!this.configDir.exists()) {
            this.configDir.mkdir();
        }
        if (!this.moduleConfig.hasConfig()) {
            this.moduleConfig.saveConfig();
        } else {
            this.moduleConfig.loadConfig();
        }
        if (!this.elementsConfig.hasConfig()) {
            this.elementsConfig.saveConfig();
        } else {
            this.elementsConfig.loadConfig();
        }
    }

    public ConfigManager() {
        this.configDir = new File(ConfigManager.mc.mcDataDir, "Client");
        this.moduleConfig = new ModuleConfig(new File(this.configDir, "Module.json"));
        this.elementsConfig = new ElementsConfig(new File(this.configDir, "Elements.json"));
        try {
            this.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            System.out.println("Save");
            this.moduleConfig.saveConfig();
            this.elementsConfig.saveConfig();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

