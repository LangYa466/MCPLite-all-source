/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.render;

import client.event.events.Render3DEventBeforeHand;
import client.module.Module;
import client.module.ModuleType;
import client.utils.rotation.RotationSetter;

public class PlayerServerRotations
extends Module {
    public PlayerServerRotations() {
        super("ServerRotation", 0, true, ModuleType.RENDER);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onPreUpdate() {
    }

    @Override
    public void onRender3D(Render3DEventBeforeHand event) {
        if (RotationSetter.lastPacketRotation != null) {
            float yaw = RotationSetter.lastPacketRotation.x;
            float yawOffset = 0.0f;
            PlayerServerRotations.mc.thePlayer.renderYawOffset = yaw - yawOffset;
            PlayerServerRotations.mc.thePlayer.rotationYawHead = yaw - yawOffset;
            PlayerServerRotations.mc.thePlayer.prevRenderYawOffset = yaw - yawOffset;
        }
    }
}

