/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.movement;

import client.event.events.MotionEvent;
import client.event.events.PacketReceiveSyncEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.ClientUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockWeb;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoWeb
extends Module {
    @Settings(list={"Vanilla", "Grim"})
    public String mode = "Vanilla";
    private boolean pass = true;
    private List<BlockPos> hasDug = new ArrayList<BlockPos>();

    public NoWeb() {
        super("NoWeb", 0, false, ModuleType.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (NoWeb.mc.thePlayer.isInWeb) {
            NoWeb.mc.thePlayer.isInWeb = false;
        }
    }

    @Override
    public void onWorldLoad() {
        this.hasDug.clear();
    }

    @Override
    public void onTick() {
        if (ClientUtils.nullCheck()) {
            return;
        }
        if (this.mode.equalsIgnoreCase("grim")) {
            for (int i = -2; i <= 2; ++i) {
                for (int j = -2; j < 2; ++j) {
                    for (int k = -2; k < 2; ++k) {
                        BlockPos pos = NoWeb.mc.thePlayer.getPosition().add(i, j, k);
                        if (NoWeb.mc.theWorld.getBlockState(pos) == null || !(NoWeb.mc.theWorld.getBlockState(pos).getBlock() instanceof BlockWeb) || this.hasDug.contains(pos)) continue;
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                        NoWeb.mc.theWorld.setBlockToAir(pos);
                        this.pass = true;
                    }
                }
            }
        }
    }

    @Override
    public void onMotion(MotionEvent event) {
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
    }
}

