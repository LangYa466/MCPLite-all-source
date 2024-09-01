/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.combat;

import client.Client;
import client.event.events.PacketReceiveSyncEvent;
import client.module.Module;
import client.module.ModuleManager;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.misc.Gapple;
import client.utils.ClientUtils;
import client.utils.rotation.RotationSetter;
import client.utils.rotation.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.lwjgl.util.vector.Vector2f;

public class Velocity extends Module {
    @Settings
    private boolean debug = false;
    @Settings
    private boolean vanilla = false;
    @Settings
    private boolean vertical = false;
    @Settings
    private boolean verticalOnlyTargeting = false;
    @Settings
    private boolean grimC02 = false;
    @Settings(minValue=1.0, maxValue=5.0)
    private int c02s = 1;
    public boolean getKB = false;

    public Velocity() {
        super("Velocity", 44, true, ModuleType.COMBAT);
    }

    @Override
    public void onEnable() {
        this.getKB = false;
    }

    @Override
    public void onDisable() {
        this.getKB = false;
    }

    @Override
    public void onUpdate() {
        if (this.getKB) {
            Velocity.reduce();
            this.getKB = false;
        }
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity)event.getPacket()).getEntityID() == Velocity.mc.thePlayer.getEntityId()) {
            if (this.grimC02) {
                this.getKB = true;
            }
            if (this.vanilla) {
                event.cancelEvent();
            }
            if (this.vertical) {
                if (Velocity.mc.thePlayer.maxHurtTime <= 0 || Velocity.mc.thePlayer.hurtTime != Velocity.mc.thePlayer.maxHurtTime) {
                    return;
                }
                if (this.verticalOnlyTargeting && (Velocity.mc.objectMouseOver == null || Velocity.mc.objectMouseOver.entityHit == null)) {
                    return;
                }
                if (Velocity.mc.thePlayer.onGround || (double)((S12PacketEntityVelocity)event.getPacket()).getMotionY() / 8000.0 < 0.2 || (double)((S12PacketEntityVelocity)event.getPacket()).getMotionY() / 8000.0 > 0.41995) {
                    Velocity.mc.thePlayer.motionY = (double)((S12PacketEntityVelocity)event.getPacket()).getMotionY() / 8000.0;
                }
                event.cancelEvent();
            }
        }
    }

    public static void reduce() {
        Velocity velocity = (Velocity)ModuleManager.getModuleByClass(Velocity.class);
        if (!velocity.getState()) {
            return;
        }
        velocity.getKB = true;
        Vector2f vector2f = RotationSetter.targetRotation != null ? RotationSetter.targetRotation : new Vector2f(Velocity.mc.thePlayer.rotationYaw, Velocity.mc.thePlayer.rotationPitch);
        Entity pointEntity = RotationUtils.raycastEntity(3.0, Float.valueOf(vector2f.x), Float.valueOf(vector2f.y));
        if (pointEntity instanceof EntityLivingBase) {
            for (int i = 0; i < velocity.c02s; ++i) {
                Gapple gapple;
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(Velocity.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                Velocity.mc.thePlayer.setSprinting(true);
                Velocity.mc.thePlayer.serverSprintState = true;
                mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(pointEntity, C02PacketUseEntity.Action.ATTACK));
                Velocity.mc.thePlayer.swingItem();
                if (velocity.debug) {
                    ClientUtils.displayChatMessage("Reduce");
                }
                if ((gapple = (Gapple)Client.moduleManager.moduleMap.get(Gapple.class)).getState() && gapple.noC02 && Gapple.eating) {
                    return;
                }
                Velocity.mc.thePlayer.motionX *= 0.6;
                Velocity.mc.thePlayer.motionZ *= 0.6;
            }
        }
    }
}

