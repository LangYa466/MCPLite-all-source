/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.Client;
import client.utils.TipSoundPlayer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import org.apache.commons.io.IOUtils;

public class TipSoundManager {
    public TipSoundPlayer glassSound;
    public TipSoundPlayer skeetSound;

    public TipSoundManager() {
        File glassFile = new File(Client.configManager.configDir, "glass.wav");
        File disableSoundFile = new File(Client.configManager.configDir, "skeet.wav");
        try {
            if (!glassFile.exists()) {
                TipSoundManager.unpackFile(glassFile, "assets/glass.wav");
            }
            if (!disableSoundFile.exists()) {
                TipSoundManager.unpackFile(disableSoundFile, "assets/skeet.wav");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.glassSound = new TipSoundPlayer(glassFile);
        this.skeetSound = new TipSoundPlayer(disableSoundFile);
    }

    public static void unpackFile(File file, String name) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        IOUtils.copy(Objects.requireNonNull(TipSoundPlayer.class.getClassLoader().getResourceAsStream(name)), (OutputStream)fos);
        fos.close();
    }
}

