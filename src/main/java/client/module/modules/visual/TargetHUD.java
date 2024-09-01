/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.visual;

import client.Client;
import client.event.events.Render2DEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.combat.KillAura;
import client.module.modules.visual.ClientColor;
import client.ui.element.Element;
import client.ui.element.ElementManager;
import client.ui.fastuni.FastUniFontRenderer;
import client.ui.fastuni.FontLoader;
import client.ui.font.FontLoaders;
import client.utils.ClientUtils;
import client.utils.ColorUtil;
import client.utils.RenderUtils;
import client.utils.RoundedUtil;
import client.utils.StencilUtil;
import client.utils.anim.ContinualAnimation;
import java.awt.Color;
import java.text.DecimalFormat;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class TargetHUD
extends Module {
    private final ContinualAnimation animation = new ContinualAnimation();
    private final DecimalFormat DF_1 = new DecimalFormat("0.0");
    @Settings(list={"LongAndBig", "SCP", "BigHitPoint", "MCPLITE", "Test"})
    private String mode = "LongAndBig";
    @Settings(maxValue=255.0)
    private int HPR = 98;
    @Settings(maxValue=255.0)
    private int HPG = 98;
    @Settings(maxValue=255.0)
    private int HPB = 98;
    @Settings(maxValue=255.0)
    private int HPAlpha = 128;
    @Settings(maxValue=255.0)
    private int BGAlpha = 100;
    @Settings(minValue=1.0, maxValue=100.0)
    private float speed = 5.0f;
    @Settings(list={"NONE", "A", "B", "C", "D"})
    private String aniMode = "D";
    private AbstractClientPlayer target;
    private AbstractClientPlayer renderIng;
    private float aniStep = 0.0f;

    public TargetHUD() {
        super("TargetHUD", 0, false, ModuleType.RENDER);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        boolean speedMove = false;
        if (this.aniStep > 0.0f) {
            this.drawTarget(this.aniStep, this.aniMode);
        } else {
            this.renderIng = null;
        }
        if (this.target != null) {
            this.renderIng = this.target;
            if (this.aniStep < 1.0f) {
                this.aniStep = speedMove ? (float)((double)this.aniStep + Math.max(0.001, (double)((1.0f - this.aniStep) / 100.0f * this.speed))) : (float)((double)this.aniStep + 0.001 * (double)this.speed);
            }
        } else if (this.aniStep > 0.0f) {
            this.aniStep = speedMove ? (float)((double)this.aniStep - Math.max(0.001, (double)((1.0f - this.aniStep) / 100.0f * this.speed))) : (float)((double)this.aniStep - 0.001 * (double)this.speed);
        }
    }

    @Override
    public void onTick() {
        if (ClientUtils.nullCheck()) {
            return;
        }
        if (this.target == null) {
            KillAura killAura = (KillAura)Client.moduleManager.moduleMap.get(KillAura.class);
            if (TargetHUD.mc.objectMouseOver != null && TargetHUD.mc.objectMouseOver.entityHit instanceof AbstractClientPlayer) {
                this.target = (AbstractClientPlayer)TargetHUD.mc.objectMouseOver.entityHit;
            } else if (killAura.target != null) {
                this.target = (AbstractClientPlayer)killAura.target;
            } else if (TargetHUD.mc.currentScreen instanceof GuiChat) {
                this.target = TargetHUD.mc.thePlayer;
            }
        } else if (this.target.getHealth() == 0.0f) {
            this.target = null;
        } else if (TargetHUD.mc.thePlayer.getDistanceToEntity(this.target) > 6.0f) {
            this.target = null;
        } else if (this.target.getEntityId() == TargetHUD.mc.thePlayer.getEntityId() && TargetHUD.mc.currentScreen == null) {
            this.target = null;
        }
    }

    @Override
    public void onWorldLoad() {
    }

    private void drawTarget(float loadStep, String aniMode) {
        float playerMiddle;
        float y;
        float x;
        Element element = ElementManager.targetHUD;
        loadStep = Math.min(1.0f, loadStep);
        loadStep = Math.max(0.0f, loadStep);
        float width = 0.0f;
        float height = 0.0f;
        boolean animA = aniMode.equalsIgnoreCase("A");
        boolean animB = aniMode.equalsIgnoreCase("B");
        boolean animC = aniMode.equalsIgnoreCase("C");
        boolean animD = aniMode.equalsIgnoreCase("D");
        boolean animSpecial = false;
        if (this.mode.equalsIgnoreCase("MCPLITE")) {
            width = 140.0f;
            height = 34.0f;
        }
        if (this.mode.equalsIgnoreCase("Test")) {
            width = 140.0f;
            height = 34.0f;
        }
        if (this.mode.equalsIgnoreCase("longandbig")) {
            width = 184.0f;
            height = 40.0f;
        }
        if (this.mode.equalsIgnoreCase("SCP")) {
            width = 120.0f;
            height = 30.0f;
            animA = false;
            animB = false;
            animC = false;
            animD = false;
            animSpecial = true;
        }
        if (this.mode.equalsIgnoreCase("BigHitPoint")) {
            width = 120.0f;
            height = 30.0f;
        }
        element.width = width;
        element.height = height;
        if (animA) {
            width *= loadStep;
            height *= loadStep;
            StencilUtil.initStencilToWrite();
        }
        GL11.glPushMatrix();
        if (animC) {
            GL11.glScalef(this.aniStep, this.aniStep, 0.0f);
        }
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        if (this.mode.equalsIgnoreCase("scp")) {
            x = (float)scaledResolution.getScaledWidth() / 2.0f - width / 2.0f;
            y = (int)((float)scaledResolution.getScaledHeight() / 3.0f);
            element.posY = y += element.moveY;
            element.posX = x += element.moveX;
            float theHeight = Math.min(height * (this.aniStep / 0.2f), 30.0f);
            float theWidth = Math.max(theHeight, width * this.aniStep);
            GL11.glTranslated(x + width / 2.0f - theWidth / 2.0f, y, 0.0);
            x = 0.0f;
            y = 0.0f;
            playerMiddle = 5.0f;
            int size = 6;
            GL11.glPushMatrix();
            GL11.glScaled(size, size, 0.0);
            RenderUtils.drawRoundedRect(x, y, theWidth / (float)size, theHeight / 6.0f, 10.0f, new Color(0, 0, 0, 255).getRGB(), 3.0f, new Color(0, 0, 0, 255).getRGB());
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderUtils.drawRoundedRect(x, y, theWidth * (this.renderIng.getHealth() / 20.0f) / (float)size, theHeight / 6.0f, 10.0f, new Color(100, 100, 100, 120).getRGB(), 1.0f, new Color(100, 100, 100, 100).getRGB());
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
            StencilUtil.initStencilToWrite();
            RenderUtils.drawRoundedRect(x + playerMiddle, y + playerMiddle, theHeight - playerMiddle * 2.0f * (theHeight / height), theHeight - playerMiddle * 2.0f * (theHeight / height), 10.0f, -1, 1.0f, -1);
            StencilUtil.readStencilBuffer(1);
            RenderUtils.renderPlayer2D(x + playerMiddle, y + playerMiddle - 1.0f, 30.0f - playerMiddle * 2.0f, 30.0f - playerMiddle * 2.0f, this.renderIng);
            StencilUtil.uninitStencilBuffer();
            StencilUtil.initStencilToWrite();
            GL11.glPushMatrix();
            GL11.glScaled(size, size, 0.0);
            RenderUtils.drawRoundedRect(x, y, theWidth / (float)size, theHeight / 6.0f, 10.0f, new Color(0, 0, 0, 75).getRGB(), 3.0f, new Color(0, 0, 0, 0).getRGB());
            GL11.glPopMatrix();
            GL11.glColor4f(255.0f, 255.0f, 255.0f, 0.0f);
            StencilUtil.readStencilBuffer(1);
            FontLoaders.GoogleSans22.drawString("ID: " + this.renderIng.getEntityId(), x + 30.0f + 5.0f, y + 20.0f - (float)FontLoaders.GoogleSans22.getHeight(), new Color(255, 255, 255, 255).getRGB());
            FontLoaders.GoogleSans12.drawString("Secure. Contain. Protect.", x + 20.0f, y + 30.0f - 4.0f, new Color(255, 255, 255, 255).getRGB());
            StencilUtil.uninitStencilBuffer();
            RenderUtils.drawSCPAnimationA(x + Math.max(105.0f * this.aniStep, 15.0f), y + 16.5f, 0.4f, Math.min(360, (int)(360.0f * Math.min(1.0f, this.aniStep))));
        }
        if (this.mode.equalsIgnoreCase("bighitpoint")) {
            int x2 = (int)((float)scaledResolution.getScaledWidth() / 2.0f - width / 2.0f);
            int y2 = (int)((float)scaledResolution.getScaledHeight() / 3.0f);
            y2 = (int)((float)y2 + element.moveY);
            element.posY = y2;
            x2 = (int)((float)x2 + element.moveX);
            element.posX = x2;
            GL11.glTranslated(x2, y2, 0.0);
            if (animD) {
                GL11.glTranslated(-x2, -y2, 0.0);
                GL11.glTranslated((float)x2 + width / 2.0f - width / 2.0f * this.aniStep, (float)y2 + height / 2.0f - height / 2.0f * this.aniStep, 0.0);
                GL11.glScalef(this.aniStep, this.aniStep, 0.0f);
            }
            x = 0.0f;
            y = 0.0f;
            if (animB) {
                GL11.glScalef(this.aniStep, this.aniStep, 0.0f);
            }
            playerMiddle = 5.0f;
            if (animA) {
                RenderUtils.drawRDRect(x, y, width, height, -1);
                StencilUtil.readStencilBuffer(1);
            }
            RenderUtils.drawRDRect(x, y, width, height, new Color(0, 0, 0, this.BGAlpha).getRGB());
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderUtils.drawRDRect(x, y, width * (this.renderIng.getHealth() / 20.0f), height, new Color(this.HPR, this.HPG, this.HPB, this.HPAlpha).getRGB());
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderUtils.renderPlayer2D(x + playerMiddle, y + playerMiddle, 30.0f - playerMiddle * 2.0f, 30.0f - playerMiddle * 2.0f, this.renderIng);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            DecimalFormat d = new DecimalFormat("#.0");
            FontLoaders.GoogleSans18.drawString("HEALTH : ", x + 30.0f, y + 20.0f - (float)FontLoaders.GoogleSans35.getHeight() + 6.0f, Color.white.getRGB());
            FontLoaders.GoogleSans35.drawString(d.format(this.renderIng.getHealth()), x + 75.0f, y + 20.0f - (float)FontLoaders.GoogleSans35.getHeight(), Color.white.getRGB());
            FontLoader.simpleFont18.drawString(this.renderIng.getName(), x + 30.0f, y + 20.0f - (float)FontLoaders.GoogleSans35.getHeight() + 3.0f + (float)(FontLoader.miFont12.FONT_HEIGHT * 2), Color.white.getRGB());
            if (animA) {
                StencilUtil.uninitStencilBuffer();
            }
            RenderUtils.drawShadow(x, y, width, height);
        }
        if (this.mode.equalsIgnoreCase("longandbig")) {
            x = (float)scaledResolution.getScaledWidth() / 2.0f - width / 2.0f;
            y = (int)((float)scaledResolution.getScaledHeight() / 3.0f);
            element.posY = y += element.moveY;
            element.posX = x += element.moveX;
            GL11.glTranslated(x, y, 0.0);
            if (animD) {
                GL11.glTranslated(-x, -y, 0.0);
                GL11.glTranslated(x + width / 2.0f - width / 2.0f * this.aniStep, y + height / 2.0f - height / 2.0f * this.aniStep, 0.0);
                GL11.glScalef(this.aniStep, this.aniStep, 0.0f);
            }
            x = 0.0f;
            y = 0.0f;
            if (animB) {
                GL11.glScalef(this.aniStep, this.aniStep, 0.0f);
            }
            float playerMiddle2 = 4.0f;
            if (animA) {
                RenderUtils.drawRoundedRect(x, y, width, height, 5.0f, new Color(0, 0, 0, 150).getRGB(), 1.0f, new Color(0, 0, 0, 50).getRGB());
                StencilUtil.readStencilBuffer(1);
            }
            RenderUtils.drawRoundedRect(x, y, width, height, 5.0f, new Color(0, 0, 0, 150).getRGB(), 1.0f, new Color(0, 0, 0, 50).getRGB());
            RenderUtils.renderPlayer2D(x + playerMiddle2, y + playerMiddle2, 40.0f - playerMiddle2 * 2.0f, 40.0f - playerMiddle2 * 2.0f, this.renderIng);
            float HPHeight = 4.0f;
            RenderUtils.drawRoundedRect(x + 40.0f, y + 20.0f + 6.0f - HPHeight, 141.0f * (this.renderIng.getHealth() / 20.0f), HPHeight + 3.0f, 2.0f, new Color(ClientColor.INSTANCE.getMixColor().getRed(), ClientColor.INSTANCE.getMixColor().getGreen(), ClientColor.INSTANCE.getMixColor().getBlue(), 255).getRGB(), 1.0f, new Color(ClientColor.INSTANCE.getMixColor().getRed(), ClientColor.INSTANCE.getMixColor().getGreen(), ClientColor.INSTANCE.getMixColor().getBlue(), 255).getRGB());
            RenderUtils.drowColorStringUni(FontLoader.miFont20, this.renderIng.getName(), (int)x + 40, (int)y + 11, 255);
            FontLoader.miFont20.drawString("H:" + this.renderIng.getHealth(), (float)((int)x + 40 + 4 + FontLoader.miFont20.getStringWidth(this.renderIng.getName())), (int)y + 10, new Color(255, 255, 255, 255).getRGB());
            if (animA) {
                StencilUtil.uninitStencilBuffer();
            }
        }
        if (this.mode.equalsIgnoreCase("MCPLITE")) {
            x = (float)scaledResolution.getScaledWidth() / 2.0f - width / 2.0f;
            y = (int)((float)scaledResolution.getScaledHeight() / 3.0f);
            element.posY = y += element.moveY;
            element.posX = x += element.moveX;
            GL11.glTranslated(x, y, 0.0);
            if (animD) {
                float size = 0.7f + 0.3f * this.aniStep;
                GL11.glTranslated(-x, -y, 0.0);
                GL11.glTranslated(x + width / 2.0f - width / 2.0f * size, y + height / 2.0f - height / 2.0f * size, 0.0);
                GL11.glScalef(size, size, 0.0f);
            }
            x = 0.0f;
            y = 0.0f;
            Color color = new Color(20, 20, 20, (int)((float)this.BGAlpha * Math.max(Math.min(this.aniStep, 1.0f), 0.01f)));
            Color color1 = new Color(190, 0, 255, (int)(235.0f * Math.max(Math.min(this.aniStep, 1.0f), 0.01f)));
            DecimalFormat decimalFormat = new DecimalFormat("#.##%");
            String name = this.renderIng.getName();
            float health = this.renderIng.getHealth();
            RenderUtils.drawRoundedRect(x, y, width, height, 2.0f, color.getRGB(), 1.0f, color.getRGB());
            RenderUtils.drawShadow(x, y, width, height);
            FastUniFontRenderer fontRenderer = FontLoader.QuickSandMedium24;
            float playerWidth = height - 8.0f;
            StencilUtil.initStencilToWrite();
            RenderUtils.drawRoundedRect(x + 4.0f, y + 4.0f, playerWidth, playerWidth, 3.0f, color.getRGB(), 1.0f, color.getRGB());
            StencilUtil.readStencilBuffer(1);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f * Math.max(Math.min(this.aniStep, 1.0f), 0.01f));
            RenderUtils.renderPlayer2D(x + 4.0f, y + 4.0f, playerWidth, playerWidth, this.renderIng);
            StencilUtil.uninitStencilBuffer();
            Color color2 = RenderUtils.getGradientOffset(new Color(ClientColor.INSTANCE.mixR1, ClientColor.INSTANCE.mixG1, ClientColor.INSTANCE.mixB1, (int)(255.0f * Math.max(Math.min(this.aniStep, 1.0f), 0.01f))), new Color(ClientColor.INSTANCE.mixR2, ClientColor.INSTANCE.mixG2, ClientColor.INSTANCE.mixB2, (int)(255.0f * Math.max(Math.min(this.aniStep, 1.0f), 0.01f))), ((double)(TargetHUD.mc.thePlayer.ticksExisted * 4) + 2.0) % 400.0 / 100.0, (int)(255.0f * Math.max(Math.min(this.aniStep, 1.0f), 0.01f)));
            Color color3 = RenderUtils.getGradientOffset(new Color(ClientColor.INSTANCE.mixR1, ClientColor.INSTANCE.mixG1, ClientColor.INSTANCE.mixB1, (int)(255.0f * Math.max(Math.min(this.aniStep, 1.0f), 0.01f))), new Color(ClientColor.INSTANCE.mixR2, ClientColor.INSTANCE.mixG2, ClientColor.INSTANCE.mixB2, (int)(255.0f * Math.max(Math.min(this.aniStep, 1.0f), 0.01f))), ((double)(TargetHUD.mc.thePlayer.ticksExisted * 4) + 2.0 + 25.0) % 400.0 / 100.0, (int)(255.0f * Math.max(Math.min(this.aniStep, 1.0f), 0.01f)));
            StencilUtil.initStencilToWrite();
            RenderUtils.drawRoundedRect(x + 35.0f, y + 20.0f, width - playerWidth - 13.0f, 8.0f, 3.0f, new Color(ClientColor.INSTANCE.getMixColor().getRed(), ClientColor.INSTANCE.getMixColor().getGreen(), ClientColor.INSTANCE.getMixColor().getBlue(), (int)(255.0f * Math.max(Math.min(this.aniStep, 1.0f), 0.01f))).getRGB(), 1.0f, new Color(ClientColor.INSTANCE.getMixColor().getRed(), ClientColor.INSTANCE.getMixColor().getGreen(), ClientColor.INSTANCE.getMixColor().getBlue(), (int)(255.0f * Math.max(Math.min(this.aniStep, 1.0f), 0.01f))).getRGB());
            StencilUtil.readStencilBuffer(1);
            RenderUtils.drawRDRect(x + 35.0f, y + 20.0f, width - playerWidth - 13.0f, 8.0, new Color(color2.getRed(), color3.getGreen(), color2.getBlue(), (int)(50.0f * Math.max(Math.min(this.aniStep, 1.0f), 0.01f))).getRGB());
            RenderUtils.drawHGradientRect(x + 35.0f, y + 20.0f, (width - playerWidth - 13.0f) * (this.renderIng.getHealth() / this.renderIng.getMaxHealth()), 8.0, color2.getRGB(), color3.getRGB());
            StencilUtil.uninitStencilBuffer();
            RenderUtils.drawHGradientRect(x, y + height / 2.0f - 5.0f, 2.0, 10.0, color2.getRGB(), color3.getRGB());
            FontLoader.miMiFont22.drawString(name, x + 35.0f, y + 10.0f, new Color(255, 255, 255, (int)(255.0f * Math.max(Math.min(this.aniStep, 1.0f), 0.01f))).getRGB());
        }
        if (this.mode.equalsIgnoreCase("Test")) {
            x = (float)scaledResolution.getScaledWidth() / 2.0f - width / 2.0f;
            y = (int)((float)scaledResolution.getScaledHeight() / 3.0f);
            element.posY = y += element.moveY;
            element.posX = x += element.moveX;
            Color c1 = ColorUtil.applyOpacity(new Color(121, 255, 166, 120), 60.0f);
            Color c2 = ColorUtil.applyOpacity(new Color(70, 173, 246, 120), 60.0f);
            Color color = new Color(20, 18, 18, (int)(90.0f * this.aniStep));
            int textColor = ColorUtil.applyOpacity(-1, 40.0f);
            RoundedUtil.drawRound(x, y, x, y, 5.0f, color);
            if (this.target instanceof AbstractClientPlayer) {
                StencilUtil.initStencilToWrite();
                RenderUtils.renderRoundedRect(x + 3.0f, y + 3.0f, 31.0f, 31.0f, 6.0f, -1);
                StencilUtil.readStencilBuffer(1);
                RenderUtils.color(-1, 100.0f);
                RenderUtils.renderPlayer2D(x + 3.0f, y + 3.0f, 31.0f, 31.0f, this.target);
                StencilUtil.uninitStencilBuffer();
                GlStateManager.disableBlend();
            } else {
                FontLoaders.Tenacity24.drawCenteredStringWithShadow("?", x + 20.0f, y + 17.0f - (float)FontLoaders.Tenacity24.getHeight() / 2.0f, textColor);
            }
            float realHealthHeight = 5.0f;
            float realHealthWidth = x - 44.0f;
            float healthWidth = this.animation.getOutput();
            float healthPercent = MathHelper.clamp_float((this.target.getHealth() + this.target.getAbsorptionAmount()) / (this.target.getMaxHealth() + this.target.getAbsorptionAmount()), 0.0f, 1.0f);
            RoundedUtil.drawGradientHorizontal(x + 37.5f, y + y - 10.5f, healthWidth - 7.5f, realHealthHeight, 2.5f, c1, c2);
            this.animation.animate(realHealthWidth * healthPercent, 18);
            FontLoaders.Tenacity18.drawStringWithShadow(this.target.getName(), x + 35.5f, y + 5.0f, textColor);
            FontLoaders.Tenacity18.drawStringWithShadow("Distance: " + this.DF_1.format(TargetHUD.mc.thePlayer.getDistanceToEntity(this.target)), x + 35.5f, y + 15.0f, textColor);
            float targetHealth = this.target.getHealth();
            float targetAbsorptionAmount = this.target.getAbsorptionAmount();
            String healthText = String.valueOf((int)Math.ceil(targetHealth + targetAbsorptionAmount));
            FontLoaders.Tenacity16.drawStringWithShadow(healthText, x + healthWidth + 32.5f, y + y - 11.8f, textColor);
        }
        GL11.glPopMatrix();
    }

    @Override
    public String getTag() {
        return this.mode;
    }
}

