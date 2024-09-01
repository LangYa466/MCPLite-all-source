/*
 * Decompiled with CFR 0.151.
 */
package client.file.configs;

import client.file.Config;
import client.ui.element.Element;
import client.ui.element.ElementManager;
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
import java.util.Map;

public class ElementsConfig
extends Config {
    public ElementsConfig(File file) {
        super(file);
    }

    @Override
    protected void loadConfig() throws IOException {
        JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(this.getFile())));
        if (jsonElement instanceof JsonNull) {
            return;
        }
        for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
            Element element = ElementManager.get(entry.getKey());
            if (element == null) continue;
            JsonObject jsonObject = (JsonObject)entry.getValue();
            element.moveX = jsonObject.get("X").getAsFloat();
            element.moveY = jsonObject.get("Y").getAsFloat();
        }
    }

    @Override
    protected void saveConfig() throws IOException {
        if (!this.hasConfig()) {
            this.createConfig();
        }
        JsonObject jsonObject = new JsonObject();
        for (Element element : ElementManager.INSTANCE.elements) {
            JsonObject jsonMod = new JsonObject();
            jsonMod.addProperty("X", Float.valueOf(element.moveX));
            jsonMod.addProperty("Y", Float.valueOf(element.moveY));
            jsonObject.add(element.name, jsonMod);
        }
        PrintWriter printWriter = new PrintWriter(new FileWriter(this.getFile()));
        printWriter.println(Config.PRETTY_GSON.toJson(jsonObject));
        printWriter.close();
    }
}

