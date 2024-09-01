/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.render;

import client.event.events.Render2DEvent;
import client.event.events.Render3DEventAfterHand;
import client.event.events.Render3DEventBeforeHand;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.RenderUtils;
import client.utils.WorldToScreen;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class ESP
extends Module {
    @Settings(list={"2D", "Box"})
    private String mode = "2D";
    @Settings
    private boolean test = false;

    public ESP() {
        super("ESP", 0, true, ModuleType.RENDER);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
    }

    @Override
    public void onRender3D(Render3DEventBeforeHand event) {
        if (this.test) {
            return;
        }
        if (this.mode.equalsIgnoreCase("2d")) {
            Matrix4f mvMatrix = WorldToScreen.getMatrix(2982);
            Matrix4f projectionMatrix = WorldToScreen.getMatrix(2983);
            GL11.glPushAttrib(8192);
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glMatrixMode(5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, ESP.mc.displayWidth, ESP.mc.displayHeight, 0.0, -1.0, 1.0);
            GL11.glMatrixMode(5888);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glDisable(2929);
            GL11.glBlendFunc(770, 771);
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
            GL11.glLineWidth(1.0f);
            for (Entity entityLiving : ESP.mc.theWorld.loadedEntityList) {
                if (entityLiving.getEntityId() == ESP.mc.thePlayer.getEntityId()) continue;
                RenderManager renderManager = mc.getRenderManager();
                Timer timer = ESP.mc.timer;
                AxisAlignedBB bb = entityLiving.getEntityBoundingBox().offset(-entityLiving.posX, -entityLiving.posY, -entityLiving.posZ).offset(entityLiving.lastTickPosX + (entityLiving.posX - entityLiving.lastTickPosX) * (double)timer.renderPartialTicks, entityLiving.lastTickPosY + (entityLiving.posY - entityLiving.lastTickPosY) * (double)timer.renderPartialTicks, entityLiving.lastTickPosZ + (entityLiving.posZ - entityLiving.lastTickPosZ) * (double)timer.renderPartialTicks).offset(-renderManager.getRenderPosX(), -renderManager.getRenderPosY(), -renderManager.getRenderPosZ());
                double[][] boxVertices = new double[][]{{bb.minX, bb.minY, bb.minZ}, {bb.minX, bb.maxY, bb.minZ}, {bb.maxX, bb.maxY, bb.minZ}, {bb.maxX, bb.minY, bb.minZ}, {bb.minX, bb.minY, bb.maxZ}, {bb.minX, bb.maxY, bb.maxZ}, {bb.maxX, bb.maxY, bb.maxZ}, {bb.maxX, bb.minY, bb.maxZ}};
                float minX = Float.MAX_VALUE;
                float minY = Float.MAX_VALUE;
                float maxX = -1.0f;
                float maxY = -1.0f;
                for (double[] boxVertex : boxVertices) {
                    Vector2f screenPos = WorldToScreen.worldToScreen(new Vector3f((float)boxVertex[0], (float)boxVertex[1], (float)boxVertex[2]), mvMatrix, projectionMatrix, ESP.mc.displayWidth, ESP.mc.displayHeight);
                    if (screenPos == null) continue;
                    minX = Math.min(screenPos.x, minX);
                    minY = Math.min(screenPos.y, minY);
                    maxX = Math.max(screenPos.x, maxX);
                    maxY = Math.max(screenPos.y, maxY);
                }
                if (!(minX > 0.0f || minY > 0.0f || maxX <= (float)ESP.mc.displayWidth) && !(maxY <= (float)ESP.mc.displayWidth)) continue;
                Color color = Color.WHITE;
                GL11.glColor4f((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, 1.0f);
                GL11.glBegin(2);
                GL11.glVertex2f(minX, minY);
                GL11.glVertex2f(minX, maxY);
                GL11.glVertex2f(maxX, maxY);
                GL11.glVertex2f(maxX, minY);
                GL11.glEnd();
            }
            GL11.glEnable(2929);
            GL11.glMatrixMode(5889);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
        if (this.mode.equalsIgnoreCase("box")) {
            for (Entity entityLiving : ESP.mc.theWorld.loadedEntityList) {
                Color color = Color.WHITE;
                if (entityLiving.getEntityId() == ESP.mc.thePlayer.getEntityId()) continue;
                if (entityLiving instanceof EntityLivingBase && ((EntityLivingBase)entityLiving).hurtTime > 0) {
                    color = Color.red;
                }
                RenderUtils.drawEntityBox(entityLiving, color, 30);
            }
        }
    }

    @Override
    public void onRender3D(Render3DEventAfterHand event) {
        if (!this.test) {
            return;
        }
        if (this.mode.equalsIgnoreCase("2d")) {
            Matrix4f mvMatrix = WorldToScreen.getMatrix(2982);
            Matrix4f projectionMatrix = WorldToScreen.getMatrix(2983);
            GL11.glPushAttrib(8192);
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glMatrixMode(5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, ESP.mc.displayWidth, ESP.mc.displayHeight, 0.0, -1.0, 1.0);
            GL11.glMatrixMode(5888);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glDisable(2929);
            GL11.glBlendFunc(770, 771);
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
            GL11.glLineWidth(1.0f);
            for (Entity entityLiving : ESP.mc.theWorld.loadedEntityList) {
                if (entityLiving.getEntityId() == ESP.mc.thePlayer.getEntityId()) continue;
                RenderManager renderManager = mc.getRenderManager();
                Timer timer = ESP.mc.timer;
                AxisAlignedBB bb = entityLiving.getEntityBoundingBox().offset(-entityLiving.posX, -entityLiving.posY, -entityLiving.posZ).offset(entityLiving.lastTickPosX + (entityLiving.posX - entityLiving.lastTickPosX) * (double)timer.renderPartialTicks, entityLiving.lastTickPosY + (entityLiving.posY - entityLiving.lastTickPosY) * (double)timer.renderPartialTicks, entityLiving.lastTickPosZ + (entityLiving.posZ - entityLiving.lastTickPosZ) * (double)timer.renderPartialTicks).offset(-renderManager.getRenderPosX(), -renderManager.getRenderPosY(), -renderManager.getRenderPosZ());
                double[][] boxVertices = new double[][]{{bb.minX, bb.minY, bb.minZ}, {bb.minX, bb.maxY, bb.minZ}, {bb.maxX, bb.maxY, bb.minZ}, {bb.maxX, bb.minY, bb.minZ}, {bb.minX, bb.minY, bb.maxZ}, {bb.minX, bb.maxY, bb.maxZ}, {bb.maxX, bb.maxY, bb.maxZ}, {bb.maxX, bb.minY, bb.maxZ}};
                float minX = Float.MAX_VALUE;
                float minY = Float.MAX_VALUE;
                float maxX = -1.0f;
                float maxY = -1.0f;
                for (double[] boxVertex : boxVertices) {
                    Vector2f screenPos = WorldToScreen.worldToScreen(new Vector3f((float)boxVertex[0], (float)boxVertex[1], (float)boxVertex[2]), mvMatrix, projectionMatrix, ESP.mc.displayWidth, ESP.mc.displayHeight);
                    if (screenPos == null) continue;
                    minX = Math.min(screenPos.x, minX);
                    minY = Math.min(screenPos.y, minY);
                    maxX = Math.max(screenPos.x, maxX);
                    maxY = Math.max(screenPos.y, maxY);
                }
                if (!(minX > 0.0f || minY > 0.0f || maxX <= (float)ESP.mc.displayWidth) && !(maxY <= (float)ESP.mc.displayWidth)) continue;
                Color color = Color.WHITE;
                GL11.glColor4f((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, 1.0f);
                GL11.glBegin(2);
                GL11.glVertex2f(minX, minY);
                GL11.glVertex2f(minX, maxY);
                GL11.glVertex2f(maxX, maxY);
                GL11.glVertex2f(maxX, minY);
                GL11.glEnd();
            }
            GL11.glEnable(2929);
            GL11.glMatrixMode(5889);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
        if (this.mode.equalsIgnoreCase("box")) {
            for (Entity entityLiving : ESP.mc.theWorld.loadedEntityList) {
                Color color = Color.WHITE;
                if (entityLiving.getEntityId() == ESP.mc.thePlayer.getEntityId()) continue;
                if (entityLiving instanceof EntityLivingBase && ((EntityLivingBase)entityLiving).hurtTime > 0) {
                    color = Color.red;
                }
                RenderUtils.drawEntityBox(entityLiving, color, 30);
            }
        }
    }

    @Override
    public String getTag() {
        return this.mode;
    }
}

