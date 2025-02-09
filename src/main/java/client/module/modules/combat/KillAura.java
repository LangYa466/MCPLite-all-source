/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.combat;

import client.event.events.MotionEvent;
import client.event.events.PacketReceiveAsyncEvent;
import client.event.events.Render3DEventBeforeHand;
import client.module.Module;
import client.module.ModuleManager;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.combat.Velocity;
import client.module.modules.misc.AntiBot;
import client.module.modules.misc.Teams;
import client.module.modules.visual.ClientColor;
import client.utils.BlinkUtils;
import client.utils.ClientUtils;
import client.utils.PacketUtils;
import client.utils.RenderUtils;
import client.utils.rotation.RotationSetter;
import client.utils.rotation.RotationUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class KillAura
extends Module {
    @Settings(minValue=1.0, maxValue=8.0)
    public float range = 3.0f;
    @Settings(minValue=0.0, maxValue=10.0)
    public int attackDelay = 1;
    @Settings
    public boolean autoBlock = true;
    @Settings
    public boolean postAutoBlock = true;
    @Settings
    public boolean watchDogAutoBlock = false;
    @Settings(list={"One", "Switch"})
    private String targetMode = "One";
    @Settings
    public boolean attackAnimal = true;
    @Settings
    public boolean attackVillager = false;
    @Settings
    public boolean targetMessage = false;
    @Settings(maxValue=20.0)
    public int keepRotTicks = 0;
    @Settings(list={"None", "Vanilla", "Box", "Sigma"})
    public String targetMark = "Vanilla";
    @Settings(list={"Custom", "Client"})
    public String colorMode = "Custom";
    @Settings(maxValue=255.0)
    public int R = 255;
    @Settings(maxValue=255.0)
    public int G = 255;
    @Settings(maxValue=255.0)
    public int B = 255;
    @Settings(maxValue=255.0)
    public int HurtR = 255;
    @Settings(maxValue=255.0)
    public int HurtG = 0;
    @Settings(maxValue=255.0)
    public int HurtB = 0;
    @Settings(maxValue=255.0)
    public int alpha = 30;
    private List<Integer> targets = new ArrayList<Integer>();
    public EntityLivingBase target;
    public ArrayList<EntityLivingBase> renderTargets = new ArrayList();
    public boolean isBlocking = true;
    private int count = 0;
    private int watchDogBlockingTicks = 0;

    public KillAura() {
        super("KillAura", 19, ModuleType.COMBAT);
    }

    @Override
    public void onEnable() {
        this.count = 10;
    }

    @Override
    public void onDisable() {
        this.stopBlocking();
        this.target = null;
        this.watchDogBlockingTicks = 0;
    }

    @Override
    public void onTick() {
        if (ClientUtils.nullCheck()) {
            return;
        }
        this.search();
        if (KillAura.mc.thePlayer.ticksExisted % 40 == 0 && this.target != null && this.targetMessage) {
            ClientUtils.displayChatMessage("\u4f60\u6b63\u5728\u5bb3\u4eba\uff01 yourHealth: " + KillAura.mc.thePlayer.getHealth() + " " + this.target.getName() + "Health " + this.target.getHealth());
        }
        this.rotation();
    }

    @Override
    public void onUpdate() {
        Velocity velocity = (Velocity)ModuleManager.getModuleByClass(Velocity.class);
        if (velocity.getKB) {
            return;
        }
        if (this.renderTargets.isEmpty()) {
            this.stopBlocking();
        }
        this.attack();
    }

    @Override
    public void onPacketReceiveAsync(PacketReceiveAsyncEvent event) {
    }

    private void attack() {
        Vector2f vector2f = RotationSetter.targetRotation != null ? RotationSetter.targetRotation : new Vector2f(KillAura.mc.thePlayer.rotationYaw, KillAura.mc.thePlayer.rotationPitch);
        Entity pointEntity = RotationUtils.raycastEntity(3.0, Float.valueOf(vector2f.x), Float.valueOf(vector2f.y));
        if (KillAura.mc.thePlayer.isSpectator() || KillAura.mc.thePlayer.getHealth() == 0.0f || KillAura.mc.thePlayer.isDead) {
            this.stopBlocking();
            return;
        }
        if (this.watchDogAutoBlock && (this.watchDogBlockingTicks == 0 || this.watchDogBlockingTicks == 3)) {
            return;
        }
        if (this.count > this.attackDelay) {
            if (pointEntity instanceof EntityLivingBase) {
                if (Teams.isSameTeam(pointEntity)) {
                    return;
                }
                if (pointEntity instanceof EntityPlayer && ((EntityPlayer)pointEntity).isPlayerSleeping()) {
                    return;
                }
                if (pointEntity.getEntityId() == KillAura.mc.thePlayer.getEntityId()) {
                    return;
                }
                if (pointEntity instanceof EntityAnimal && !this.attackAnimal) {
                    return;
                }
                if (pointEntity instanceof EntityVillager && !this.attackVillager) {
                    return;
                }
                if (pointEntity.rotationPitch > 90.0f || pointEntity.rotationPitch < -90.0f) {
                    return;
                }
                if (this.targetMark.equalsIgnoreCase("vanilla")) {
                    ArrayList<Vector2f> points = new ArrayList<Vector2f>();
                    for (int i = 0; i < 360; i += 20) {
                        float x = (float)(pointEntity.posX - 0.5 * Math.sin(Math.PI * (double)(i - 90) / 180.0));
                        float y = (float)(pointEntity.posZ - 0.5 * Math.cos(Math.PI * (double)(i - 90) / 180.0));
                        points.add(new Vector2f(x, y));
                    }
                    for (Vector2f e : points) {
                        KillAura.mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), e.x, pointEntity.posY + 2.0, e.y, 0.0, -0.3, 0.0, new int[0]);
                    }
                }
                mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(pointEntity, C02PacketUseEntity.Action.ATTACK));
                KillAura.mc.thePlayer.swingItem();
            }
            this.count = 0;
        } else {
            ++this.count;
        }
        if (this.autoBlock && KillAura.mc.thePlayer.getHeldItem() != null && KillAura.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && !this.renderTargets.isEmpty()) {
            this.startBlocking();
        }
    }

    @Override
    public void onPostUpdate() {
        if (KillAura.mc.thePlayer.getHeldItem() != null && KillAura.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && this.postAutoBlock && !this.renderTargets.isEmpty()) {
            this.startBlocking();
        }
        this.target = null;
    }

    @Override
    public void onMotion(MotionEvent event) {
        if (this.watchDogAutoBlock && this.target != null) {
            if (this.watchDogBlockingTicks == 3) {
                this.watchDogBlockingTicks = 0;
                BlinkUtils.stopBlink();
            }
            if (this.watchDogBlockingTicks == 0) {
                if (event.isPre()) {
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange((KillAura.mc.thePlayer.inventory.currentItem + 1) % 9));
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(KillAura.mc.thePlayer.inventory.currentItem));
                } else {
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(KillAura.mc.thePlayer.getHeldItem()));
                }
                BlinkUtils.blink(new Class[0]);
            }
            ++this.watchDogBlockingTicks;
        }
    }

    private void rotation() {
        Vector2f vector2f;
        if (this.target != null && (vector2f = RotationUtils.searchCenter(this.target.getEntityBoundingBox(), true, true, this.range)) != null && this.target != null && !this.target.isDead && this.target.getHealth() != 0.0f) {
            RotationSetter.setRotation(vector2f, this.keepRotTicks);
        }
    }

    private void search() {
        ArrayList<EntityLivingBase> entityLivingBases = new ArrayList<EntityLivingBase>();
        for (Entity e : KillAura.mc.theWorld.loadedEntityList) {
            EntityLivingBase living;
            if (!(e instanceof EntityLivingBase) || AntiBot.INSTANCE.isBot(living = (EntityLivingBase)e) || e.getEntityId() == KillAura.mc.thePlayer.getEntityId() || e instanceof EntityAnimal && !this.attackAnimal || e instanceof EntityVillager && !this.attackVillager || Teams.isSameTeam(living) || e.isDead || living.getHealth() == 0.0f || !(KillAura.mc.thePlayer.getDistanceToEntityBox(living.getEntityBoundingBox()) <= (double)this.range)) continue;
            entityLivingBases.add(living);
            this.target = living;
        }
        if (this.target == null) {
            return;
        }
        if (this.targets.size() >= entityLivingBases.size()) {
            this.targets.clear();
        }
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        entityLivingBases.forEach(entityLivingBase -> {
            if (!this.targets.contains(entityLivingBase.getEntityId())) {
                if (atomicBoolean.get()) {
                    return;
                }
                this.target = entityLivingBase;
                atomicBoolean.set(true);
            }
        });
        if (!this.targets.contains(this.target.getEntityId())) {
            this.targets.add(this.target.getEntityId());
        }
    }

    private void startBlocking() {
        if (KillAura.mc.thePlayer.getHeldItem() != null && KillAura.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
            PacketWrapper use_0 = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
            use_0.write(Type.VAR_INT, 0);
            com.viaversion.viarewind.utils.PacketUtil.sendToServer(use_0, Protocol1_8To1_9.class, true, true);
            PacketWrapper use_1 = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
            use_1.write(Type.VAR_INT, 1);
            PacketUtil.sendToServer(use_1, Protocol1_8To1_9.class, true, true);
        }
    }

    @Override
    public void onRender3D(Render3DEventBeforeHand event) {
        this.renderTargets.clear();
        for (Entity entity : KillAura.mc.theWorld.loadedEntityList) {
            Color color;
            EntityLivingBase living;
            if (entity instanceof EntityLivingBase && (AntiBot.INSTANCE.isBot(living = (EntityLivingBase)entity) || living.getEntityId() == KillAura.mc.thePlayer.getEntityId() || living instanceof EntityAnimal && !this.attackAnimal || living instanceof EntityVillager && !this.attackVillager || Teams.isSameTeam(living) || entity.isDead || living.getHealth() == 0.0f) || !(KillAura.mc.thePlayer.getDistanceToEntityBox(entity.getEntityBoundingBox()) < 5.0) || !(entity instanceof EntityLivingBase) || entity.getEntityId() == KillAura.mc.thePlayer.getEntityId()) continue;
            EntityLivingBase renderTarget = (EntityLivingBase)entity;
            if (this.targetMark.equalsIgnoreCase("box")) {
                color = new Color(this.R, this.G, this.B);
                if (renderTarget.hurtTime > 0) {
                    color = new Color(this.HurtR, this.HurtG, this.HurtB);
                }
                if (this.colorMode.equalsIgnoreCase("Client")) {
                    color = ClientColor.INSTANCE.getMixColor();
                }
                double x = renderTarget.prevPosX + (renderTarget.posX - renderTarget.prevPosX) * (double)event.partialTicks - Minecraft.getMinecraft().getRenderManager().getRenderPosX();
                double y = renderTarget.prevPosY + (renderTarget.posY - renderTarget.prevPosY) * (double)event.partialTicks - Minecraft.getMinecraft().getRenderManager().getRenderPosY();
                double z = renderTarget.prevPosZ + (renderTarget.posZ - renderTarget.prevPosZ) * (double)event.partialTicks - Minecraft.getMinecraft().getRenderManager().getRenderPosZ();
                double xMoved = renderTarget.posX - renderTarget.prevPosX;
                double yMoved = renderTarget.posY - renderTarget.prevPosY;
                double zMoved = renderTarget.posZ - renderTarget.prevPosZ;
                double motionX = 0.0;
                double motionY = 0.0;
                double motionZ = 0.0;
                GL11.glPushMatrix();
                GlStateManager.translate(x + (xMoved + motionX), y + (yMoved + motionY), z + (zMoved + motionZ));
                RenderUtils.drawEntityBox(renderTarget, color, this.alpha);
                GL11.glPopMatrix();
            }
            if (this.targetMark.equalsIgnoreCase("sigma")) {
                color = new Color(this.R, this.G, this.B, this.alpha);
                if (this.colorMode.equalsIgnoreCase("Client")) {
                    color = ClientColor.INSTANCE.getMixColor();
                }
                RenderUtils.sigmaRing(renderTarget, color);
            }
            this.renderTargets.add((EntityLivingBase)entity);
        }
    }

    @Override
    public String getTag() {
        return this.targetMode;
    }

    private void stopBlocking() {
        if (this.isBlocking) {
            this.isBlocking = false;
            if (PacketUtils.packetBlocking) {
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
            this.watchDogBlockingTicks = 0;
            KillAura.mc.thePlayer.clearItemInUse();
        }
    }
}

