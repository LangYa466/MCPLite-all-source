/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.visual;

import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.RenderUtils;
import java.awt.Color;

public class ClientColor
extends Module {
    public static ClientColor INSTANCE = new ClientColor();
    @Settings(maxValue=255.0)
    public int R = 0;
    @Settings(maxValue=255.0)
    public int G = 0;
    @Settings(maxValue=255.0)
    public int B = 0;
    @Settings(maxValue=255.0)
    public int mixR1 = 0;
    @Settings(maxValue=255.0)
    public int mixG1 = 0;
    @Settings(maxValue=255.0)
    public int mixB1 = 0;
    @Settings(maxValue=255.0)
    public int mixR2 = 0;
    @Settings(maxValue=255.0)
    public int mixG2 = 0;
    @Settings(maxValue=255.0)
    public int mixB2 = 0;
    @Settings(list={"Custom", "White", "blue", "purple", "green", "yellow", "SkyPurple", "red", "pink", "Skyblue", "VDNight", "Myau", "Rise", "orange", "Tenacity", "lightgreen"})
    public String mode = "Custom";

    protected ClientColor() {
        super("Color", 0, false, ModuleType.VISUAL);
    }

    public Color getMixColor() {
        switch (this.mode) {
            case "White": {
                this.mixR1 = 255;
                this.mixG1 = 255;
                this.mixB1 = 255;
                this.mixR2 = 255;
                this.mixG2 = 255;
                this.mixB2 = 255;
                break;
            }
            case "blue": {
                this.mixR1 = 90;
                this.mixG1 = 137;
                this.mixB1 = 228;
                this.mixR2 = 255;
                this.mixG2 = 255;
                this.mixB2 = 255;
                break;
            }
            case "purple": {
                this.mixR1 = 95;
                this.mixG1 = 70;
                this.mixB1 = 227;
                this.mixR2 = 23;
                this.mixG2 = 12;
                this.mixB2 = 60;
                break;
            }
            case "green": {
                this.mixR1 = 31;
                this.mixG1 = 227;
                this.mixB1 = 11;
                this.mixR2 = 5;
                this.mixG2 = 35;
                this.mixB2 = 3;
                break;
            }
            case "yellow": {
                this.mixR1 = 241;
                this.mixG1 = 231;
                this.mixB1 = 41;
                this.mixR2 = 15;
                this.mixG2 = 62;
                this.mixB2 = 193;
                break;
            }
            case "SkyPurple": {
                this.mixR1 = 54;
                this.mixG1 = 120;
                this.mixB1 = 255;
                this.mixR2 = 95;
                this.mixG2 = 61;
                this.mixB2 = 227;
                break;
            }
            case "red": {
                this.mixR1 = 228;
                this.mixG1 = 4;
                this.mixB1 = 4;
                this.mixR2 = 50;
                this.mixG2 = 3;
                this.mixB2 = 3;
                break;
            }
            case "pink": {
                this.mixR1 = 193;
                this.mixG1 = 7;
                this.mixB1 = 189;
                this.mixR2 = 31;
                this.mixG2 = 227;
                this.mixB2 = 11;
                break;
            }
            case "Skyblue": {
                this.mixR1 = 67;
                this.mixG1 = 122;
                this.mixB1 = 255;
                this.mixR2 = 224;
                this.mixG2 = 171;
                this.mixB2 = 255;
                break;
            }
            case "VDNight": {
                this.mixR1 = 79;
                this.mixG1 = 22;
                this.mixB1 = 194;
                this.mixR2 = 177;
                this.mixG2 = 60;
                this.mixB2 = 255;
                break;
            }
            case "Myau": {
                this.mixR1 = 81;
                this.mixG1 = 0;
                this.mixB1 = 255;
                this.mixR2 = 255;
                this.mixG2 = 255;
                this.mixB2 = 255;
                break;
            }
            case "Rise": {
                this.mixR1 = 0;
                this.mixG1 = 102;
                this.mixB1 = 208;
                this.mixR2 = 81;
                this.mixG2 = 194;
                this.mixB2 = 83;
                break;
            }
            case "orange": {
                this.mixR1 = 255;
                this.mixG1 = 106;
                this.mixB1 = 0;
                this.mixR2 = 80;
                this.mixG2 = 151;
                this.mixB2 = 255;
                break;
            }
            case "Tenacity": {
                this.mixR1 = 182;
                this.mixG1 = 0;
                this.mixB1 = 201;
                this.mixR2 = 255;
                this.mixG2 = 222;
                this.mixB2 = 80;
                break;
            }
            case "lightgreen": {
                this.mixR1 = 169;
                this.mixG1 = 243;
                this.mixB1 = 196;
                this.mixR2 = 58;
                this.mixG2 = 82;
                this.mixB2 = 67;
            }
        }
        if (ClientColor.mc.thePlayer != null) {
            return RenderUtils.getGradientOffset(new Color(ClientColor.INSTANCE.mixR1, ClientColor.INSTANCE.mixG1, ClientColor.INSTANCE.mixB1, 255), new Color(ClientColor.INSTANCE.mixR2, ClientColor.INSTANCE.mixG2, ClientColor.INSTANCE.mixB2, 255), ((double)(ClientColor.mc.thePlayer.ticksExisted * 4) + 2.0) % 400.0 / 100.0, 255);
        }
        return RenderUtils.getGradientOffset(new Color(ClientColor.INSTANCE.mixR1, ClientColor.INSTANCE.mixG1, ClientColor.INSTANCE.mixB1, 255), new Color(ClientColor.INSTANCE.mixR2, ClientColor.INSTANCE.mixG2, ClientColor.INSTANCE.mixB2, 255), ((double)System.currentTimeMillis() / 20.0 + 2.0) % 400.0 / 100.0, 255);
    }

    public Color getColor() {
        return new Color(this.R, this.G, this.B);
    }

    @Override
    public String getTag() {
        return this.mode;
    }
}

