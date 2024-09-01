/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.movement;

import client.event.events.JumpEvent;
import client.event.events.MotionEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.MovementUtils;
import client.utils.rotation.RotationSetter;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;

public class Speed
extends Module {
    @Settings(list={"Grim", "Hypixel", "Strafe"})
    private String mode = "Grim";
    @Settings(maxValue=20.0)
    private int grimAddHitBoxes = 0;
    @Settings
    private boolean follow = false;

    public Speed() {
        super("Speed", 0, false, ModuleType.MOVEMENT);
    }

    @Override
    public void onJump(JumpEvent jumpEvent) {
        if (this.mode.equalsIgnoreCase("Hypixel") && Speed.mc.thePlayer.onGround && !Speed.mc.thePlayer.inWater && MovementUtils.isMove()) {
            Speed.strafe(0.32);
        }
    }

    @Override
    public void onMotion(MotionEvent event) {
        if (this.mode.equalsIgnoreCase("Strafe")) {
            if (Speed.isMoving()) {
                if (Speed.mc.thePlayer.onGround) {
                    Speed.mc.thePlayer.jump();
                    Speed.mc.thePlayer.motionY -= (double)0.02f;
                    Speed.mc.thePlayer.speedInAir = 0.021f;
                    double moveSpeed = Speed.mc.thePlayer.moveForward != 0.0f ? (Speed.mc.thePlayer.moveStrafing != 0.0f ? 0.45 : 0.48) : (double)0.4f;
                    if (Speed.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        moveSpeed *= 1.0 + 0.2 * (double)(Speed.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
                    }
                    if (Speed.mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
                        moveSpeed *= (double)0.9f;
                    }
                    Speed.strafe(moveSpeed);
                }
                Speed.mc.thePlayer.motionY -= 2.0E-4;
            } else {
                Speed.mc.thePlayer.motionX *= 0.0;
                Speed.mc.thePlayer.motionZ *= 0.0;
            }
            Speed.strafe();
        }
    }

    public static boolean isMoving() {
        return Speed.mc.thePlayer.moveStrafing != 0.0f || Speed.mc.thePlayer.moveForward != 0.0f;
    }

    public static void strafe() {
        Speed.strafe(Speed.mc.thePlayer == null ? 0.0 : Math.sqrt(Speed.mc.thePlayer.motionX * Speed.mc.thePlayer.motionX + Speed.mc.thePlayer.motionZ * Speed.mc.thePlayer.motionZ));
    }

    public static void strafe(double speed) {
        double yaw = MovementUtils.getDirection();
        Speed.mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        Speed.mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    @Override
    public void onUpdate() {
        switch (this.mode) {
            case "Grim": {
                float rotationYaw;
                EntityPlayerSP player = Speed.mc.thePlayer;
                WorldClient world = Speed.mc.theWorld;
                if (!MovementUtils.isMove()) {
                    return;
                }
                int collisions = 0;
                AxisAlignedBB box = player.getEntityBoundingBox();
                for (Entity obj : world.loadedEntityList) {
                    if (obj == null) continue;
                    AxisAlignedBB entityBox = obj.getEntityBoundingBox().contract((double)this.grimAddHitBoxes / 10.0, 0.0, (double)this.grimAddHitBoxes / 10.0);
                    if (obj != Speed.mc.thePlayer && obj instanceof EntityLivingBase && box.intersectsWith(entityBox)) {
                        ++collisions;
                    }
                    if (!(obj instanceof EntityLivingBase) || obj == Speed.mc.thePlayer || !(Speed.mc.thePlayer.getDistanceToEntityBox(obj.getEntityBoundingBox()) <= 3.0) || !this.follow) continue;
                    RotationSetter.setFollow(true);
                }
                float f = rotationYaw = RotationSetter.targetRotation != null && this.follow ? RotationSetter.targetRotation.x : Speed.mc.thePlayer.rotationYaw;
                if (Speed.mc.thePlayer.moveForward < 0.0f) {
                    rotationYaw += 180.0f;
                }
                float forward = 1.0f;
                if (Speed.mc.thePlayer.moveForward < 0.0f) {
                    forward = -0.5f;
                } else if (Speed.mc.thePlayer.moveForward > 0.0f) {
                    forward = 0.5f;
                }
                if (Speed.mc.thePlayer.moveStrafing > 0.0f) {
                    rotationYaw -= 90.0f * forward;
                }
                if (Speed.mc.thePlayer.moveStrafing < 0.0f) {
                    rotationYaw += 90.0f * forward;
                }
                rotationYaw = (float)Math.toRadians(rotationYaw);
                if (collisions <= 0) break;
                float yaw = rotationYaw;
                double boost = 0.08 * (double)collisions;
                player.addVelocity(-Math.sin(yaw) * boost, 0.0, Math.cos(yaw) * boost);
                break;
            }
            case "hypixel": {
                if (!Speed.mc.thePlayer.onGround || Speed.mc.thePlayer.inWater || !MovementUtils.isMove()) break;
                Speed.mc.thePlayer.jump();
            }
        }
    }

    @Override
    public String getTag() {
        return this.mode;
    }
}

