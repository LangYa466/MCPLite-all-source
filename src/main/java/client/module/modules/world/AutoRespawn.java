/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.world;

import client.module.Module;
import client.module.ModuleType;
import client.utils.ClientUtils;

public class AutoRespawn
extends Module {
    public AutoRespawn() {
        super("AutoRespawn", 0, false, ModuleType.WORLD);
    }

    @Override
    public void onTick() {
        if (ClientUtils.nullCheck()) {
            return;
        }
        if (AutoRespawn.mc.thePlayer.getHealth() == 0.0f || AutoRespawn.mc.thePlayer.isDead) {
            AutoRespawn.mc.thePlayer.isDead = false;
            AutoRespawn.mc.thePlayer.respawnPlayer();
            mc.displayGuiScreen(null);
        }
    }
}

