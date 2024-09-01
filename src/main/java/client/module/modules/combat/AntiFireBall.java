/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.combat;

import client.module.Module;
import client.module.ModuleType;
import client.utils.ClientUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class AntiFireBall
extends Module {
    public AntiFireBall() {
        super("AntiFireBall", 0, true, ModuleType.COMBAT);
    }

    @Override
    public void onTick() {
        if (ClientUtils.nullCheck()) {
            return;
        }
        for (Entity entity : AntiFireBall.mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityFireball) || !(entity.getDistanceToEntity(AntiFireBall.mc.thePlayer) <= 5.0f)) continue;
            mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
            AntiFireBall.mc.thePlayer.swingItem();
        }
    }
}

