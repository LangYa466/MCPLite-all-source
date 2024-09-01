/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.visual;

import client.event.events.PacketReceiveSyncEvent;
import client.event.events.Render2DEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.visual.ClientColor;
import client.ui.element.Element;
import client.ui.element.ElementManager;
import client.ui.fastuni.FontLoader;
import client.ui.font.CFontRenderer;
import client.ui.font.FontLoaders;
import client.utils.RenderUtils;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class EffectRender
extends Module {
    private final Map<Integer, Integer> potionMap = new HashMap<Integer, Integer>();
    @Settings(list={"Text", "Normal", "SCP"})
    private String mode = "Text";

    public EffectRender() {
        super("PotionEffect", 0, ModuleType.VISUAL);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        Element element = ElementManager.potionEffect;
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        CFontRenderer fontRenderer = FontLoaders.GoogleSans20;
        int size = EffectRender.mc.thePlayer.getActivePotionEffects().size();
        element.posX = element.moveX;
        int x1 = (int)element.posX;
        element.posY = (float)(scaledResolution.getScaledHeight() / 2) + element.moveY;
        int y1 = (int)element.posY;
        GL11.glPushMatrix();
        GL11.glTranslated(x1, y1 - size * 20, 0.0);
        int x = 0;
        int y = 0;
        int i = 0;
        boolean width = false;
        DecimalFormat d = new DecimalFormat("###.0%");
        if (size > 0) {
            RenderUtils.drawShadow(x, y - 20 - 1, 130.0f, 20.0f);
            RenderUtils.drawRDRect(x, y - 20 - 1, 130.0, 20.0, new Color(15, 15, 15, 100).getRGB());
            if (this.mode.equalsIgnoreCase("normal")) {
                RenderUtils.drowColorString(FontLoaders.GoogleSans28, "Effects", x + 5, y - 17, 255);
                RenderUtils.drawRDRect(x, y - 20 - 1, 2.0, 20.0, ClientColor.INSTANCE.getMixColor().getRGB());
            }
            if (this.mode.equalsIgnoreCase("scp")) {
                RenderUtils.drawSCP(x + 10, y - 10, 0.3f);
                FontLoader.miFont28.drawString("OBJECTS", (float)(x + 20), y - 10, Color.white.getRGB());
            }
        }
        for (PotionEffect potioneffect : EffectRender.mc.thePlayer.getActivePotionEffects()) {
            if (!this.potionMap.containsKey(potioneffect.getPotionID()) && potioneffect.getDuration() > 20) {
                this.potionMap.put(potioneffect.getPotionID(), potioneffect.getDuration());
            }
            String text = potioneffect.getEffectName().replace("potion.", " ") + " " + this.IntegerToRoman(potioneffect.getAmplifier() + 1);
            text = text.substring(1, 2).toUpperCase() + text.substring(2);
            Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
            Color color = new Color(255, 255, 255);
            switch (this.mode) {
                case "Text": {
                    fontRenderer.drawString(text, x, y + i * fontRenderer.getHeight(), Color.white.getRGB());
                    element.width = fontRenderer.getStringWidth(potioneffect.getEffectName());
                    element.height = (i + 1) * fontRenderer.getHeight();
                    break;
                }
                case "Normal": {
                    int statusIconIndex;
                    RenderUtils.drawShadow(x, y + i * 20 - 1, 130.0f, 20.0f);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    RenderUtils.drawRDRect(x, y + i * 20 - 1, 130.0, 20.0, new Color(15, 15, 15, 100).getRGB());
                    RenderUtils.drawRDRect(x, y + i * 20 - 1, 130.0f * Math.min(1.0f, (float)potioneffect.getDuration() / (float)this.potionMap.get(potioneffect.getPotionID()).intValue()), 20.0, new Color(200, 200, 200, 100).getRGB());
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    if (potion.hasStatusIcon()) {
                        GlStateManager.pushMatrix();
                        GL11.glDisable(2929);
                        GL11.glEnable(3042);
                        GL11.glDepthMask(false);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        statusIconIndex = potion.getStatusIconIndex();
                        mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                        EffectRender.mc.ingameGUI.drawTexturedModalRect(x, y + i * 20, statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
                        GL11.glDepthMask(true);
                        GL11.glDisable(3042);
                        GL11.glEnable(2929);
                        GlStateManager.popMatrix();
                    }
                    FontLoaders.GoogleSans20.drawString(text, x + 20, y + i * 20 + 5, Color.WHITE.getRGB());
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    element.width = fontRenderer.getStringWidth(potioneffect.getEffectName());
                    element.height = (i + 1) * fontRenderer.getHeight();
                    break;
                }
                case "SCP": {
                    int statusIconIndex;
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    RenderUtils.drawShadow(x, y + i * 20 - 1, 130.0f, 20.0f);
                    RenderUtils.drawRDRect(x, y + i * 20 - 1, 130.0, 20.0, new Color(15, 15, 15, 100).getRGB());
                    RenderUtils.drawRDRect(x, y + i * 20 - 1, 130.0f * Math.min(1.0f, (float)potioneffect.getDuration() / (float)this.potionMap.get(potioneffect.getPotionID()).intValue()), 20.0, new Color(color.getRed(), color.getGreen(), color.getBlue(), 100).getRGB());
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    if (potion.hasStatusIcon()) {
                        GlStateManager.pushMatrix();
                        GL11.glDisable(2929);
                        GL11.glEnable(3042);
                        GL11.glDepthMask(false);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        statusIconIndex = potion.getStatusIconIndex();
                        mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                        EffectRender.mc.ingameGUI.drawTexturedModalRect(x, y + i * 20, statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
                        GL11.glDepthMask(true);
                        GL11.glDisable(3042);
                        GL11.glEnable(2929);
                        GlStateManager.popMatrix();
                    }
                    fontRenderer.drawString(text + " " + d.format((float)potioneffect.getDuration() / (float)this.potionMap.get(potioneffect.getPotionID()).intValue()), x + 20, y + i * 20 + 5, Color.white.getRGB());
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
            ++i;
        }
        element.width = 130.0f;
        element.height = (i + 1) * 20;
        element.posY -= element.height;
        GL11.glPopMatrix();
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        Packet<INetHandlerPlayClient> packetIn;
        PotionEffect potioneffect;
        if (event.getPacket() instanceof S1DPacketEntityEffect && !this.potionMap.containsKey((potioneffect = new PotionEffect(((S1DPacketEntityEffect)(packetIn = (S1DPacketEntityEffect)event.getPacket())).getEffectId(), ((S1DPacketEntityEffect)packetIn).getDuration(), ((S1DPacketEntityEffect)packetIn).getAmplifier(), false, ((S1DPacketEntityEffect)packetIn).func_179707_f())).getPotionID())) {
            this.potionMap.put(potioneffect.getPotionID(), potioneffect.getDuration());
        }
        if (event.getPacket() instanceof S1EPacketRemoveEntityEffect && ((S1EPacketRemoveEntityEffect)(packetIn = (S1EPacketRemoveEntityEffect)event.getPacket())).getEntityId() == EffectRender.mc.thePlayer.getEntityId()) {
            this.potionMap.remove(((S1EPacketRemoveEntityEffect)packetIn).getEffectId());
        }
    }

    private String IntegerToRoman(int DemoInteger) {
        int[] IntegerValues = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] RomanValues = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder RomanNumber = new StringBuilder();
        for (int x = 0; x < IntegerValues.length; ++x) {
            while (DemoInteger >= IntegerValues[x]) {
                DemoInteger -= IntegerValues[x];
                RomanNumber.append(RomanValues[x]);
            }
        }
        return RomanNumber.toString();
    }
}

