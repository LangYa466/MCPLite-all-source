/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.render;

import client.Client;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.ClientUtils;
import net.minecraft.item.ItemSword;

public class Animation
extends Module {
    public static Animation INSTANCE;
    @Settings(list={"Vanilla", "Exh", "1.7", "Flux", "Shield", "Jello", "SigmaOld", "1_8", "Hide", "Swing", "Old", "Push", "Dash", "Slash", "Slide", "Scale", "Swank", "Swang", "Swonk", "Stella", "Small", "Edit", "Rhys", "Stab", "Float", "Remix", "Avatar", "Xiv", "Winter", "Yamato", "SlideSwing", "SmallPush", "Reverse", "Invent", "Leaked", "Aqua", "Astro", "Fadeaway", "Astolfo", "AstolfoSpin", "Moon", "MoonPush", "Smooth", "Jigsaw", "Tap1", "Tap2", "Sigma3", "Sigma4"}, name="BlockAnimation")
    public String mode = "Vanilla";
    @Settings(list={"1_7", "1_8", "Flux", "Smooth", "Dash"})
    public String hideMode = "1_8";
    @Settings(maxValue=10.0)
    public float scale = 0.4f;
    @Settings(minValue=0.0, maxValue=15.0)
    public float swingSpeed = 0.0f;
    @Settings
    public boolean cancelEquip = false;
    @Settings
    public boolean cancelEquipOnlySword = false;
    @Settings(minValue=-1.0, maxValue=1.0)
    public double x = 0.0;
    @Settings(minValue=-1.0, maxValue=1.0)
    public double y = 0.0;
    @Settings(minValue=-1.0, maxValue=1.0)
    public double z = 0.0;
    @Settings(minValue=-1.0, maxValue=1.0)
    public double blockX = 0.0;
    @Settings(minValue=-1.0, maxValue=1.0)
    public double blockY = 0.0;
    @Settings(minValue=-1.0, maxValue=1.0)
    public double blockZ = 0.0;
    @Settings(minValue=-1.0, maxValue=1.0)
    public double eatX = 0.0;
    @Settings(minValue=-1.0, maxValue=1.0)
    public double eatY = 0.0;
    @Settings(minValue=-1.0, maxValue=1.0)
    public double eatZ = 0.0;

    public Animation() {
        super("Animation", 0, true, ModuleType.RENDER);
    }

    @Override
    public void onModulesInited() {
        INSTANCE = (Animation)Client.moduleManager.moduleMap.get(this.getClass());
    }

    @Override
    public void onTick() {
        if (!ClientUtils.nullCheck() && this.cancelEquipOnlySword) {
            this.cancelEquip = Animation.mc.thePlayer.getHeldItem() != null && Animation.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
        }
    }

    @Override
    public String getTag() {
        return this.mode;
    }
}

