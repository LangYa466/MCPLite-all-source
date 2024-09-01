/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.client.renderer;

import client.Client;
import client.module.modules.combat.KillAura;
import client.module.modules.render.Animation;
import client.utils.MSTimer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.optifine.DynamicLights;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public class ItemRenderer {
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");
    private final Minecraft mc;
    private ItemStack itemToRender;
    private float equippedProgress;
    private float prevEquippedProgress;
    private final RenderManager renderManager;
    private final RenderItem itemRenderer;
    private int equippedItemSlot = -1;
    private float delay = 0.0f;
    private long lastUpdateTime = System.currentTimeMillis();
    private MSTimer rotateTimer = new MSTimer();

    public ItemRenderer(Minecraft mcIn) {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.itemRenderer = mcIn.getRenderItem();
    }

    public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform) {
        if (heldStack != null) {
            Item item = heldStack.getItem();
            Block block = Block.getBlockFromItem(item);
            GlStateManager.pushMatrix();
            if (this.itemRenderer.shouldRenderItemIn3D(heldStack)) {
                GlStateManager.scale(2.0f, 2.0f, 2.0f);
                if (!(!this.isBlockTranslucent(block) || Config.isShaders() && Shaders.renderItemKeepDepthMask)) {
                    GlStateManager.depthMask(false);
                }
            }
            this.itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);
            if (this.isBlockTranslucent(block)) {
                GlStateManager.depthMask(true);
            }
            GlStateManager.popMatrix();
        }
    }

    private boolean isBlockTranslucent(Block blockIn) {
        return blockIn != null && blockIn.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
    }

    private void rotateArroundXAndY(float angle, float angleY) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(angleY, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void setLightMapFromPlayer(AbstractClientPlayer clientPlayer) {
        int i = this.mc.theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + (double)clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);
        if (Config.isDynamicLights()) {
            i = DynamicLights.getCombinedLight(this.mc.getRenderViewEntity(), i);
        }
        float f = i & 0xFFFF;
        float f1 = i >> 16;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    }

    private void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks) {
        float f = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
        float f1 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
        GlStateManager.rotate((entityplayerspIn.rotationPitch - f) * 0.1f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate((entityplayerspIn.rotationYaw - f1) * 0.1f, 0.0f, 1.0f, 0.0f);
    }

    private float getMapAngleFromPitch(float pitch) {
        float f = 1.0f - pitch / 45.0f + 0.1f;
        f = MathHelper.clamp_float(f, 0.0f, 1.0f);
        f = -MathHelper.cos(f * (float)Math.PI) * 0.5f + 0.5f;
        return f;
    }

    private void renderRightArm(RenderPlayer renderPlayerIn) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(54.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(64.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(-62.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(0.25f, -0.85f, 0.75f);
        renderPlayerIn.renderRightArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderLeftArm(RenderPlayer renderPlayerIn) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(92.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(45.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(41.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(-0.3f, -1.1f, 0.45f);
        renderPlayerIn.renderLeftArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderPlayerArms(AbstractClientPlayer clientPlayer) {
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        Render render = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
        RenderPlayer renderplayer = (RenderPlayer)render;
        if (!clientPlayer.isInvisible()) {
            GlStateManager.disableCull();
            this.renderRightArm(renderplayer);
            this.renderLeftArm(renderplayer);
            GlStateManager.enableCull();
        }
    }

    private void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress) {
        float f = -0.4f * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        float f1 = 0.2f * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI * 2.0f);
        float f2 = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
        GlStateManager.translate(f, f1, f2);
        float f3 = this.getMapAngleFromPitch(pitch);
        GlStateManager.translate(0.0f, 0.04f, -0.72f);
        GlStateManager.translate(0.0f, equipmentProgress * -1.2f, 0.0f);
        GlStateManager.translate(0.0f, f3 * -0.5f, 0.0f);
        GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f3 * -85.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(0.0f, 1.0f, 0.0f, 0.0f);
        this.renderPlayerArms(clientPlayer);
        float f4 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f5 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f4 * -20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f5 * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(f5 * -80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(0.38f, 0.38f, 0.38f);
        GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(0.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(-1.0f, -1.0f, 0.0f);
        GlStateManager.scale(0.015625f, 0.015625f, 0.015625f);
        this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GL11.glNormal3f(0.0f, 0.0f, -1.0f);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-7.0, 135.0, 0.0).tex(0.0, 1.0).endVertex();
        worldrenderer.pos(135.0, 135.0, 0.0).tex(1.0, 1.0).endVertex();
        worldrenderer.pos(135.0, -7.0, 0.0).tex(1.0, 0.0).endVertex();
        worldrenderer.pos(-7.0, -7.0, 0.0).tex(0.0, 0.0).endVertex();
        tessellator.draw();
        MapData mapdata = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);
        if (mapdata != null) {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
        }
    }

    private void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress) {
        float f = -0.3f * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        float f1 = 0.4f * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI * 2.0f);
        float f2 = -0.4f * MathHelper.sin(swingProgress * (float)Math.PI);
        GlStateManager.translate(f, f1, f2);
        GlStateManager.translate(0.64000005f, -0.6f, -0.71999997f);
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float f3 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f4 * 70.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f3 * -20.0f, 0.0f, 0.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        GlStateManager.translate(-1.0f, 3.6f, 3.5f);
        GlStateManager.rotate(120.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(200.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        GlStateManager.translate(5.6f, 0.0f, 0.0f);
        Render render = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
        GlStateManager.disableCull();
        RenderPlayer renderplayer = (RenderPlayer)render;
        renderplayer.renderRightArm(this.mc.thePlayer);
        GlStateManager.enableCull();
    }

    private void doItemUsedTransformations(float swingProgress) {
        float f = -0.4f * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        float f1 = 0.2f * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI * 2.0f);
        float f2 = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
        GlStateManager.translate(f, f1, f2);
    }

    private void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks) {
        float f = (float)clientPlayer.getItemInUseCount() - partialTicks + 1.0f;
        float f1 = f / (float)this.itemToRender.getMaxItemUseDuration();
        float f2 = MathHelper.abs(MathHelper.cos(f / 4.0f * (float)Math.PI) * 0.1f);
        if (f1 >= 0.8f) {
            f2 = 0.0f;
        }
        GlStateManager.translate(0.0f, f2, 0.0f);
        float f3 = 1.0f - (float)Math.pow(f1, 27.0);
        GlStateManager.translate(f3 * 0.6f, f3 * -0.5f, f3 * 0.0f);
        GlStateManager.rotate(f3 * 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f3 * 10.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(f3 * 30.0f, 0.0f, 0.0f, 1.0f);
    }

    private void transformFirstPersonItem(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56f, -0.52f, -0.71999997f);
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f * -20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f1 * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(f1 * -80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(Animation.INSTANCE.scale, Animation.INSTANCE.scale, Animation.INSTANCE.scale);
    }

    private void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer) {
        GlStateManager.rotate(-18.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(-12.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-8.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(-0.9f, 0.2f, 0.0f);
        float f = (float)this.itemToRender.getMaxItemUseDuration() - ((float)clientPlayer.getItemInUseCount() - partialTicks + 1.0f);
        float f1 = f / 20.0f;
        f1 = (f1 * f1 + f1 * 2.0f) / 3.0f;
        if (f1 > 1.0f) {
            f1 = 1.0f;
        }
        if (f1 > 0.1f) {
            float f2 = MathHelper.sin((f - 0.1f) * 1.3f);
            float f3 = f1 - 0.1f;
            float f4 = f2 * f3;
            GlStateManager.translate(f4 * 0.0f, f4 * 0.01f, f4 * 0.0f);
        }
        GlStateManager.translate(f1 * 0.0f, f1 * 0.0f, f1 * 0.1f);
        GlStateManager.scale(1.0f, 1.0f, 1.0f + f1 * 0.2f);
    }

    private void doBlockTransformations() {
        GlStateManager.translate(-0.5f, 0.2f, 0.0f);
        GlStateManager.rotate(30.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(60.0f, 0.0f, 1.0f, 0.0f);
    }

    private void sigmaold(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56f, -0.52f, -0.71999997f);
        GlStateManager.translate(0.0f, p_178096_1_ * -0.6f, 0.0f);
        GlStateManager.rotate(25.0f, 0.0f, 1.0f, 0.0f);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * (float)Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * (float)Math.PI);
        GlStateManager.rotate(var3 * -15.0f, 0.0f, 1.0f, 0.2f);
        GlStateManager.rotate(var4 * -10.0f, 0.2f, 0.1f, 1.0f);
        GlStateManager.rotate(var4 * -30.0f, 1.3f, 0.1f, 0.2f);
        GlStateManager.scale(Animation.INSTANCE.scale, Animation.INSTANCE.scale, Animation.INSTANCE.scale);
    }

    private void tap1(float var2, float swingProgress) {
        GlStateManager.translate(0.56f, -0.52f, -0.71999997f);
        GlStateManager.translate(0.0f, var2 * -0.15f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((swingProgress * 0.8f - swingProgress * swingProgress * 0.8f) * -90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(0.37f, 0.37f, 0.37f);
    }

    private void tap2(float var2, float swing) {
        GlStateManager.translate(0.56f, -0.42f, -0.71999997f);
        GlStateManager.translate(0.0f, var2 * -0.15f, 0.0f);
        GlStateManager.rotate(30.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(MathHelper.sin(MathHelper.sqrt_float(swing) * (float)Math.PI) * -30.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(0.4f, 0.4f, 0.4f);
    }

    private void slideSwing(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56f, -0.52f, -0.72f);
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f * -0.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f1 * -0.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(f1 * -80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(0.4f, 0.4f, 0.4f);
    }

    private void avatar(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56f, -0.52f, -0.72f);
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f * -20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f1 * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(f1 * -40.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(0.4f, 0.4f, 0.4f);
    }

    private void smallPush(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56f, -0.52f, -0.72f);
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f * -10.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.rotate(f1 * -10.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.rotate(f1 * -10.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.scale(0.4f, 0.4f, 0.4f);
    }

    private void func_178096_b(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56f, -0.52f, -0.71999997f);
        GlStateManager.translate(0.0f, p_178096_1_ * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * (float)Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * (float)Math.PI);
        GlStateManager.rotate(var3 * -20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(var4 * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(var4 * -80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(Animation.INSTANCE.scale, Animation.INSTANCE.scale, Animation.INSTANCE.scale);
    }

    private void genCustom(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56f, -0.52f, -0.71999997f);
        GlStateManager.translate(0.0f, p_178096_1_ * -0.6f, 0.0f);
        GlStateManager.rotate(25.0f, 0.0f, 1.0f, 0.0f);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * (float)Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * (float)Math.PI);
        GlStateManager.rotate(var3 * -15.0f, 0.0f, 1.0f, 0.2f);
        GlStateManager.rotate(var4 * -10.0f, 0.2f, 0.1f, 1.0f);
        GlStateManager.rotate(var4 * -30.0f, 1.3f, 0.1f, 0.2f);
        GlStateManager.scale(Animation.INSTANCE.scale, Animation.INSTANCE.scale, Animation.INSTANCE.scale);
    }

    private void func_178103_d() {
        GlStateManager.translate(-0.5f, 0.2f, 0.0f);
        GlStateManager.rotate(30.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(60.0f, 0.0f, 1.0f, 0.0f);
    }

    private void shield(float var11, float var12) {
        GlStateManager.translate(0.56f, -0.52f, -0.71999997f);
        GlStateManager.rotate(48.57f, 0.0f, 0.24f, 0.14f);
        float var13 = MathHelper.sin(var12 * var12 * (float)Math.PI);
        float var14 = MathHelper.sin(MathHelper.sqrt_float(var12) * (float)Math.PI);
        GlStateManager.rotate(var13 * -35.0f, 0.0f, 0.0f, 0.0f);
        GlStateManager.rotate(var14 * 0.0f, 0.0f, 0.0f, 0.0f);
        GlStateManager.rotate(var14 * 20.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.scale(Animation.INSTANCE.scale, Animation.INSTANCE.scale, Animation.INSTANCE.scale);
    }

    private void func_178105_d(float p_178105_1_) {
        float f = -0.4f * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float)Math.PI);
        float f1 = 0.2f * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float)Math.PI * 2.0f);
        float f2 = -0.2f * MathHelper.sin(p_178105_1_ * (float)Math.PI);
        GlStateManager.translate(f, f1, f2);
    }

    public void renderItemInFirstPerson(float partialTicks) {
        if (!Config.isShaders() || !Shaders.isSkipRenderHand()) {
            float f = 1.0f - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
            EntityPlayerSP abstractclientplayer = this.mc.thePlayer;
            float f1 = abstractclientplayer.getSwingProgress(partialTicks);
            float f2 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
            float f3 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
            this.rotateArroundXAndY(f2, f3);
            this.setLightMapFromPlayer(abstractclientplayer);
            this.rotateWithPlayerRotations(abstractclientplayer, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();
            if (this.itemToRender != null) {
                if (Animation.INSTANCE.getState()) {
                    KillAura killAura = (KillAura)Client.moduleManager.moduleMap.get(KillAura.class);
                    if (this.mc.thePlayer.isBlocking() || killAura.isBlocking) {
                        GL11.glTranslated(Animation.INSTANCE.blockX, Animation.INSTANCE.blockY, Animation.INSTANCE.blockZ);
                    } else if (this.mc.thePlayer.isUsingItem() && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) {
                        GL11.glTranslated(Animation.INSTANCE.eatX, Animation.INSTANCE.eatY, Animation.INSTANCE.eatZ);
                    } else {
                        GL11.glTranslated(Animation.INSTANCE.x, Animation.INSTANCE.y, Animation.INSTANCE.z);
                    }
                }
                if (this.itemToRender.getItem() instanceof ItemMap) {
                    this.renderItemMap(abstractclientplayer, f2, f, f1);
                } else if (abstractclientplayer.getItemInUseCount() > 0) {
                    EnumAction enumaction = this.itemToRender.getItemUseAction();
                    block0 : switch (enumaction) {
                        case NONE: {
                            this.transformFirstPersonItem(f, 0.0f);
                            break;
                        }
                        case EAT: 
                        case DRINK: {
                            this.performDrinking(abstractclientplayer, partialTicks);
                            this.transformFirstPersonItem(f, f1);
                            break;
                        }
                        case BLOCK: {
                            switch (Animation.INSTANCE.mode) {
                                case "Vanilla": {
                                    this.transformFirstPersonItem(f, 0.0f);
                                    this.doBlockTransformations();
                                    break block0;
                                }
                                case "Exh": {
                                    this.func_178096_b(f, f1);
                                    this.doBlockTransformations();
                                    GL11.glTranslated(1.0, -0.1, 0.5);
                                    GL11.glTranslatef(-1.0f, this.mc.thePlayer.isSneaking() ? -0.2f : -0.3f, 0.1f);
                                    break block0;
                                }
                                case "1.7": {
                                    this.transformFirstPersonItem(f, f1);
                                    GlStateManager.translate(0.0, 0.3, 0.0);
                                    this.doBlockTransformations();
                                    break block0;
                                }
                                case "Flux": {
                                    this.transformFirstPersonItem(f / 2.0f, 0.0f);
                                    GlStateManager.rotate(-MathHelper.sin(f1 * f1 * (float)Math.PI) * 40.0f / 2.0f, MathHelper.sin(f1 * f1 * (float)Math.PI) / 2.0f, -0.0f, 9.0f);
                                    GlStateManager.rotate(-MathHelper.sin(f1 * f1 * (float)Math.PI) * 30.0f, 1.0f, MathHelper.sin(f1 * f1 * (float)Math.PI) / 2.0f, -0.0f);
                                    this.doBlockTransformations();
                                    GL11.glTranslatef(-0.05f, this.mc.thePlayer.isSneaking() ? -0.2f : 0.0f, 0.1f);
                                    break block0;
                                }
                                case "Remix": {
                                    this.genCustom(f, 0.83f);
                                    this.func_178103_d();
                                    float f4 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.83f);
                                    GlStateManager.translate(-0.5f, 0.2f, 0.2f);
                                    GlStateManager.rotate(-f4 * 0.0f, 0.0f, 0.0f, 0.0f);
                                    GlStateManager.rotate(-f4 * 43.0f, 58.0f, 23.0f, 45.0f);
                                    break block0;
                                }
                                case "Shield": {
                                    this.shield(0.0f, f1);
                                    this.func_178103_d();
                                    break block0;
                                }
                                case "Jello": {
                                    this.func_178096_b(0.0f, 0.0f);
                                    this.func_178103_d();
                                    int alpha = (int)Math.min(255L, (System.currentTimeMillis() % 255L > 127L ? Math.abs(Math.abs(System.currentTimeMillis()) % 255L - 255L) : System.currentTimeMillis() % 255L) * 2L);
                                    float f5 = (double)f1 > 0.5 ? 1.0f - f1 : f1;
                                    GlStateManager.translate(0.3f, -0.0f, 0.4f);
                                    GlStateManager.rotate(0.0f, 0.0f, 0.0f, 1.0f);
                                    GlStateManager.translate(0.0f, 0.5f, 0.0f);
                                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, -1.0f);
                                    GlStateManager.translate(0.6f, 0.5f, 0.0f);
                                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, -1.0f);
                                    GlStateManager.rotate(-10.0f, 1.0f, 0.0f, -1.0f);
                                    GlStateManager.rotate(-f5 * 10.0f, 10.0f, 10.0f, -9.0f);
                                    GlStateManager.rotate(10.0f, -1.0f, 0.0f, 0.0f);
                                    GlStateManager.translate(0.0, 0.0, -0.5);
                                    GlStateManager.rotate(this.mc.thePlayer.isSwingInProgress ? (float)(-alpha) / 5.0f : 1.0f, 1.0f, -0.0f, 1.0f);
                                    GlStateManager.translate(0.0, 0.0, 0.5);
                                    break block0;
                                }
                                case "SigmaOld": {
                                    float var15 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                                    this.sigmaold(f * 0.5f, 0.0f);
                                    GlStateManager.rotate(-var15 * 55.0f / 2.0f, -8.0f, -0.0f, 9.0f);
                                    GlStateManager.rotate(-var15 * 45.0f, 1.0f, var15 / 2.0f, -0.0f);
                                    this.func_178103_d();
                                    GL11.glTranslated(1.2, 0.3, 0.5);
                                    GL11.glTranslatef(-1.0f, this.mc.thePlayer.isSneaking() ? -0.1f : -0.2f, 0.2f);
                                    GlStateManager.scale(1.2f, 1.2f, 1.2f);
                                    break block0;
                                }
                                case "Astolfo": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * (float)Math.PI);
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 2.5f, 0.0f);
                                    }
                                    GlStateManager.rotate(-var9 * 58.0f / 2.0f, var9 / 2.0f, 1.0f, 0.5f);
                                    GlStateManager.rotate(-var9 * 43.0f, 1.0f, var9 / 3.0f, -0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Scale": {
                                    GL11.glTranslated(0.84, -0.77, -1.1);
                                    GlStateManager.translate(0.56f, -0.52f, -0.71999997f);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        GlStateManager.translate(0.0f, -0.0f, 0.0f);
                                    } else {
                                        GlStateManager.translate(0.0f, f / 0.8f * -0.8f, 0.0f);
                                    }
                                    GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
                                    float var3 = MathHelper.sin(f1 * f1 * (float)Math.PI);
                                    float var4 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                                    GlStateManager.rotate(var3 * -27.0f, 0.0f, 0.0f, 0.0f);
                                    GlStateManager.rotate(var4 * -27.0f, 0.0f, 0.0f, 0.0f);
                                    GlStateManager.rotate(var4 * -27.0f, 0.0f, 0.0f, 0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Leaked": {
                                    GL11.glTranslated(0.08, 0.02, 0.0);
                                    float var2 = MathHelper.sin((float)((double)MathHelper.sqrt_float(f1) * Math.PI));
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.4f, 0.0f);
                                    }
                                    this.doBlockTransformations();
                                    GlStateManager.rotate(-var2 * 41.0f, 1.1f, 0.8f, -0.3f);
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Tap1": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.tap1(0.0f, f1);
                                    } else {
                                        this.tap1(f, f1);
                                    }
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Tap2": {
                                    GL11.glTranslated(0.0, -0.1f, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.tap2(0.0f, f1);
                                    } else {
                                        this.tap2(f, f1);
                                    }
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "AstolfoSpin": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    GlStateManager.rotate(this.delay, 0.0f, 0.0f, -0.1f);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.5f, 0.0f);
                                    }
                                    long currentTime = System.currentTimeMillis();
                                    long elapsedTime = currentTime - this.lastUpdateTime;
                                    if (this.rotateTimer.hasPassed(1L)) {
                                        this.delay = (float)((double)this.delay + (double)elapsedTime * 360.0 / 850.0);
                                        this.rotateTimer.reset();
                                    }
                                    this.lastUpdateTime = currentTime;
                                    if (this.delay > 360.0f) {
                                        this.delay = 0.0f;
                                    }
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Astro": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, f1);
                                    } else {
                                        this.transformFirstPersonItem(f / 2.3f, f1);
                                    }
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                                    GlStateManager.rotate(var9 * 50.0f / 9.0f, -var9, -0.0f, 90.0f);
                                    GlStateManager.rotate(var9 * 50.0f, 200.0f, -var9 / 2.0f, -0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Hide": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.hideMode.equals("1_7")) {
                                        if (f1 != 0.0f) {
                                            GlStateManager.scale(0.85f, 0.85f, 0.85f);
                                            GlStateManager.translate(-0.06f, 0.003f, 0.05f);
                                        }
                                        this.doItemUsedTransformations(f1);
                                        if (Animation.INSTANCE.cancelEquip) {
                                            this.transformFirstPersonItem(0.0f, f1);
                                        } else {
                                            this.transformFirstPersonItem(f, f1);
                                        }
                                    }
                                    if (Animation.INSTANCE.hideMode.equals("1_8")) {
                                        this.doItemUsedTransformations(f1);
                                        if (Animation.INSTANCE.cancelEquip) {
                                            this.transformFirstPersonItem(0.0f, f1);
                                        } else {
                                            this.transformFirstPersonItem(f, f1);
                                        }
                                    }
                                    if (Animation.INSTANCE.hideMode.equals("Flux")) {
                                        if (Animation.INSTANCE.cancelEquip) {
                                            this.transformFirstPersonItem(0.0f, f1);
                                        } else {
                                            this.transformFirstPersonItem(f, f1);
                                        }
                                    }
                                    if (Animation.INSTANCE.hideMode.equals("Smooth")) {
                                        if (Animation.INSTANCE.cancelEquip) {
                                            this.transformFirstPersonItem(0.0f, f1);
                                        } else {
                                            this.transformFirstPersonItem(f, f1);
                                        }
                                        this.func_178105_d(f1);
                                    }
                                    if (Animation.INSTANCE.hideMode.equals("Dash")) {
                                        this.doItemUsedTransformations(f1);
                                        if (Animation.INSTANCE.cancelEquip) {
                                            this.transformFirstPersonItem(0.0f, 0.0f);
                                        } else {
                                            this.transformFirstPersonItem(f, 0.0f);
                                        }
                                        this.func_178105_d(f1);
                                    }
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Slash": {
                                    GL11.glTranslated(0.08, 0.08, 0.0);
                                    float var3 = MathHelper.sin((float)((double)MathHelper.sqrt_float(f1) * Math.PI));
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.4f, 0.0f);
                                    }
                                    this.doBlockTransformations();
                                    GlStateManager.rotate(-var3 * 70.0f, 5.0f, 13.0f, 50.0f);
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Reverse": {
                                    GL11.glTranslated(0.0, 0.1, -0.12);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, f1);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.2f, f1);
                                    }
                                    this.doBlockTransformations();
                                    GL11.glTranslated(0.08, -0.1, -0.3);
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Smooth": {
                                    GL11.glTranslated(0.14, -0.1, -0.24);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.4f, 0.0f);
                                    }
                                    float var91 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                                    this.doBlockTransformations();
                                    GlStateManager.translate(-0.36f, 0.25f, -0.06f);
                                    GlStateManager.rotate(-var91 * 35.0f, -8.0f, -0.0f, 9.0f);
                                    GlStateManager.rotate(-var91 * 70.0f, 1.0f, 0.4f, -0.0f);
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Rhys": {
                                    float f4 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                                    GL11.glTranslated(0.0, 0.19, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.4f, 0.0f);
                                    }
                                    GlStateManager.translate(0.41f, -0.25f, -0.5555557f);
                                    GlStateManager.translate(0.0f, 0.0f, 0.0f);
                                    GlStateManager.rotate(35.0f, 0.0f, 1.5f, 0.0f);
                                    float racism = MathHelper.sin(f1 * f1 / 64.0f * (float)Math.PI);
                                    GlStateManager.rotate(racism * -5.0f, 0.0f, 0.0f, 0.0f);
                                    GlStateManager.rotate(f4 * -12.0f, 0.0f, 0.0f, 1.0f);
                                    GlStateManager.rotate(f4 * -65.0f, 1.0f, 0.0f, 0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Stab": {
                                    GL11.glTranslated(-0.25, 0.45, 0.8);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.5f, 0.0f);
                                    }
                                    float spin = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                                    GlStateManager.translate((double)0.6f, (double)0.3f, (double)-0.6f + (double)(-spin) * 0.7);
                                    GlStateManager.rotate(6090.0f, 0.0f, 0.0f, 0.1f);
                                    GlStateManager.rotate(6085.0f, 0.0f, 0.1f, 0.0f);
                                    GlStateManager.rotate(6110.0f, 0.1f, 0.0f, 0.0f);
                                    this.transformFirstPersonItem(0.0f, 0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Winter": {
                                    GL11.glTranslated(0.0, -0.16, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, f1);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.5f, f1);
                                    }
                                    this.doBlockTransformations();
                                    GL11.glTranslatef(-0.35f, 0.1f, 0.0f);
                                    GL11.glTranslatef(-0.05f, -0.1f, 0.1f);
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Slide": {
                                    GL11.glTranslated(0.08, -0.11, -0.07);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.5f, 0.0f);
                                    }
                                    float var91 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                                    this.doBlockTransformations();
                                    GlStateManager.translate(-0.4f, 0.28f, 0.0f);
                                    GlStateManager.rotate(-var91 * 35.0f, -8.0f, -0.0f, 9.0f);
                                    GlStateManager.rotate(-var91 * 70.0f, 1.0f, -0.4f, -0.0f);
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Sigma4": {
                                    GL11.glTranslated(-0.6, -0.17, 0.11);
                                    float var4 = MathHelper.sin((float)((double)MathHelper.sqrt_float(f1) * Math.PI));
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.8f, 0.0f);
                                    }
                                    GlStateManager.rotate(-var4 * 55.0f / 2.0f, -8.0f, -0.0f, 9.0f);
                                    GlStateManager.rotate(-var4 * 45.0f, 1.0f, var4 / 2.0f, 0.0f);
                                    this.doBlockTransformations();
                                    GL11.glTranslated(-0.08, -1.25, 1.25);
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Old": {
                                    GL11.glTranslated(0.08, -0.14, -0.05);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, f1);
                                    } else {
                                        this.transformFirstPersonItem(f, f1);
                                    }
                                    this.doBlockTransformations();
                                    GlStateManager.translate(-0.35f, 0.2f, 0.0f);
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Jigsaw": {
                                    GL11.glTranslated(0.0, -0.18, -0.1);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, f1);
                                    } else {
                                        this.transformFirstPersonItem(f, f1);
                                    }
                                    this.doBlockTransformations();
                                    GlStateManager.translate(-0.5, 0.0, 0.0);
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Small": {
                                    GL11.glTranslated(-0.01, 0.03, -0.24);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, f1);
                                    } else {
                                        this.transformFirstPersonItem(f, f1);
                                    }
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Dash": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 2.4f, 0.0f);
                                    }
                                    GL11.glRotated(-var9 * 22.0f, var9 / 2.0f, 0.0, 9.0);
                                    GL11.glRotated(-var9 * 50.0f, 0.8f, var9 / 2.0f, 0.0);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Xiv": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    float var5 = MathHelper.sin((float)((double)MathHelper.sqrt_float(f1) * Math.PI));
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.5f, 0.0f);
                                    }
                                    this.doBlockTransformations();
                                    float var16 = MathHelper.sin((float)((double)(f1 * f1) * Math.PI));
                                    GlStateManager.rotate(-var16 * 20.0f, 0.0f, 1.0f, 0.0f);
                                    GlStateManager.rotate(-var5 * 20.0f, 0.0f, 0.0f, 1.0f);
                                    GlStateManager.rotate(-var5 * 80.0f, 1.0f, 0.0f, 0.0f);
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Swank": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, f1);
                                    } else {
                                        this.transformFirstPersonItem(f / 2.0f, f1);
                                    }
                                    GL11.glRotatef(var9 * 30.0f / 2.0f, -var9, -0.0f, 9.0f);
                                    GL11.glRotatef(var9 * 40.0f, 1.0f, -var9 / 2.0f, -0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Swonk": {
                                    GL11.glTranslated(0.0, 0.03, 0.0);
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.8f, 0.0f);
                                    }
                                    GL11.glRotated(-var9 * -30.0f / 2.0f, var9 / 2.0f, 1.0, 4.0);
                                    GL11.glRotated(-var9 * 7.5f, 1.0, var9 / 3.0f, -0.0);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "MoonPush": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.5f, 0.0f);
                                    }
                                    this.doBlockTransformations();
                                    float sin = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                                    GlStateManager.scale(1.0f, 1.0f, 1.0f);
                                    GlStateManager.translate(-0.2f, 0.45f, 0.25f);
                                    GlStateManager.rotate(-sin * 20.0f, -5.0f, -5.0f, 9.0f);
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Stella": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(-0.1f, f1);
                                    } else {
                                        this.transformFirstPersonItem(f - 0.08333333f, f1);
                                    }
                                    GlStateManager.translate(-0.5f, 0.3f, -0.2f);
                                    GlStateManager.rotate(32.0f, 0.0f, 1.0f, 0.0f);
                                    GlStateManager.rotate(-70.0f, 1.0f, 0.0f, 0.0f);
                                    GlStateManager.rotate(40.0f, 0.0f, 1.0f, 0.0f);
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Sigma3": {
                                    GL11.glTranslated(0.02, 0.02, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, f1);
                                    } else {
                                        this.transformFirstPersonItem(f / 2.0f, f1);
                                    }
                                    GL11.glTranslated(0.4, -0.06, -0.46);
                                    float Swang = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                                    GlStateManager.rotate(Swang * 25.0f / 2.0f, -Swang, -0.0f, 9.0f);
                                    GlStateManager.rotate(Swang * 15.0f, 1.0f, -Swang / 2.0f, -0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Push": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * (float)Math.PI);
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 2.5f, 0.0f);
                                    }
                                    GlStateManager.rotate(-var9 * 40.0f / 2.0f, var9 / 2.0f, 1.0f, 4.0f);
                                    GlStateManager.rotate(-var9 * 30.0f, 1.0f, var9 / 3.0f, -0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Yamato": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 2.5f, 0.0f);
                                    }
                                    this.doBlockTransformations();
                                    GL11.glRotatef(-var9 * 200.0f / 2.0f, -9.0f, 5.0f, 9.0f);
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Aqua": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * (float)Math.PI);
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 6.0f, 0.0f);
                                    }
                                    GlStateManager.rotate(-var9 * 17.0f / 2.0f, var9 / 2.0f, 1.0f, 4.0f);
                                    GlStateManager.rotate(-var9 * 6.0f, 1.0f, var9 / 3.0f, -0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Swang": {
                                    GL11.glTranslated(0.0, 0.03, 0.0);
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * (float)Math.PI);
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 2.5f, 0.0f);
                                    }
                                    GlStateManager.rotate(-var9 * 74.0f / 2.0f, var9 / 2.0f, 1.0f, 4.0f);
                                    GlStateManager.rotate(-var9 * 52.0f, 1.0f, var9 / 3.0f, -0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Moon": {
                                    GL11.glTranslated(-0.08, 0.12, 0.0);
                                    float var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * (float)Math.PI);
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.4f, 0.0f);
                                    }
                                    GlStateManager.rotate(-var9 * 65.0f / 2.0f, var9 / 2.0f, 1.0f, 4.0f);
                                    GlStateManager.rotate(-var9 * 60.0f, 1.0f, var9 / 3.0f, -0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "1_8": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f, 0.0f);
                                    }
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Swing": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, f1);
                                    } else {
                                        this.transformFirstPersonItem(f, f1);
                                    }
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "SlideSwing": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.slideSwing(0.0f, f1);
                                    } else {
                                        this.slideSwing(f, f1);
                                    }
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "SmallPush": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.smallPush(0.0f, f1);
                                    } else {
                                        this.smallPush(f / 1.8f, f1);
                                    }
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Avatar": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.avatar(0.0f, f1);
                                    } else {
                                        this.avatar(f / 2.5f, f1);
                                    }
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Float": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 2.0f, 0.0f);
                                    }
                                    GlStateManager.rotate(-MathHelper.sin(f1 * f1 * (float)Math.PI) * 40.0f / 2.0f, MathHelper.sin(f1 * f1 * (float)Math.PI) / 2.0f, -0.0f, 9.0f);
                                    GlStateManager.rotate(-MathHelper.sin(f1 * f1 * (float)Math.PI) * 30.0f, 1.0f, MathHelper.sin(f1 * f1 * (float)Math.PI) / 2.0f, -0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Invent": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    float table = MathHelper.sin((float)((double)MathHelper.sqrt_float(f1) * Math.PI));
                                    GlStateManager.rotate(-table * 30.0f, -8.0f, -0.2f, 9.0f);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, 0.0f);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.8f, 0.0f);
                                    }
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Fadeaway": {
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    float var6 = MathHelper.sin((float)((double)MathHelper.sqrt_float(f1) * Math.PI));
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, -0.3f);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.4f, -0.3f);
                                    }
                                    this.doBlockTransformations();
                                    float var16 = MathHelper.sin((float)((double)(f1 * f1) * Math.PI));
                                    GlStateManager.rotate(-var16 * 45.0f, 0.0f, 0.0f, 1.0f);
                                    GlStateManager.rotate(-var6 * 0.0f, 0.0f, 0.0f, 1.0f);
                                    GlStateManager.rotate(-var6 * 0.0f, 1.5f, 0.0f, 0.0f);
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                                case "Edit": {
                                    GL11.glTranslated(-0.04, 0.06, 0.0);
                                    if (Animation.INSTANCE.cancelEquip) {
                                        this.transformFirstPersonItem(0.0f, f1);
                                    } else {
                                        this.transformFirstPersonItem(f / 1.4f, f1);
                                    }
                                    GL11.glTranslated(0.0, 0.0, 0.0);
                                    float Swang = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                                    GlStateManager.rotate(Swang * 16.0f / 2.0f, -Swang, -0.0f, 2.0f);
                                    GlStateManager.rotate(Swang * 22.0f, 1.0f, -Swang / 3.0f, -0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.scale((double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7, (double)Animation.INSTANCE.scale + 0.7);
                                    break block0;
                                }
                            }
                            break;
                        }
                        case BOW: {
                            this.transformFirstPersonItem(f, 0.0f);
                            this.doBowTransformations(partialTicks, abstractclientplayer);
                        }
                    }
                } else {
                    this.doItemUsedTransformations(f1);
                    this.transformFirstPersonItem(f, f1);
                }
                this.renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
            } else if (!abstractclientplayer.isInvisible()) {
                if (Animation.INSTANCE.cancelEquip) {
                    this.renderPlayerArm(abstractclientplayer, 0.0f, f1);
                } else {
                    this.renderPlayerArm(abstractclientplayer, f, f1);
                }
            }
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
        }
    }

    public void renderOverlays(float partialTicks) {
        GlStateManager.disableAlpha();
        if (this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
            IBlockState iblockstate = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
            BlockPos blockpos = new BlockPos(this.mc.thePlayer);
            EntityPlayerSP entityplayer = this.mc.thePlayer;
            for (int i = 0; i < 8; ++i) {
                double d0 = entityplayer.posX + (double)(((float)((i >> 0) % 2) - 0.5f) * entityplayer.width * 0.8f);
                double d1 = entityplayer.posY + (double)(((float)((i >> 1) % 2) - 0.5f) * 0.1f);
                double d2 = entityplayer.posZ + (double)(((float)((i >> 2) % 2) - 0.5f) * entityplayer.width * 0.8f);
                BlockPos blockpos1 = new BlockPos(d0, d1 + (double)entityplayer.getEyeHeight(), d2);
                IBlockState iblockstate1 = this.mc.theWorld.getBlockState(blockpos1);
                if (!iblockstate1.getBlock().isVisuallyOpaque()) continue;
                iblockstate = iblockstate1;
                blockpos = blockpos1;
            }
            if (iblockstate.getBlock().getRenderType() != -1) {
                Object object = Reflector.getFieldValue(Reflector.RenderBlockOverlayEvent_OverlayType_BLOCK);
                if (!Reflector.callBoolean(Reflector.ForgeEventFactory_renderBlockOverlay, this.mc.thePlayer, Float.valueOf(partialTicks), object, iblockstate, blockpos)) {
                    this.renderBlockInHand(partialTicks, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(iblockstate));
                }
            }
        }
        if (!this.mc.thePlayer.isSpectator()) {
            if (this.mc.thePlayer.isInsideOfMaterial(Material.water) && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderWaterOverlay, this.mc.thePlayer, Float.valueOf(partialTicks))) {
                this.renderWaterOverlayTexture(partialTicks);
            }
            if (this.mc.thePlayer.isBurning() && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderFireOverlay, this.mc.thePlayer, Float.valueOf(partialTicks))) {
                this.renderFireInFirstPerson(partialTicks);
            }
        }
        GlStateManager.enableAlpha();
    }

    private void renderBlockInHand(float partialTicks, TextureAtlasSprite atlas) {
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        float f = 0.1f;
        GlStateManager.color(0.1f, 0.1f, 0.1f, 0.5f);
        GlStateManager.pushMatrix();
        float f1 = -1.0f;
        float f2 = 1.0f;
        float f3 = -1.0f;
        float f4 = 1.0f;
        float f5 = -0.5f;
        float f6 = atlas.getMinU();
        float f7 = atlas.getMaxU();
        float f8 = atlas.getMinV();
        float f9 = atlas.getMaxV();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-1.0, -1.0, -0.5).tex(f7, f9).endVertex();
        worldrenderer.pos(1.0, -1.0, -0.5).tex(f6, f9).endVertex();
        worldrenderer.pos(1.0, 1.0, -0.5).tex(f6, f8).endVertex();
        worldrenderer.pos(-1.0, 1.0, -0.5).tex(f7, f8).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderWaterOverlayTexture(float partialTicks) {
        if (!Config.isShaders() || Shaders.isUnderwaterOverlay()) {
            this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            float f = this.mc.thePlayer.getBrightness(partialTicks);
            GlStateManager.color(f, f, f, 0.5f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            float f1 = 4.0f;
            float f2 = -1.0f;
            float f3 = 1.0f;
            float f4 = -1.0f;
            float f5 = 1.0f;
            float f6 = -0.5f;
            float f7 = -this.mc.thePlayer.rotationYaw / 64.0f;
            float f8 = this.mc.thePlayer.rotationPitch / 64.0f;
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(-1.0, -1.0, -0.5).tex(4.0f + f7, 4.0f + f8).endVertex();
            worldrenderer.pos(1.0, -1.0, -0.5).tex(0.0f + f7, 4.0f + f8).endVertex();
            worldrenderer.pos(1.0, 1.0, -0.5).tex(0.0f + f7, 0.0f + f8).endVertex();
            worldrenderer.pos(-1.0, 1.0, -0.5).tex(4.0f + f7, 0.0f + f8).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableBlend();
        }
    }

    private void renderFireInFirstPerson(float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.9f);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        float f = 1.0f;
        for (int i = 0; i < 2; ++i) {
            GlStateManager.pushMatrix();
            TextureAtlasSprite textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float f1 = textureatlassprite.getMinU();
            float f2 = textureatlassprite.getMaxU();
            float f3 = textureatlassprite.getMinV();
            float f4 = textureatlassprite.getMaxV();
            float f5 = (0.0f - f) / 2.0f;
            float f6 = f5 + f;
            float f7 = 0.0f - f / 2.0f;
            float f8 = f7 + f;
            float f9 = -0.5f;
            GlStateManager.translate((float)(-(i * 2 - 1)) * 0.24f, -0.3f, 0.0f);
            GlStateManager.rotate((float)(i * 2 - 1) * 10.0f, 0.0f, 1.0f, 0.0f);
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.setSprite(textureatlassprite);
            worldrenderer.pos(f5, f7, f9).tex(f2, f4).endVertex();
            worldrenderer.pos(f6, f7, f9).tex(f1, f4).endVertex();
            worldrenderer.pos(f6, f8, f9).tex(f1, f3).endVertex();
            worldrenderer.pos(f5, f8, f9).tex(f2, f3).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }

    public void updateEquippedItem() {
        this.prevEquippedProgress = this.equippedProgress;
        EntityPlayerSP entityplayer = this.mc.thePlayer;
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        boolean flag = false;
        if (this.itemToRender != null && itemstack != null) {
            if (!this.itemToRender.getIsItemStackEqual(itemstack)) {
                boolean flag1;
                if (Reflector.ForgeItem_shouldCauseReequipAnimation.exists() && !(flag1 = Reflector.callBoolean(this.itemToRender.getItem(), Reflector.ForgeItem_shouldCauseReequipAnimation, this.itemToRender, itemstack, this.equippedItemSlot != entityplayer.inventory.currentItem))) {
                    this.itemToRender = itemstack;
                    this.equippedItemSlot = entityplayer.inventory.currentItem;
                    return;
                }
                flag = true;
            }
        } else {
            flag = this.itemToRender != null || itemstack != null;
        }
        float f2 = 0.4f;
        float f = flag ? 0.0f : 1.0f;
        float f1 = MathHelper.clamp_float(f - this.equippedProgress, -f2, f2);
        this.equippedProgress += f1;
        if (this.equippedProgress < 0.1f) {
            this.itemToRender = itemstack;
            this.equippedItemSlot = entityplayer.inventory.currentItem;
            if (Config.isShaders()) {
                Shaders.setItemToRenderMain(itemstack);
            }
        }
    }

    public void resetEquippedProgress() {
        this.equippedProgress = 0.0f;
    }

    public void resetEquippedProgress2() {
        this.equippedProgress = 0.0f;
    }
}

