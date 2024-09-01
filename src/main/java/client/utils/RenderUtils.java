/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.module.modules.visual.ClientColor;
import client.ui.fastuni.FastUniFontRenderer;
import client.ui.fastuni.FontLoader;
import client.ui.font.CFontRenderer;
import client.ui.font.FontLoaders;
import client.ui.notifi.Notifi;
import client.utils.BlockUtils;
import client.utils.GLUtil;
import client.utils.MinecraftInstance;
import client.utils.anim.Animation;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class RenderUtils
extends MinecraftInstance {
    private static final Map<Integer, Boolean> glCapMap = new HashMap<Integer, Boolean>();

    public static void resetCaps() {
        glCapMap.forEach(RenderUtils::setGlState);
    }

    public static void setGlState(int cap, boolean state) {
        if (state) {
            GL11.glEnable(cap);
        } else {
            GL11.glDisable(cap);
        }
    }

    public static void drawPlayerHead(ResourceLocation skin, int x2, int y2, int width, int height) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(x2, y2, 8.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
    }

    public static void enableGlCap(int cap) {
        RenderUtils.setGlCap(cap, true);
    }

    public static void enableGlCap(int ... caps) {
        for (int cap : caps) {
            RenderUtils.setGlCap(cap, true);
        }
    }

    public static void disableGlCap(int cap) {
        RenderUtils.setGlCap(cap, true);
    }

    public static void disableGlCap(int ... caps) {
        for (int cap : caps) {
            RenderUtils.setGlCap(cap, false);
        }
    }

    public static void setGlCap(int cap, boolean state) {
        glCapMap.put(cap, GL11.glGetBoolean(cap));
        RenderUtils.setGlState(cap, state);
    }

    public static void scissorStart(double x, double y, double width, double height) {
        GL11.glEnable(3089);
        ScaledResolution sr = new ScaledResolution(mc);
        double scale = sr.getScaleFactor();
        double finalHeight = height * scale;
        double finalY = ((double)sr.getScaledHeight() - y) * scale;
        double finalX = x * scale;
        double finalWidth = width * scale;
        GL11.glScissor((int)finalX, (int)(finalY - finalHeight), (int)finalWidth, (int)finalHeight);
    }

    public static void color(int color, float alpha) {
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        GlStateManager.color(r, g, b, alpha);
    }

    public static void drawClickGuiArrow(float x, float y, float size, Animation animation, int color) {
        GL11.glTranslatef(x, y, 0.0f);
        RenderUtils.color(color);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableTexture2D();
        GL11.glBegin(5);
        double interpolation = MathHelper.interpolate(0.0, (double)size / 2.0, animation.getOutput().floatValue());
        if ((double)animation.getOutput().floatValue() >= 0.48) {
            GL11.glVertex2d(size / 2.0f, MathHelper.interpolate((double)size / 2.0, 0.0, animation.getOutput().floatValue()));
        }
        GL11.glVertex2d(0.0, interpolation);
        if ((double)animation.getOutput().floatValue() < 0.48) {
            GL11.glVertex2d(size / 2.0f, MathHelper.interpolate((double)size / 2.0, 0.0, animation.getOutput().floatValue()));
        }
        GL11.glVertex2d(size, interpolation);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glTranslatef(-x, -y, 0.0f);
    }

    public static double animate(double endPoint, double current, double speed) {
        boolean shouldContinueAnimation;
        boolean bl = shouldContinueAnimation = endPoint > current;
        if (speed < 0.0) {
            speed = 0.0;
        } else if (speed > 1.0) {
            speed = 1.0;
        }
        double dif = Math.max(endPoint, current) - Math.min(endPoint, current);
        double factor = dif * speed;
        return current + (shouldContinueAnimation ? factor : -factor);
    }

    public static void renderRoundedRect(float x, float y, float width, float height, float radius, int color) {
        RenderUtils.drawGoodCircle(x + radius, y + radius, radius, color);
        RenderUtils.drawGoodCircle(x + width - radius, y + radius, radius, color);
        RenderUtils.drawGoodCircle(x + radius, y + height - radius, radius, color);
        RenderUtils.drawGoodCircle(x + width - radius, y + height - radius, radius, color);
        RenderUtils.drawRect2(x + radius, y, width - radius * 2.0f, height, color);
        RenderUtils.drawRect2(x, y + radius, width, height - radius * 2.0f, color);
    }

    public static void color(int color) {
        RenderUtils.color(color, (float)(color >> 24 & 0xFF) / 255.0f);
    }

    public static void color(double red, double green, double blue, double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static void color(double red, double green, double blue) {
        RenderUtils.color(red, green, blue, 1.0);
    }

    public static void color(Color color) {
        if (color == null) {
            color = Color.white;
        }
        RenderUtils.color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
    }

    public static void color(Color color, int alpha) {
        if (color == null) {
            color = Color.white;
        }
        RenderUtils.color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, 0.5);
    }

    public static void scissorEnd() {
        GL11.glDisable(3089);
    }

    public static void drawBorderedRect(float x, float y, float width, float height, float outlineThickness, int rectColor, int outlineColor) {
        RenderUtils.drawRect2(x, y, width, height, rectColor);
        GL11.glEnable(2848);
        RenderUtils.color(outlineColor);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableTexture2D();
        GL11.glLineWidth(outlineThickness);
        float cornerValue = (float)((double)outlineThickness * 0.19);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y - cornerValue);
        GL11.glVertex2d(x, y + height + cornerValue);
        GL11.glVertex2d(x + width, y + height + cornerValue);
        GL11.glVertex2d(x + width, y - cornerValue);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x, y + height);
        GL11.glVertex2d(x + width, y + height);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glDisable(2848);
    }

    public static void drawGoodCircle(double x, double y, float radius, int color) {
        RenderUtils.color(color);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableTexture2D();
        GL11.glEnable(2832);
        GL11.glHint(3153, 4354);
        GL11.glPointSize(radius * (float)(2 * RenderUtils.mc.gameSettings.guiScale));
        GL11.glBegin(0);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void enableRender3D(boolean disableDepth) {
        if (disableDepth) {
            GL11.glDepthMask(false);
            GL11.glDisable(2929);
        }
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0f);
    }

    public static void drawRect(float x, float y, float x2, float y2, Color color) {
        RenderUtils.drawRect(x, y, x2, y2, color.getRGB());
    }

    public static void drawRect(float x, float y, float x2, float y2, int color) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        RenderUtils.glColor(color);
        GL11.glBegin(7);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static void glColor(int red, int green, int blue, int alpha) {
        GlStateManager.color((float)red / 255.0f, (float)green / 255.0f, (float)blue / 255.0f, (float)alpha / 255.0f);
    }

    public static void glColor(Color color) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        GlStateManager.color(red, green, blue, alpha);
    }

    private static void glColor(int hex) {
        float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        GlStateManager.color(red, green, blue, alpha);
    }

    public static void drowColorString(CFontRenderer cFontRenderer, String text, int x, int y, int alpha) {
        StringBuilder hasDrew = new StringBuilder();
        for (int i = 0; i < text.length(); ++i) {
            Color color1 = RenderUtils.getGradientOffset(new Color(ClientColor.INSTANCE.mixR1, ClientColor.INSTANCE.mixG1, ClientColor.INSTANCE.mixB1, 255), new Color(ClientColor.INSTANCE.mixR2, ClientColor.INSTANCE.mixG2, ClientColor.INSTANCE.mixB2, 255), ((double)(RenderUtils.mc.thePlayer.ticksExisted * 5) + (double)i * 5.0) % 400.0 / 100.0, 255);
            Color color = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), alpha);
            if (cFontRenderer == null) {
                RenderUtils.mc.fontRendererObj.drawStringWithShadow(String.valueOf(text.charAt(i)), x + RenderUtils.mc.fontRendererObj.getStringWidth(hasDrew.toString()), y, color.getRGB());
            } else {
                cFontRenderer.drawString(String.valueOf(text.charAt(i)), x + cFontRenderer.getStringWidth(hasDrew.toString()), y, color.getRGB());
            }
            hasDrew.append(text.charAt(i));
        }
    }

    public static void drowColorStringUni(FastUniFontRenderer cFontRenderer, String text, int x, int y, int alpha) {
        StringBuilder hasDrew = new StringBuilder();
        for (int i = 0; i < text.length(); ++i) {
            Color color1 = RenderUtils.getGradientOffset(new Color(ClientColor.INSTANCE.mixR1, ClientColor.INSTANCE.mixG1, ClientColor.INSTANCE.mixB1, 255), new Color(ClientColor.INSTANCE.mixR2, ClientColor.INSTANCE.mixG2, ClientColor.INSTANCE.mixB2, 255), ((double)(RenderUtils.mc.thePlayer.ticksExisted * 5) + (double)i * 5.0) % 400.0 / 100.0, 255);
            Color color = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), alpha);
            if (cFontRenderer == null) {
                RenderUtils.mc.fontRendererObj.drawStringWithShadow(String.valueOf(text.charAt(i)), x + RenderUtils.mc.fontRendererObj.getStringWidth(hasDrew.toString()), y, color.getRGB());
            } else {
                cFontRenderer.drawString(String.valueOf(text.charAt(i)), (float)(x + cFontRenderer.getStringWidth(hasDrew.toString())), y, color.getRGB());
            }
            hasDrew.append(text.charAt(i));
        }
    }

    public static void drowColorString(CFontRenderer cFontRenderer, String text, int x, int y) {
        RenderUtils.drowColorString(cFontRenderer, text, x, y, 255);
    }

    public static Color getGradientOffset(Color color1, Color color2, double offset, int alpha) {
        int redPart;
        double inverse_percent;
        if (offset > 1.0) {
            inverse_percent = offset % 1.0;
            redPart = (int)offset;
            offset = redPart % 2 == 0 ? inverse_percent : 1.0 - inverse_percent;
        }
        inverse_percent = 1.0 - offset;
        redPart = (int)((double)color1.getRed() * inverse_percent + (double)color2.getRed() * offset);
        int greenPart = (int)((double)color1.getGreen() * inverse_percent + (double)color2.getGreen() * offset);
        int bluePart = (int)((double)color1.getBlue() * inverse_percent + (double)color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart, alpha);
    }

    public static void drawText(String name, float x, float y) {
        RenderUtils.drawText(null, name, x, y, new Color(19, 19, 19, 100), ClientColor.INSTANCE.getMixColor());
    }

    public static void drawText(CFontRenderer cFontRenderer, String name, float x, float y, Color backGroundColor, Color textColor) {
        RenderUtils.drawText(cFontRenderer, name, x, y, backGroundColor, textColor, false);
    }

    public static void drawText(CFontRenderer cFontRenderer, String name, float x, float y, Color backGroundColor, Color textColor, boolean photoShadow) {
        RenderUtils.drawText(cFontRenderer, name, x, y, 0.0f, 0.0f, backGroundColor, textColor, photoShadow);
    }

    public static void drawText(CFontRenderer cFontRenderer, String name, float x, float y, float wordWidth, float wordHeight, Color backGroundColor, Color textColor, boolean photoShadow) {
        RenderUtils.drawText(cFontRenderer, name, x, y, wordWidth, wordHeight, backGroundColor, textColor, photoShadow, false);
    }

    public static void drawText(CFontRenderer cFontRenderer, String name, float x, float y, float wordWidth, float wordHeight, Color backGroundColor, Color textColor, boolean photoShadow, boolean rounded) {
        int width = cFontRenderer != null ? cFontRenderer.getStringWidth(name) : RenderUtils.mc.fontRendererObj.getStringWidth(name);
        int height = cFontRenderer != null ? cFontRenderer.getHeight() : RenderUtils.mc.fontRendererObj.FONT_HEIGHT;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        if (cFontRenderer == null) {
            if (photoShadow) {
                RenderUtils.drawShadow(x - wordWidth / 2.0f, y - wordHeight / 2.0f, (float)width + wordWidth, (float)height + wordHeight);
            }
            if (rounded) {
                RenderUtils.drawRoundedRect(x - wordWidth / 2.0f, y - wordHeight / 2.0f, (float)width + wordWidth, (float)height + wordHeight, 1.0f, backGroundColor.getRGB(), 1.0f, backGroundColor.getRGB());
            } else {
                RenderUtils.drawRDRect(x - wordWidth / 2.0f, y - wordHeight / 2.0f, (float)width + wordWidth, (float)height + wordHeight, backGroundColor.getRGB());
            }
            RenderUtils.mc.fontRendererObj.drawString(name, (int)x, (int)y + 1, textColor.getRGB());
        } else {
            if (photoShadow) {
                RenderUtils.drawShadow(x - wordWidth / 2.0f, y - wordHeight / 2.0f, (float)width + wordWidth, (float)height + wordHeight);
            }
            if (rounded) {
                RenderUtils.drawRoundedRect(x - wordWidth / 2.0f, y - wordHeight / 2.0f, (float)width + wordWidth, (float)height + wordHeight, 1.0f, backGroundColor.getRGB(), 1.0f, backGroundColor.getRGB());
            } else {
                RenderUtils.drawRDRect(x - wordWidth / 2.0f, y - wordHeight / 2.0f, (float)width + wordWidth, (float)height + wordHeight, backGroundColor.getRGB());
            }
            cFontRenderer.drawString(name, x, y, textColor.getRGB());
        }
        GL11.glPopMatrix();
    }

    public static void drawModule(CFontRenderer cFontRenderer, String name, String tag, float x, float y, float wordWidth, float wordHeight, Color backGroundColor, Color textColor, boolean photoShadow, boolean rounded) {
        tag = tag != null && !tag.isEmpty() ? " " + tag : "";
        int width = cFontRenderer != null ? cFontRenderer.getStringWidth(name + tag) : RenderUtils.mc.fontRendererObj.getStringWidth(name + tag);
        int height = cFontRenderer != null ? cFontRenderer.getHeight() : RenderUtils.mc.fontRendererObj.FONT_HEIGHT;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        if (cFontRenderer == null) {
            if (photoShadow) {
                RenderUtils.drawShadow(x - wordWidth / 2.0f, y - wordHeight / 2.0f, (float)width + wordWidth, (float)height + wordHeight);
            }
            if (rounded) {
                RenderUtils.drawRoundedRect(x - wordWidth / 2.0f, y - wordHeight / 2.0f, (float)width + wordWidth, (float)height + wordHeight, 1.0f, backGroundColor.getRGB(), 1.0f, backGroundColor.getRGB());
            } else {
                RenderUtils.drawRDRect(x - wordWidth / 2.0f, y - wordHeight / 2.0f, (float)width + wordWidth, (float)height + wordHeight, backGroundColor.getRGB());
            }
            RenderUtils.mc.fontRendererObj.drawString(name, (int)x, (int)y + 1, textColor.getRGB());
            RenderUtils.mc.fontRendererObj.drawString(tag, (int)((float)RenderUtils.mc.fontRendererObj.getStringWidth(name) + x), (int)y, new Color(150, 150, 150, 255).getRGB());
        } else {
            if (photoShadow) {
                RenderUtils.drawShadow(x - wordWidth / 2.0f, y - wordHeight / 2.0f, (float)width + wordWidth, (float)height + wordHeight);
            }
            if (rounded) {
                RenderUtils.drawRoundedRect(x - wordWidth / 2.0f, y - wordHeight / 2.0f, (float)width + wordWidth, (float)height + wordHeight, 1.0f, backGroundColor.getRGB(), 1.0f, backGroundColor.getRGB());
            } else {
                RenderUtils.drawRDRect(x - wordWidth / 2.0f, y - wordHeight / 2.0f, (float)width + wordWidth, (float)height + wordHeight, backGroundColor.getRGB());
            }
            cFontRenderer.drawString(name, x, y, textColor.getRGB());
            cFontRenderer.drawString(tag, (int)((float)cFontRenderer.getStringWidth(name) + x), (int)y, new Color(150, 150, 150, 255).getRGB());
        }
        GL11.glPopMatrix();
    }

    public static void drawNotifi(String name, float rollbackX, float rollbackY, Notifi notifi) {
        RenderUtils.drawNotifi(name, rollbackX, rollbackY, new Color(19, 19, 19, 50), ClientColor.INSTANCE.getMixColor(), notifi);
    }

    public static void drawNotifi(String name, float rollbackX, float rollbackY, Color backGroundColor, Color textColor, Notifi notifi) {
        ScaledResolution sr = new ScaledResolution(mc);
        float screenWidth = sr.getScaledWidth();
        float screenHeight = sr.getScaledHeight();
        CFontRenderer fontRenderer = FontLoaders.GoogleSans20;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        Color jdtColor = new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 50);
        int jdtWidth = (int)((float)(fontRenderer.getStringWidth(name) + 10) * Math.min(1.0f, (float)notifi.timer.getPassed() / 1000.0f));
        RenderUtils.drawRollBackRect(rollbackX + (float)fontRenderer.getStringWidth(name) + 5.0f, rollbackY + 10.0f, 1.0f, 24.0f, textColor.getRGB());
        RenderUtils.drawRollBackRect(rollbackX - 5.0f, rollbackY + 10.0f, fontRenderer.getStringWidth(name) + 10, 24.0f, backGroundColor.getRGB());
        RenderUtils.drawRollBackRect(rollbackX + (float)fontRenderer.getStringWidth(name) + 5.0f - (float)jdtWidth, rollbackY + 10.0f, jdtWidth, 24.0f, jdtColor.getRGB());
        RenderUtils.drowColorString(FontLoaders.GoogleSans24, name.toLowerCase().contains("enable") ? "Enable" : "Disable", (int)(screenWidth - rollbackX - (float)fontRenderer.getStringWidth(name)), (int)((float)((int)screenHeight - 20) - rollbackY - (float)FontLoaders.GoogleSans24.getHeight()));
        fontRenderer.drawString(name, (int)(screenWidth - rollbackX - (float)fontRenderer.getStringWidth(name)), (int)((float)((int)screenHeight - 20) - rollbackY), Color.white.getRGB());
        GL11.glPopMatrix();
    }

    public static void drawNotifiB(String name, float rollbackX, float rollbackY, Notifi notifi) {
        RenderUtils.drawNotifiB(name, rollbackX, rollbackY, new Color(19, 19, 19, 128), ClientColor.INSTANCE.getMixColor(), notifi);
    }

    public static void drawNotifiB(String name, float rollbackX, float rollbackY, Color backGroundColor, Color textColor, Notifi notifi) {
        ScaledResolution sr = new ScaledResolution(mc);
        float screenWidth = sr.getScaledWidth();
        float screenHeight = sr.getScaledHeight();
        CFontRenderer fontRenderer = FontLoaders.Tenacityb18;
        CFontRenderer bigFontRenderer = FontLoaders.Tenacityb24;
        FastUniFontRenderer icon = FontLoader.icon48;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        Color jdtColor = new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 255);
        int height = 27;
        float width = fontRenderer.getStringWidth(name) + 40;
        int jdtWidth = (int)(width * Math.min(1.0f, (float)notifi.timer.getPassed() / 800.0f));
        int jdtHeight = (int)((float)height * Math.min(1.0f, (float)notifi.timer.getPassed() / 800.0f));
        int x = (int)(screenWidth - (rollbackX - 10.0f));
        int y = (int)(screenHeight - (rollbackY + 10.0f));
        RenderUtils.drawRollBackRect(rollbackX - 10.0f, rollbackY + 10.0f, width, height, backGroundColor.getRGB());
        RenderUtils.drawRollBackRect(rollbackX - 10.0f + width - (float)jdtWidth, rollbackY + 10.0f, jdtWidth, 1.0f, jdtColor.getRGB());
        ResourceLocation resourceLocation = new ResourceLocation("noti/" + (name.toLowerCase().contains("enable") ? "SUCCESS" : "ERROR") + ".png");
        icon.drawString(name.toLowerCase().contains("enable") ? "A" : "B", (float)((int)((float)x - width) + 3 + (name.toLowerCase().contains("enable") ? 0 : 2)), (float)(y - height) + 11.0f + (float)(!name.toLowerCase().contains("enable") ? 1 : 0), jdtColor.getRGB());
        int fontX = (int)(screenWidth - width - rollbackX + 10.0f + 25.0f);
        int fontY = (int)((float)((int)screenHeight - 20) - rollbackY);
        fontRenderer.drawString(name, fontX, fontY - 1, Color.WHITE.getRGB());
        bigFontRenderer.drawString("Module", fontX, fontY - 10 - 4, textColor.getRGB());
        GL11.glPopMatrix();
    }

    public static void enableRender2D() {
        GL11.glEnable(3042);
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.0f);
    }

    public static void drawRDRect(double x, double y, double width, double height, int color) {
        RenderUtils.drawRect((float)x, (float)y, (float)(x + width), (float)(y + height), color);
    }

    public static void drawRollBackRect(float rollbackX, float rollbackY, float width, float height, int color) {
        ScaledResolution sr = new ScaledResolution(mc);
        float scw = sr.getScaledWidth();
        float sch = sr.getScaledHeight();
        RenderUtils.drawRect(scw - rollbackX - width, sch - rollbackY - height, scw - rollbackX, sch - rollbackY, color);
    }

    public static void drawHGradientRect(double x2, double y2, double width, double height, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(startColor & 0xFF) / 255.0f;
        float f4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(endColor & 0xFF) / 255.0f;
        GLUtil.setup2DRendering(() -> {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            GL11.glShadeModel(7425);
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(x2, y2, 0.0).color(f1, f2, f3, f).endVertex();
            worldrenderer.pos(x2, y2 + height, 0.0).color(f1, f2, f3, f).endVertex();
            worldrenderer.pos(x2 + width, y2 + height, 0.0).color(f5, f6, f7, f4).endVertex();
            worldrenderer.pos(x2 + width, y2, 0.0).color(f5, f6, f7, f4).endVertex();
            tessellator.draw();
            GlStateManager.resetColor();
            GL11.glShadeModel(7424);
        });
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float edgeRadius, int color, float borderWidth, int borderColor) {
        double angleRadians;
        int i;
        if (color == 0xFFFFFF) {
            color = -65794;
        }
        if (borderColor == 0xFFFFFF) {
            borderColor = -65794;
        }
        if (edgeRadius < 0.0f) {
            edgeRadius = 0.0f;
        }
        if (edgeRadius > width / 2.0f) {
            edgeRadius = width / 2.0f;
        }
        if (edgeRadius > height / 2.0f) {
            edgeRadius = height / 2.0f;
        }
        RenderUtils.drawRDRect(x + edgeRadius, y + edgeRadius, width - edgeRadius * 2.0f, height - edgeRadius * 2.0f, color);
        RenderUtils.drawRDRect(x + edgeRadius, y, width - edgeRadius * 2.0f, edgeRadius, color);
        RenderUtils.drawRDRect(x + edgeRadius, y + height - edgeRadius, width - edgeRadius * 2.0f, edgeRadius, color);
        RenderUtils.drawRDRect(x, y + edgeRadius, edgeRadius, height - edgeRadius * 2.0f, color);
        RenderUtils.drawRDRect(x + width - edgeRadius, y + edgeRadius, edgeRadius, height - edgeRadius * 2.0f, color);
        RenderUtils.enableRender2D();
        RenderUtils.color(color);
        GL11.glBegin(6);
        float centerX = x + edgeRadius;
        float centerY = y + edgeRadius;
        GL11.glVertex2d(centerX, centerY);
        int vertices = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f);
        for (i = 0; i < vertices + 1; ++i) {
            angleRadians = Math.PI * 2 * (double)(i + 180) / (double)(vertices * 4);
            GL11.glVertex2d((double)centerX + Math.sin(angleRadians) * (double)edgeRadius, (double)centerY + Math.cos(angleRadians) * (double)edgeRadius);
        }
        GL11.glEnd();
        GL11.glBegin(6);
        centerX = x + width - edgeRadius;
        centerY = y + edgeRadius;
        GL11.glVertex2d(centerX, centerY);
        vertices = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f);
        for (i = 0; i < vertices + 1; ++i) {
            angleRadians = Math.PI * 2 * (double)(i + 90) / (double)(vertices * 4);
            GL11.glVertex2d((double)centerX + Math.sin(angleRadians) * (double)edgeRadius, (double)centerY + Math.cos(angleRadians) * (double)edgeRadius);
        }
        GL11.glEnd();
        GL11.glBegin(6);
        centerX = x + edgeRadius;
        centerY = y + height - edgeRadius;
        GL11.glVertex2d(centerX, centerY);
        vertices = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f);
        for (i = 0; i < vertices + 1; ++i) {
            angleRadians = Math.PI * 2 * (double)(i + 270) / (double)(vertices * 4);
            GL11.glVertex2d((double)centerX + Math.sin(angleRadians) * (double)edgeRadius, (double)centerY + Math.cos(angleRadians) * (double)edgeRadius);
        }
        GL11.glEnd();
        GL11.glBegin(6);
        centerX = x + width - edgeRadius;
        centerY = y + height - edgeRadius;
        GL11.glVertex2d(centerX, centerY);
        vertices = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f);
        for (i = 0; i < vertices + 1; ++i) {
            angleRadians = Math.PI * 2 * (double)i / (double)(vertices * 4);
            GL11.glVertex2d((double)centerX + Math.sin(angleRadians) * (double)edgeRadius, (double)centerY + Math.cos(angleRadians) * (double)edgeRadius);
        }
        GL11.glEnd();
        RenderUtils.color(borderColor);
        GL11.glLineWidth(borderWidth);
        GL11.glBegin(3);
        centerX = x + edgeRadius;
        centerY = y + edgeRadius;
        vertices = i = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f);
        while (i >= 0) {
            angleRadians = Math.PI * 2 * (double)(i + 180) / (double)(vertices * 4);
            GL11.glVertex2d((double)centerX + Math.sin(angleRadians) * (double)edgeRadius, (double)centerY + Math.cos(angleRadians) * (double)edgeRadius);
            --i;
        }
        GL11.glVertex2d(x + edgeRadius, y);
        GL11.glVertex2d(x + width - edgeRadius, y);
        centerX = x + width - edgeRadius;
        centerY = y + edgeRadius;
        for (i = vertices; i >= 0; --i) {
            angleRadians = Math.PI * 2 * (double)(i + 90) / (double)(vertices * 4);
            GL11.glVertex2d((double)centerX + Math.sin(angleRadians) * (double)edgeRadius, (double)centerY + Math.cos(angleRadians) * (double)edgeRadius);
        }
        GL11.glVertex2d(x + width, y + edgeRadius);
        GL11.glVertex2d(x + width, y + height - edgeRadius);
        centerX = x + width - edgeRadius;
        centerY = y + height - edgeRadius;
        for (i = vertices; i >= 0; --i) {
            angleRadians = Math.PI * 2 * (double)i / (double)(vertices * 4);
            GL11.glVertex2d((double)centerX + Math.sin(angleRadians) * (double)edgeRadius, (double)centerY + Math.cos(angleRadians) * (double)edgeRadius);
        }
        GL11.glVertex2d(x + width - edgeRadius, y + height);
        GL11.glVertex2d(x + edgeRadius, y + height);
        centerX = x + edgeRadius;
        centerY = y + height - edgeRadius;
        for (i = vertices; i >= 0; --i) {
            angleRadians = Math.PI * 2 * (double)(i + 270) / (double)(vertices * 4);
            GL11.glVertex2d((double)centerX + Math.sin(angleRadians) * (double)edgeRadius, (double)centerY + Math.cos(angleRadians) * (double)edgeRadius);
        }
        GL11.glVertex2d(x, y + height - edgeRadius);
        GL11.glVertex2d(x, y + edgeRadius);
        GL11.glEnd();
        RenderUtils.disableRender2D();
    }

    public static void disableRender2D() {
        GL11.glDisable(3042);
        GL11.glEnable(2884);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static void drawLoadingCircle(float x, float y) {
        int rot = (int)(System.nanoTime() / 5000000L * 5L % 360L);
        RenderUtils.drawCircle(x, y, 10.0f, rot - 180, rot);
    }

    public static void drawImage(ResourceLocation image, int x, int y, int width, int height) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(image);
        RenderUtils.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void drawImage(ResourceLocation image, int x, int y, int width, int height, Color color) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        RenderUtils.glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        mc.getTextureManager().bindTexture(image);
        RenderUtils.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void drawRect2(double x, double y, double width, double height, int color) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(x, y, 0.0).color(color).endVertex();
        worldrenderer.pos(x, y + height, 0.0).color(color).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0).color(color).endVertex();
        worldrenderer.pos(x + width, y, 0.0).color(color).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
    }

    public static void drawExhi(float x, float y, float width, float height) {
        boolean lowerAlpha = false;
        RenderUtils.drawRect2(x, y, width, height, new Color(0.1f, 0.1f, 0.1f, lowerAlpha ? 0.4f : 0.75f).getRGB());
        float percentage = Math.min(1, 1);
        RenderUtils.drawRect2(x + width * percentage, y + height - 1.0f, width - width * percentage, 1.0, Color.WHITE.getRGB());
        FontLoaders.Bold18.drawString("1123", x + 3.0f, y + (float)FontLoaders.Bold18.getHeight() + 1.0f, Color.WHITE.getRGB());
        FontLoaders.Bold18.drawString("2245", x + 7.0f + width, y + 4.0f, Color.WHITE.getRGB());
        FontLoaders.Bold18.drawString("123", x + 7.0f + width, y + 8.5f + height, Color.WHITE.getRGB());
    }

    public static void drawLines(float x, float y, float x1, float y1, float width, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderUtils.glColor(color);
        GL11.glEnable(2848);
        GL11.glLineWidth(width);
        GL11.glBegin(1);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawLines(float x, float y, float x1, float y1, float width) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderUtils.glColor(new Color(255, 255, 255, 255));
        GL11.glEnable(2848);
        GL11.glLineWidth(width);
        GL11.glBegin(1);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawCircle(float x, float y, float radius, int start, int end, float width) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderUtils.glColor(new Color(255, 255, 255, 255));
        GL11.glEnable(2848);
        GL11.glLineWidth(width);
        GL11.glBegin(3);
        for (float i = (float)end; i >= (float)start; i -= 4.0f) {
            GL11.glVertex2f((float)((double)x + Math.cos((double)i * Math.PI / 180.0) * (double)(radius * 1.001f)), (float)((double)y + Math.sin((double)i * Math.PI / 180.0) * (double)(radius * 1.001f)));
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawCircle(float x, float y, float radius, int start, int end, float width, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(2848);
        GL11.glLineWidth(width);
        GL11.glBegin(3);
        RenderUtils.glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        for (float i = (float)end; i >= (float)start; i -= 4.0f) {
            GL11.glVertex2f((float)((double)x + Math.cos((double)i * Math.PI / 180.0) * (double)(radius * 1.001f)), (float)((double)y + Math.sin((double)i * Math.PI / 180.0) * (double)(radius * 1.001f)));
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawShadow(float x, float y, float width, float height) {
        RenderUtils.drawTexturedRect(x - 9.0f, y - 9.0f, 9.0f, 9.0f, "paneltopleft");
        RenderUtils.drawTexturedRect(x - 9.0f, y + height, 9.0f, 9.0f, "panelbottomleft");
        RenderUtils.drawTexturedRect(x + width, y + height, 9.0f, 9.0f, "panelbottomright");
        RenderUtils.drawTexturedRect(x + width, y - 9.0f, 9.0f, 9.0f, "paneltopright");
        RenderUtils.drawTexturedRect(x - 9.0f, y, 9.0f, height, "panelleft");
        RenderUtils.drawTexturedRect(x + width, y, 9.0f, height, "panelright");
        RenderUtils.drawTexturedRect(x, y - 9.0f, width, 9.0f, "paneltop");
        RenderUtils.drawTexturedRect(x, y + height, width, 9.0f, "panelbottom");
    }

    public static void drawTexturedRect(float x, float y, float width, float height, String image) {
        boolean disableAlpha;
        GL11.glPushMatrix();
        boolean enableBlend = GL11.glIsEnabled(3042);
        boolean bl = disableAlpha = !GL11.glIsEnabled(3008);
        if (!enableBlend) {
            GL11.glEnable(3042);
        }
        if (!disableAlpha) {
            GL11.glDisable(3008);
        }
        mc.getTextureManager().bindTexture(new ResourceLocation("shadow/" + image + ".png"));
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtils.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        if (!enableBlend) {
            GL11.glDisable(3042);
        }
        if (!disableAlpha) {
            GL11.glEnable(3008);
        }
        GL11.glPopMatrix();
    }

    public static void drawModalRectWithCustomSizedTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        float f = 1.0f / textureWidth;
        float f1 = 1.0f / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0).tex(u * f, (v + height) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0).tex((u + width) * f, (v + height) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0).tex((u + width) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    public static void drawSCP(float x, float y, float size) {
        RenderUtils.drawSCPAnimationA(x, y, size, 360);
    }

    public static void drawSCPAnimationB(float x, float y, float size, int animationStep) {
        int width = 17;
        int move = 30 + animationStep;
        int radius = 15;
        int lineWidth = 5;
        GL11.glPushMatrix();
        GL11.glScalef(size, size, 0.0f);
        GL11.glTranslated(x / size, y / size, 0.0);
        RenderUtils.drawCircle(0.0f, 0.0f, radius, 0 + width + move, 120 - width + move + animationStep, lineWidth);
        RenderUtils.drawCircle(0.0f, 0.0f, radius, 120 + width + move, 240 - width + move, lineWidth);
        RenderUtils.drawCircle(0.0f, 0.0f, radius, 240 + width + move, 360 - width + move, lineWidth);
        int interval = 8;
        Color color = new Color(255, 255, 255, 255);
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval, 0 + width + move, 120 - width + move, lineWidth - 2, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval, 120 + width + move, 240 - width + move, lineWidth - 2, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval, 240 + width + move, 360 - width + move, lineWidth - 2, color);
        int interpolation = width / 5;
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval + 5, 0 - (width -= interpolation) + move, 0 + width + move, lineWidth - 2, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval + 5, 120 - width + move, 120 + width + move, lineWidth - 2, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval + 5, 240 - width + move, 240 + width + move, lineWidth - 2, color);
        for (int rotationYaw = 0; rotationYaw <= 360; rotationYaw += 120) {
            Vector2f pointA = RenderUtils.getCirclePoint(0.0f, 0.0f, radius + interval, rotationYaw - width + move - interpolation);
            Vector2f pointAA = RenderUtils.getCirclePoint(0.0f, 0.0f, (float)(radius + interval) + (float)interval / 2.0f, rotationYaw - width + move);
            RenderUtils.drawLines(pointA.x, pointA.y, pointAA.x, pointAA.y, lineWidth - 2, color);
            Vector2f pointB = RenderUtils.getCirclePoint(0.0f, 0.0f, radius + interval, rotationYaw + width + move + interpolation);
            Vector2f pointBB = RenderUtils.getCirclePoint(0.0f, 0.0f, (float)(radius + interval) + (float)interval / 2.0f, rotationYaw + width + move);
            RenderUtils.drawLines(pointB.x, pointB.y, pointBB.x, pointBB.y, lineWidth - 2, color);
        }
        RenderUtils.drawArrowHead(0.0f, 0.0f, 7.0f, 15.0f, 2.0f, 0 + animationStep, 0.0f, 0.0f);
        RenderUtils.drawArrowHead(0.0f, 0.0f, 7.0f, 15.0f, 2.0f, 120 + animationStep, 0.0f, 0.0f);
        RenderUtils.drawArrowHead(0.0f, 0.0f, 7.0f, 15.0f, 2.0f, 240 + animationStep, 0.0f, 0.0f);
        GL11.glPopMatrix();
    }

    public static void drawEntityBox(Entity ent, Color color, int alpha) {
        RenderManager renderManager = mc.getRenderManager();
        Timer timer = RenderUtils.mc.timer;
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glDepthMask(false);
        double x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double)timer.renderPartialTicks - renderManager.getRenderPosX();
        double y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * (double)timer.renderPartialTicks - renderManager.getRenderPosY();
        double z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double)timer.renderPartialTicks - renderManager.getRenderPosZ();
        AxisAlignedBB entityBox = ent.getEntityBoundingBox();
        AxisAlignedBB axix = new AxisAlignedBB(entityBox.minX - 0.2 - ent.posX, entityBox.minY - 0.1 - ent.posY, entityBox.minZ - 0.2 - ent.posZ, entityBox.maxX + 0.2 - ent.posX, entityBox.maxY + 0.2 - ent.posY, entityBox.maxZ + 0.2 - ent.posZ);
        RenderUtils.glColor(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        RenderUtils.drawFilledBox(axix);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
    }

    public static void drawFilledBox(AxisAlignedBB axisAlignedBB) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        tessellator.draw();
    }

    public static void drawSCPAnimationA(float x, float y, float size, int animationStep) {
        int width = 17;
        int move = 30;
        int radius = 15;
        int lineWidth = 5;
        GL11.glPushMatrix();
        GL11.glScalef(size, size, 0.0f);
        GL11.glTranslated(x / size, y / size, 0.0);
        Color color = new Color(255, 255, 255, (int)(255.0f * Math.min(1.0f, (float)animationStep / 360.0f)));
        RenderUtils.drawCircle(0.0f, 0.0f, radius, 0 + width + move, 120 - width + move, lineWidth, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius, 120 + width + move, 240 - width + move, lineWidth, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius, 240 + width + move, 360 - width + move, lineWidth, color);
        int interval = 8;
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval, 0 + width + move, 120 - width + move, lineWidth - 2, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval, 120 + width + move, 240 - width + move, lineWidth - 2, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval, 240 + width + move, 360 - width + move, lineWidth - 2, color);
        int interpolation = width / 5;
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval + 5, 0 - (width -= interpolation) + move, 0 + width + move, lineWidth - 2, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval + 5, 120 - width + move, 120 + width + move, lineWidth - 2, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval + 5, 240 - width + move, 240 + width + move, lineWidth - 2, color);
        for (int rotationYaw = 0; rotationYaw < 360; rotationYaw += 120) {
            Vector2f pointA = RenderUtils.getCirclePoint(0.0f, 0.0f, radius + interval, rotationYaw - width + move - interpolation);
            Vector2f pointAA = RenderUtils.getCirclePoint(0.0f, 0.0f, (float)(radius + interval) + (float)interval / 2.0f, rotationYaw - width + move);
            RenderUtils.drawLines(pointA.x, pointA.y, pointAA.x, pointAA.y, lineWidth - 2, color);
            Vector2f pointB = RenderUtils.getCirclePoint(0.0f, 0.0f, radius + interval, rotationYaw + width + move + interpolation);
            Vector2f pointBB = RenderUtils.getCirclePoint(0.0f, 0.0f, (float)(radius + interval) + (float)interval / 2.0f, rotationYaw + width + move);
            RenderUtils.drawLines(pointB.x, pointB.y, pointBB.x, pointBB.y, lineWidth - 2, color);
        }
        float flyOut = 0.0f;
        int turnAround = 0;
        RenderUtils.drawArrowHead(0.0f, 0.0f, 7.0f, 15.0f, 2.0f, 0 + turnAround, 0.0f, flyOut, color);
        RenderUtils.drawArrowHead(0.0f, 0.0f, 7.0f, 15.0f, 2.0f, 120 + turnAround, 0.0f, flyOut, color);
        RenderUtils.drawArrowHead(0.0f, 0.0f, 7.0f, 15.0f, 2.0f, 240 + turnAround, 0.0f, flyOut, color);
        GL11.glPopMatrix();
    }

    public static void drawSCPAnimationC(float x, float y, float size, int animationStep) {
        int width = 17;
        int move = 30;
        int radius = 15;
        int lineWidth = 5;
        GL11.glPushMatrix();
        GL11.glScalef(size, size, 0.0f);
        GL11.glTranslated(x / size, y / size, 0.0);
        Color color = new Color(255, 255, 255, 255);
        int turnSPeed = 5;
        RenderUtils.drawCircle(0.0f, 1000000.0f, radius, 0, 1, lineWidth, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius, 360 + width + move + animationStep * turnSPeed, 480 - width + move + animationStep * turnSPeed, lineWidth, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius, 120 + width + move + animationStep * turnSPeed, 240 - width + move + animationStep * turnSPeed, lineWidth, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius, 240 + width + move + animationStep * turnSPeed, 360 - width + move + animationStep * turnSPeed, lineWidth, color);
        int interval = 8;
        color = Color.WHITE;
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval, 0 + width + move, 120 - width + move, lineWidth - 2, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval, 120 + width + move, 240 - width + move, lineWidth - 2, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval, 240 + width + move, 360 - width + move, lineWidth - 2, color);
        int interpolation = width / 5;
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval + 5, 0 - (width -= interpolation) + move, 0 + width + move, lineWidth - 2, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval + 5, 120 - width + move, 120 + width + move, lineWidth - 2, color);
        RenderUtils.drawCircle(0.0f, 0.0f, radius + interval + 5, 240 - width + move, 240 + width + move, lineWidth - 2, color);
        for (int rotationYaw = 0; rotationYaw < 360; rotationYaw += 120) {
            Vector2f pointA = RenderUtils.getCirclePoint(0.0f, 0.0f, radius + interval, rotationYaw - width + move - interpolation);
            Vector2f pointAA = RenderUtils.getCirclePoint(0.0f, 0.0f, (float)(radius + interval) + (float)interval / 2.0f, rotationYaw - width + move);
            RenderUtils.drawLines(pointA.x, pointA.y, pointAA.x, pointAA.y, lineWidth - 2, color);
            Vector2f pointB = RenderUtils.getCirclePoint(0.0f, 0.0f, radius + interval, rotationYaw + width + move + interpolation);
            Vector2f pointBB = RenderUtils.getCirclePoint(0.0f, 0.0f, (float)(radius + interval) + (float)interval / 2.0f, rotationYaw + width + move);
            RenderUtils.drawLines(pointB.x, pointB.y, pointBB.x, pointBB.y, lineWidth - 2, color);
        }
        float flyOut = 0.0f;
        int turnAround = 0;
        flyOut = animationStep <= 180 ? (flyOut += -45.0f * ((float)animationStep / 180.0f)) : -45.0f * ((float)(360 - animationStep) / 180.0f);
        RenderUtils.drawArrowHead(0.0f, 0.0f, 7.0f, 15.0f, 2.0f, 0 + turnAround * 2, 0.0f, flyOut);
        RenderUtils.drawArrowHead(0.0f, 0.0f, 7.0f, 15.0f, 2.0f, 120 + turnAround * 2, 0.0f, flyOut);
        RenderUtils.drawArrowHead(0.0f, 0.0f, 7.0f, 15.0f, 2.0f, 240 + turnAround * 2, 0.0f, flyOut);
        GL11.glPopMatrix();
    }

    public static void drawCircle(float x, float y, float radius, int start, int end) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderUtils.glColor(new Color(255, 255, 255, 255));
        GL11.glEnable(2848);
        GL11.glLineWidth(5.0f);
        GL11.glBegin(3);
        for (float i = (float)end; i >= (float)start; i -= 4.0f) {
            GL11.glVertex2f((float)((double)x + Math.cos((double)i * Math.PI / 180.0) * (double)(radius * 1.001f)), (float)((double)y + Math.sin((double)i * Math.PI / 180.0) * (double)(radius * 1.001f)));
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawTriangle(float x, float y, float baseSide, float height) {
        GL11.glPushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int color = Color.white.getRGB();
        worldrenderer.begin(4, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(x, y, 0.0).color(color).endVertex();
        worldrenderer.pos(x + baseSide / 2.0f, y - height, 0.0).color(color).endVertex();
        worldrenderer.pos(x - baseSide / 2.0f, y - height, 0.0).color(color).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GL11.glPopMatrix();
    }

    public static void drawArrowHead(float x, float y, float traWidth, float traHeight, float traRectWith, float angle, float moveX, float moveY) {
        RenderUtils.drawArrowHead(x, y, traWidth, traHeight, traRectWith, angle, moveX, moveY, Color.WHITE);
    }

    public static void drawArrowHead(float x, float y, float traWidth, float traHeight, float traRectWith, float angle, float moveX, float moveY, Color color) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0.0);
        GL11.glRotatef(angle, 0.0f, 0.0f, 1.0f);
        RenderUtils.drawRect2(moveX - traRectWith / 2.0f, moveY - 2.0f - traHeight - traWidth, traRectWith, traHeight, color.getRGB());
        RenderUtils.drawTriangle(moveX, moveY - 2.0f, traWidth, traWidth);
        GL11.glPopMatrix();
    }

    public static void renderPlayer2D(float x, float y, float width, float height, AbstractClientPlayer player) {
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(player.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(x, y, 8.0f, 8.0f, 8.0f, 8.0f, width, height, 64.0f, 64.0f);
        GlStateManager.disableBlend();
    }

    public static void drawHead(ResourceLocation skin, float x, float y, float scale, int width, int height, float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0.0f);
        GL11.glScalef(scale, scale, scale);
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(RenderUtils.coerceIn(red, 0.0f, 1.0f), RenderUtils.coerceIn(green, 0.0f, 1.0f), RenderUtils.coerceIn(blue, 0.0f, 1.0f), RenderUtils.coerceIn(alpha, 0.0f, 1.0f));
        mc.getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(0, 0, 8.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static Vector2f getCirclePoint(float circleX, float circleY, float radius, int angle) {
        return new Vector2f((float)((double)circleX + Math.cos((double)angle * Math.PI / 180.0) * (double)(radius * 1.001f)), (float)((double)circleY + Math.sin((double)angle * Math.PI / 180.0) * (double)(radius * 1.001f)));
    }

    public static void drawBlockBox(BlockPos blockPos, Color color, boolean outline) {
        RenderManager renderManager = mc.getRenderManager();
        Timer timer = RenderUtils.mc.timer;
        double x = (double)blockPos.getX() - renderManager.getRenderPosX();
        double y = (double)blockPos.getY() - renderManager.getRenderPosY();
        double z = (double)blockPos.getZ() - renderManager.getRenderPosZ();
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        Block block = BlockUtils.getBlock(blockPos);
        if (block != null) {
            EntityPlayerSP player = RenderUtils.mc.thePlayer;
            double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)timer.renderPartialTicks;
            double posY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)timer.renderPartialTicks;
            double posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)timer.renderPartialTicks;
            axisAlignedBB = block.getSelectedBoundingBox(RenderUtils.mc.theWorld, blockPos).expand(0.002f, 0.002f, 0.002f).offset(-posX, -posY, -posZ);
        }
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        RenderUtils.glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        RenderUtils.drawFilledBox(axisAlignedBB);
        if (outline) {
            GL11.glLineWidth(1.0f);
            RenderUtils.enableGlCap(2848);
            RenderUtils.drawSelectionBoundingBox(axisAlignedBB);
        }
        GL11.glDepthMask(true);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
    }

    public static void sigmaRing(EntityLivingBase player, Color color) {
        double vecZ;
        double vecX;
        float partialTicks = RenderUtils.mc.timer.renderPartialTicks;
        if (mc.getRenderManager() == null || player == null) {
            return;
        }
        double x = player.prevPosX + (player.posX - player.prevPosX) * (double)partialTicks - mc.getRenderManager().getRenderPosX();
        double y = player.prevPosY + (player.posY - player.prevPosY) * (double)partialTicks + Math.sin((double)System.currentTimeMillis() / 200.0) + 1.0 - mc.getRenderManager().getRenderPosY();
        double z = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)partialTicks - mc.getRenderManager().getRenderPosZ();
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDepthMask(false);
        GlStateManager.alphaFunc(516, 0.0f);
        GL11.glShadeModel(7425);
        GlStateManager.disableCull();
        GL11.glBegin(5);
        float i = 0.0f;
        while ((double)i <= 6.4795348480289485) {
            vecX = x + 0.67 * Math.cos(i);
            vecZ = z + 0.67 * Math.sin(i);
            RenderUtils.glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            GL11.glVertex3d(vecX, y, vecZ);
            i = (float)((double)i + 0.19634954084936207);
        }
        i = 0.0f;
        while ((double)i <= 6.4795348480289485) {
            vecX = x + 0.67 * Math.cos(i);
            vecZ = z + 0.67 * Math.sin(i);
            RenderUtils.glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            GL11.glVertex3d(vecX, y, vecZ);
            RenderUtils.glColor(color.getRed(), color.getGreen(), color.getBlue(), 0);
            GL11.glVertex3d(vecX, y - Math.cos((double)System.currentTimeMillis() / 200.0) / 2.0, vecZ);
            i = (float)((double)i + 0.19634954084936207);
        }
        GL11.glEnd();
        GL11.glShadeModel(7424);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }

    private static float coerceIn(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static void drawJDT(int x, int y, int width, int height, float step, boolean color, Color backGroundColor) {
        Color color2 = RenderUtils.getGradientOffset(new Color(ClientColor.INSTANCE.mixR1, ClientColor.INSTANCE.mixG1, ClientColor.INSTANCE.mixB1, 255), new Color(ClientColor.INSTANCE.mixR2, ClientColor.INSTANCE.mixG2, ClientColor.INSTANCE.mixB2, 255), ((double)(RenderUtils.mc.thePlayer.ticksExisted * 4) + 2.0) % 400.0 / 100.0, 255);
        Color color3 = RenderUtils.getGradientOffset(new Color(ClientColor.INSTANCE.mixR1, ClientColor.INSTANCE.mixG1, ClientColor.INSTANCE.mixB1, 255), new Color(ClientColor.INSTANCE.mixR2, ClientColor.INSTANCE.mixG2, ClientColor.INSTANCE.mixB2, 255), ((double)(RenderUtils.mc.thePlayer.ticksExisted * 4) + 2.0 + 65.0) % 400.0 / 100.0, 255);
        RenderUtils.drawRDRect(x, y, width, height, backGroundColor.getRGB());
        RenderUtils.drawHGradientRect(x, y, (float)width * step, height, color2.getRGB(), color3.getRGB());
    }
}

