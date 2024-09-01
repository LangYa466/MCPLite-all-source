/*
 * Decompiled with CFR 0.151.
 */
package client.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;

public abstract class Config {
    private final File file;
    public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    public Config(File file) {
        this.file = file;
    }

    protected abstract void loadConfig() throws IOException;

    protected abstract void saveConfig() throws IOException;

    public void createConfig() throws IOException {
        this.file.createNewFile();
    }

    public boolean hasConfig() {
        return this.file.exists();
    }

    public File getFile() {
        return this.file;
    }
}

