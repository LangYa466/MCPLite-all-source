/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.visual;

import client.event.events.PacketSendEvent;
import client.event.events.Render2DEvent;
import client.module.Module;
import client.module.ModuleType;
import client.ui.element.Element;
import client.ui.element.ElementManager;
import client.ui.font.FontLoaders;
import client.utils.RenderUtils;
import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class SessionHUD
extends Module {
    EntityLivingBase target;
    private float aniStep = 0.0f;
    private int kills;
    private long time;
    float x;
    float y;

    public SessionHUD() {
        super("SessionHUD", 0, ModuleType.VISUAL);
        this.resetTimer();
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);
        Element element = ElementManager.SessionHUD;
        float width = 0.0f;
        float height = 0.0f;
        this.x = (int)((float)sr.getScaledWidth() / 2.0f - width / 2.0f);
        this.y = (int)((float)sr.getScaledHeight() / 3.0f);
        element.posY = this.y = (float)((int)(this.y + element.moveY));
        element.posX = this.x = (float)((int)(this.x + element.moveX));
        element.width = width;
        element.height = height;
        Color color = new Color(20, 20, 20, (int)(100.0f * Math.max(Math.min(this.aniStep, 1.0f), 0.01f)));
        float theHeight = Math.min(height * (this.aniStep / 0.2f), 30.0f);
        float theWidth = Math.max(theHeight, width * this.aniStep);
        this.x = 0.0f;
        this.y = 0.0f;
        RenderUtils.drawShadow(this.x + 2.0f, this.y + 100.0f, width + 148.0f, height + 60.0f);
        RenderUtils.drawShadow(this.x + 8.0f, this.y + 120.0f, width + 36.0f, height + 36.0f);
        RenderUtils.drawRect(this.x + 2.0f, this.y + 100.0f, width + 150.0f, height + 160.0f, new Color(0, 0, 0, 80));
        RenderUtils.drawPlayerHead(SessionHUD.mc.thePlayer.getLocationSkin(), (int)this.x + 8, (int)this.y + 120, 36, 36);
        FontLoaders.Baloo24.drawStringWithShadow(SessionHUD.mc.thePlayer.getName(), this.x + 50.0f, this.y + 122.0f, Color.WHITE.getRGB());
        FontLoaders.Tenacity16.drawStringWithShadow(this.getTime() + " ", this.x + 54.0f, this.y + 137.0f, Color.GRAY.getRGB());
        FontLoaders.Tenacity16.drawStringWithShadow("Kills: " + this.kills, this.x + 54.0f, this.y + 147.0f, Color.GRAY.getRGB());
        FontLoaders.XylitolICON.drawString("s", this.x + 7.0f, this.y + 106.0f, Color.pink.getRGB(), true);
        FontLoaders.Avergent24.drawString("Session", this.x + 20.0f, this.y + 106.0f, Color.pink.getRGB(), true);
        SessionHUD.drawLine(this.x + 2.0f, this.y + 105.0f, 2.0, 10.0, new Color(139, 9, 243, 255));
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity)event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
            Entity entity = ((C02PacketUseEntity)event.getPacket()).getEntityFromWorld(SessionHUD.mc.theWorld);
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(150L);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (entity instanceof EntityLivingBase && (((EntityLivingBase)entity).getHealth() == 0.0f || entity.isDead || entity == null)) {
                    ++this.kills;
                }
            });
            thread.start();
        }
    }

    @Override
    public void onDisable() {
        this.kills = 0;
    }

    private void resetTimer() {
        this.time = System.currentTimeMillis();
    }

    private String getTime() {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - this.time;
        int seconds = (int)(elapsed / 1000L) % 60;
        int minutes = (int)(elapsed / 60000L) % 60;
        return String.format("%02dm %02ds", minutes, seconds);
    }

    public static void drawLine(double x, double y, double width, double height, Color color) {
        Gui.drawRect((int)x, (int)y, (int)(x + width), (int)(y + height), color.getRGB());
    }
}

