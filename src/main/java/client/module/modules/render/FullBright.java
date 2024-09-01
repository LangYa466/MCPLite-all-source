/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.render;

import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class FullBright
extends Module {
    @Settings(list={"Gamma", "NightVision"})
    private String mode = "Gamma";
    private float prevGamma = -1.0f;

    public FullBright() {
        super("FullBright", 0, false, ModuleType.RENDER);
    }

    @Override
    public void onEnable() {
        this.prevGamma = FullBright.mc.gameSettings.gammaSetting;
    }

    @Override
    public void onDisable() {
        if (this.prevGamma == -1.0f) {
            return;
        }
        FullBright.mc.gameSettings.gammaSetting = this.prevGamma;
        this.prevGamma = -1.0f;
        if (FullBright.mc.thePlayer != null) {
            FullBright.mc.thePlayer.removePotionEffectClient(Potion.nightVision.id);
        }
    }

    @Override
    public void onUpdate() {
        if (this.mode.equalsIgnoreCase("Gamma") && FullBright.mc.gameSettings.gammaSetting <= 100.0f) {
            FullBright.mc.gameSettings.gammaSetting += 1.0f;
        }
        if (this.mode.equalsIgnoreCase("NightVision") && FullBright.mc.thePlayer != null) {
            FullBright.mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 1145, 14));
        }
    }

    @Override
    public String getTag() {
        return this.mode;
    }
}

