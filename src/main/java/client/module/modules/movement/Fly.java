/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.movement;

import client.module.Module;
import client.module.ModuleManager;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.misc.Disabler;
import client.utils.MovementUtils;
import net.minecraft.entity.player.EntityPlayer;

public class Fly
extends Module {
    private EntityPlayer thePlayer;
    @Settings(minValue=-3.0, maxValue=3.0)
    public float ySpeed = 1.0f;
    @Settings(minValue=0.1, maxValue=5.0)
    public float vanillaSpeed = 3.0f;
    @Settings
    public boolean isGrimFly = true;

    public Fly() {
        super("Fly", 33, ModuleType.MOVEMENT);
    }

    @Override
    public void onTick() {
        this.thePlayer = Fly.mc.thePlayer;
        Disabler disabler = (Disabler)ModuleManager.getModuleByClass(Disabler.class);
        if (this.isGrimFly && !disabler.startFly) {
            return;
        }
        this.thePlayer.capabilities.isFlying = false;
        this.thePlayer.motionY = 0.0;
        this.thePlayer.motionX = 0.0;
        this.thePlayer.motionZ = 0.0;
        float rotationYaw = this.thePlayer.rotationYaw;
        if (Fly.mc.gameSettings.keyBindJump.isKeyDown()) {
            this.thePlayer.motionY += (double)this.ySpeed;
        }
        if (Fly.mc.gameSettings.keyBindSneak.isKeyDown()) {
            this.thePlayer.motionY -= (double)this.ySpeed;
        }
        if (MovementUtils.isMoveKeybind()) {
            if (this.thePlayer.moveForward < 0.0f) {
                rotationYaw += 180.0f;
            }
            float forward = 1.0f;
            if (this.thePlayer.moveForward < 0.0f) {
                forward = -0.5f;
            } else if (this.thePlayer.moveForward > 0.0f) {
                forward = 0.5f;
            }
            if (this.thePlayer.moveStrafing > 0.0f) {
                rotationYaw -= 90.0f * forward;
            }
            if (this.thePlayer.moveStrafing < 0.0f) {
                rotationYaw += 90.0f * forward;
            }
            float yaw = (float)Math.toRadians(rotationYaw);
            this.thePlayer.motionX = -Math.sin(yaw) * (double)this.vanillaSpeed;
            this.thePlayer.motionZ = Math.cos(yaw) * (double)this.vanillaSpeed;
        }
    }

    @Override
    public void onDisable() {
        this.thePlayer = Fly.mc.thePlayer;
        Disabler disabler = (Disabler)ModuleManager.getModuleByClass(Disabler.class);
        if (this.isGrimFly && !disabler.startFly) {
            return;
        }
        this.thePlayer.motionX = 0.0;
        this.thePlayer.motionY = 0.0;
        this.thePlayer.motionZ = 0.0;
    }
}

