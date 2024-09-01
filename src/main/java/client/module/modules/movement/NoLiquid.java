/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.movement;

import client.module.Module;
import client.module.ModuleType;
import net.minecraft.block.BlockLiquid;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoLiquid
extends Module {
    public static NoLiquid INSTANCE = new NoLiquid();

    protected NoLiquid() {
        super("NoLiquid", 0, false, ModuleType.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        NoLiquid.mc.thePlayer.inWater = false;
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j < 2; ++j) {
                for (int k = -2; k < 2; ++k) {
                    BlockPos pos = NoLiquid.mc.thePlayer.getPosition().add(i, j, k);
                    if (NoLiquid.mc.theWorld.getBlockState(pos) == null || !(NoLiquid.mc.theWorld.getBlockState(pos).getBlock() instanceof BlockLiquid)) continue;
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                }
            }
        }
    }
}

