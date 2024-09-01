/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.visual;

import client.event.events.Render2DEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.visual.ClientColor;
import client.ui.element.Element;
import client.ui.element.ElementManager;
import client.ui.font.CFontRenderer;
import client.ui.font.FontLoaders;
import client.utils.RenderUtils;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.client.Minecraft;

public class Logo
extends Module {
    @Settings(list={"Simple", "Simple2", "SCP"})
    private String mode = "Simple";
    private int anInt = 0;

    public Logo() {
        super("Logo", 0, false, ModuleType.VISUAL);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        Element element = ElementManager.logo;
        switch (this.mode) {
            case "Simple": {
                element.posX = element.moveX;
                element.posY = element.moveY;
                element.height = Logo.mc.fontRendererObj.FONT_HEIGHT;
                element.width = Logo.mc.fontRendererObj.getStringWidth("MCPClient [FPS: " + Minecraft.getDebugFPS() + "] [DEV: java.lang.NullPointerException]");
                Logo.mc.fontRendererObj.drawStringWithShadow("MCPClient [FPS: " + Minecraft.getDebugFPS() + "] [DEV: ", element.posX, element.posY, Color.white.getRGB());
                RenderUtils.drowColorString(null, "java.lang.NullPointerException", (int)(element.posX + (float)Logo.mc.fontRendererObj.getStringWidth("MCPClient [FPS: " + Minecraft.getDebugFPS() + "] [DEV: ")), (int)element.posY);
                Logo.mc.fontRendererObj.drawStringWithShadow("]", element.posX + (float)Logo.mc.fontRendererObj.getStringWidth("MCPClient [FPS: " + Minecraft.getDebugFPS() + "] [DEV: java.lang.NullPointerException"), element.posY, Color.white.getRGB());
                break;
            }
            case "Simple2": {
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                CFontRenderer fontRenderer = FontLoaders.SF24;
                element.posX = element.moveX;
                element.posY = element.moveY;
                element.height = fontRenderer.getHeight();
                element.width = 10.0f;
                Color color = new Color(255, 255, 255, 255);
                String text = "M";
                String text2 = "cplite " + dateFormat.format(date);
                fontRenderer.drawString(text, element.posX, element.posY, ClientColor.INSTANCE.getMixColor().getRGB());
                fontRenderer.drawString(text2, element.posX + (float)fontRenderer.getStringWidth(text), element.posY, color.getRGB());
                break;
            }
            case "SCP": {
                element.posX = 0.0f + element.moveX;
                element.posY = 0.0f + element.moveY;
                element.height = 30.0f;
                element.width = 30.0f;
                RenderUtils.drawSCPAnimationB(30.0f + element.moveX, 30.0f + element.moveY, 1.0f, this.anInt / 2);
            }
        }
        ++this.anInt;
        if (this.anInt > 720) {
            this.anInt = 0;
        }
    }

    @Override
    public String getTag() {
        return this.mode;
    }
}

