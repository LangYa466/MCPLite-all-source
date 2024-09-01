/*
 * Decompiled with CFR 0.151.
 */
package client.file.configs;

import client.Client;
import client.file.Config;
import client.module.Module;
import client.module.ModuleManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Map;

public class ModuleConfig
extends Config {
    public ModuleConfig(File file) {
        super(file);
    }

    @Override
    protected void loadConfig() throws IOException {
        JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(this.getFile())));
        if (jsonElement instanceof JsonNull) {
            return;
        }
        for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
            Module module = ModuleManager.getModuleByString(entry.getKey());
            if (module == null) continue;
            JsonObject jsonModule = (JsonObject)entry.getValue();
            module.setState(jsonModule.get("State").getAsBoolean());
            try {
                for (Field bool : module.getBooleans()) {
                    bool.setAccessible(true);
                    bool.set(module, jsonModule.get(bool.getName()).getAsBoolean());
                }
                for (Field ints : module.getInts()) {
                    ints.setAccessible(true);
                    ints.set(module, jsonModule.get(ints.getName()).getAsInt());
                }
                for (Field floats : module.getFloats()) {
                    floats.setAccessible(true);
                    floats.set(module, Float.valueOf(jsonModule.get(floats.getName()).getAsFloat()));
                }
                for (Field doubles : module.getDoubles()) {
                    doubles.setAccessible(true);
                    doubles.set(module, jsonModule.get(doubles.getName()).getAsDouble());
                }
                for (Field strings : module.getStrings()) {
                    strings.setAccessible(true);
                    strings.set(module, jsonModule.get(strings.getName()).getAsString());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveConfig() throws IOException {
        if (!this.hasConfig()) {
            this.createConfig();
        }
        JsonObject jsonObject = new JsonObject();
        for (Module module : Client.moduleManager.modules) {
            JsonObject jsonMod = new JsonObject();
            jsonMod.addProperty("State", module.getState());
            try {
                for (Field bool : module.getBooleans()) {
                    bool.setAccessible(true);
                    jsonMod.addProperty(bool.getName(), bool.getBoolean(module));
                }
                for (Field ints : module.getInts()) {
                    ints.setAccessible(true);
                    jsonMod.addProperty(ints.getName(), ints.getInt(module));
                }
                for (Field floats : module.getFloats()) {
                    floats.setAccessible(true);
                    jsonMod.addProperty(floats.getName(), Float.valueOf(floats.getFloat(module)));
                }
                for (Field doubles : module.getDoubles()) {
                    doubles.setAccessible(true);
                    jsonMod.addProperty(doubles.getName(), doubles.getDouble(module));
                }
                for (Field strings : module.getStrings()) {
                    strings.setAccessible(true);
                    jsonMod.addProperty(strings.getName(), (String)strings.get(module));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            jsonObject.add(module.name, jsonMod);
        }
        PrintWriter printWriter = new PrintWriter(new FileWriter(this.getFile()));
        printWriter.println(Config.PRETTY_GSON.toJson(jsonObject));
        printWriter.close();
    }
}

