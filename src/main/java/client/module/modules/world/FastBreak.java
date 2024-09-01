/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.world;

import client.event.events.PacketSendEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.ClientUtils;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class FastBreak
extends Module {
    private EnumFacing facing = null;
    private float damage = 0.0f;
    @Settings(minValue=1.0, maxValue=3.0)
    public float speed = 2.0f;
    private boolean boost = false;
    private BlockPos pos = null;
    private boolean myC07 = false;

    public FastBreak() {
        super("FastBreak", 0, true, ModuleType.WORLD);
    }

    @Override
    public void onPreUpdate() {
        if (ClientUtils.nullCheck()) {
            return;
        }
        FastBreak.mc.playerController.blockHitDelay = 0;
        if (FastBreak.mc.playerController.curBlockDamageMP > 1.0f / this.speed) {
            FastBreak.mc.playerController.curBlockDamageMP = 1.0f;
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
    }
}

