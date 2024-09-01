/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.misc;

import client.event.events.Render2DEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.BlinkUtils;
import client.utils.ClientUtils;
import client.utils.RenderUtils;
import client.utils.StencilUtil;
import client.utils.rotation.RotationSetter;
import client.utils.rotation.RotationUtils;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import org.lwjgl.util.vector.Vector2f;

public class Blink
        extends Module {
    @Settings
    private boolean autoSend = false;
    @Settings(minValue=1.0, maxValue=50.0, name="DelayTick")
    private int delay = 1;
    @Settings(minValue=1.0, maxValue=10.0, name="SendOnceTicks")
    private int onceSend = 1;
    @Settings(list={"NONE", "Normal", "Legit"})
    private String backTrack = "NONE";
    private boolean canStart = false;
    private boolean attack = false;
    private boolean tracking = false;
    private Vec3 currentPos = new Vec3(0.0, 0.0, 0.0);
    private int c03s = 0;
    private Map<C03PacketPlayer, EntityLivingBase> tracks = new HashMap<C03PacketPlayer, EntityLivingBase>();
    private LinkedBlockingQueue<EntityFX> entityFXES = new LinkedBlockingQueue();
    private EntityLivingBase currentTrack = null;

    public Blink() {
        super("Blink", 48, ModuleType.MISC);
    }

    @Override
    public void onEnable() {
        this.tracks.clear();
        Blink.mc.thePlayer.capabilities.allowEdit = true;
        this.c03s = 0;
        this.canStart = BlinkUtils.blink(new Class[0]);
        if (this.canStart) {
            BlinkUtils.setCancelAction(C03PacketPlayer.class, packet -> {
                Vector2f vector2f = RotationSetter.targetRotation != null ? RotationSetter.targetRotation : new Vector2f(Blink.mc.thePlayer.rotationYaw, Blink.mc.thePlayer.rotationPitch);
                Entity pointEntity = RotationUtils.raycastEntity(3.0, Float.valueOf(vector2f.x), Float.valueOf(vector2f.y));
                if (pointEntity instanceof EntityLivingBase) {
                    this.tracks.put((C03PacketPlayer)packet, (EntityLivingBase)pointEntity);
                } else {
                    this.tracks.put((C03PacketPlayer)packet, null);
                }
                ++this.c03s;
                EntityFX entityFX = Blink.mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), ((C03PacketPlayer)packet).x, ((C03PacketPlayer)packet).y, ((C03PacketPlayer)packet).z, 0.0, 0.0, 0.0, new int[0]);
                this.entityFXES.add(entityFX);
            });
            BlinkUtils.setCancelAction(C02PacketUseEntity.class, packet -> {
                if (((C02PacketUseEntity)packet).getAction() == C02PacketUseEntity.Action.ATTACK) {
                    this.attack = true;
                }
                if (this.backTrack.equalsIgnoreCase("normal")) {
                    C02PacketUseEntity c02PacketUseEntity = (C02PacketUseEntity)packet;
                    if (this.currentTrack != null && c02PacketUseEntity.getEntityFromWorld(Blink.mc.theWorld).getEntityId() == this.currentTrack.getEntityId()) {
                        BlinkUtils.sendPacket(c02PacketUseEntity, false);
                        BlinkUtils.sendPacket(new C0APacketAnimation(), false);
                    }
                }
            });
            BlinkUtils.setReleaseAction(C02PacketUseEntity.class, packet -> {
                if (((C02PacketUseEntity)packet).getAction() == C02PacketUseEntity.Action.ATTACK && this.backTrack.equalsIgnoreCase("legit")) {
                    this.attack = false;
                }
            });
            BlinkUtils.setReleaseAction(C03PacketPlayer.class, packet -> {
                C03PacketPlayer c03PacketPlayer = (C03PacketPlayer)packet;
                this.currentTrack = this.tracks.remove(c03PacketPlayer);
                this.currentPos = new Vec3(c03PacketPlayer.x, c03PacketPlayer.y, c03PacketPlayer.z);
                --this.c03s;
                if (!this.entityFXES.isEmpty()) {
                    Blink.mc.theWorld.removeEntity(this.entityFXES.poll());
                }
            });
        }
    }

    @Override
    public void onTick() {
        if (!ClientUtils.nullCheck() && (Blink.mc.thePlayer.getHealth() == 0.0f || ClientUtils.nullCheck() || Blink.mc.thePlayer.isDead)) {
            BlinkUtils.setReleaseReturnPredicateMap(Packet.class, packet -> packet instanceof C03PacketPlayer);
            this.setState(false);
            return;
        }
        if (this.autoSend) {
            if (Math.abs(this.c03s - (this.delay + this.onceSend)) > 4) {
                BlinkUtils.sendPacket(new C0FPacketConfirmTransaction(0, -327, false), false);
            }
            if (this.c03s > this.delay + this.onceSend) {
                boolean send = false;
                while (!BlinkUtils.packets.isEmpty() && this.c03s > this.delay) {
                    BlinkUtils.releasePacket(true);
                    send = true;
                }
                if (!send) {
                    // empty if block
                }
            }
            while (this.attack && this.backTrack.equalsIgnoreCase("legit") && !BlinkUtils.packets.isEmpty()) {
                BlinkUtils.releasePacket(true);
            }
            if (this.backTrack.equalsIgnoreCase("normal")) {
                C03PacketPlayer c03PacketPlayer = null;
                Entity entityLivingBase = null;
                int attacks = 0;
                for (Map.Entry<C03PacketPlayer, EntityLivingBase> e : this.tracks.entrySet()) {
                    Vec3 vec3 = new Vec3(e.getKey().x, e.getKey().y + (double)Blink.mc.thePlayer.getEyeHeight(), e.getKey().z);
                    if (e.getValue() == null) continue;
                    if (vec3.distanceTo(RotationUtils.getNearestPointBB(vec3, e.getValue().getEntityBoundingBox())) > 3.0) {
                        c03PacketPlayer = e.getKey();
                        break;
                    }
                    ++attacks;
                }
                for (Entity entity : Blink.mc.theWorld.loadedEntityList) {
                    if (!(entity instanceof EntityLivingBase) || entity.getEntityId() == Blink.mc.thePlayer.getEntityId() || !(this.currentPos.distanceTo(entity.getPositionEyes(1.0f)) < 4.0)) continue;
                    entityLivingBase = (EntityLivingBase)entity;
                }
                if (c03PacketPlayer != null) {
                    while (!BlinkUtils.packets.isEmpty() && this.tracks.containsKey(c03PacketPlayer)) {
                        BlinkUtils.releasePacket(true);
                    }
                } else if (entityLivingBase != null) {
                    while (this.currentPos.distanceTo(entityLivingBase.getPositionEyes(1.0f)) < 4.0 && !BlinkUtils.packets.isEmpty()) {
                        BlinkUtils.releasePacket(true);
                    }
                }
            }
            if (BlinkUtils.packets.isEmpty()) {
                this.attack = false;
            }
        } else {
            BlinkUtils.sendPacket(new C0FPacketConfirmTransaction(0, -327, false), false);
        }
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onDisable() {
        BlinkUtils.stopBlink();
    }

    @Override
    public String getTag() {
        return "DelayC03s: " + this.c03s;
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int x = scaledResolution.getScaledWidth() / 2;
        int y = scaledResolution.getScaledHeight() / 2;
        StencilUtil.initStencilToWrite();
        RenderUtils.drawRoundedRect(x - 50, y - 20, 100.0f, 5.0f, 5.0f, 0, 1.0f, 0);
        StencilUtil.readStencilBuffer(1);
        RenderUtils.drawJDT(x - 50, y - 20, 100, 5, (float)this.c03s / (float)this.delay, true, new Color(0, 0, 0, 100));
        StencilUtil.uninitStencilBuffer();
    }
}

