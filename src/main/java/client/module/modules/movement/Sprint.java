/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.movement;

import client.module.Module;
import client.module.ModuleType;
import client.utils.rotation.RotationSetter;

public class Sprint
extends Module {
    public Sprint() {
        super("Sprint", 0, true, ModuleType.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (Sprint.mc.gameSettings.keyBindForward.isKeyDown()) {
            Sprint.mc.gameSettings.keyBindSprint.pressed = true;
        }
        if (RotationSetter.targetRotation != null) {
            // empty if block
        }
    }
}

