/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.movement;

import client.event.events.PacketReceiveSyncEvent;
import client.event.events.PacketSendEvent;
import client.event.events.SlowDownEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class ChipsCatNoSlow
extends Module {
    @Settings(list={"Drop", "VLMode"})
    private String mode = "Drop";
    private boolean chipsCatWakeUp = false;
    private boolean chipsCatDropItem = false;
    private int ateTicks = 0;

    public ChipsCatNoSlow() {
        super("DropNoSlow", 0, ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
        this.chipsCatDropItem = false;
        this.ateTicks = 0;
        this.chipsCatWakeUp = false;
    }

    @Override
    public void onUpdate() {
        if (this.chipsCatWakeUp) {
            ++this.ateTicks;
            if (this.ateTicks > 36) {
                this.chipsCatDropItem = false;
                this.chipsCatWakeUp = false;
                this.ateTicks = 0;
                mc.thePlayer.stopUsingItem();
            }
            return;
        }
        if (mc.thePlayer.isUsingItem()) {
            switch (this.mode) {
                case "Drop": {
                    if (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) || mc.thePlayer.getHeldItem().stackSize <= 4) break;
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_17)) {
                        mc.thePlayer.swingItem();
                    }
                    this.chipsCatWakeUp = true;
                    break;
                }
                case "VLMode": {
                    if (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) || mc.thePlayer.getHeldItem().stackSize <= 4) break;
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), EnumFacing.EAST.getIndex(), null, 0.0f, 0.0f, 0.0f));
                    this.chipsCatWakeUp = true;
                }
            }
        }
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        if (event.getPacket() instanceof S2FPacketSetSlot && this.chipsCatWakeUp) {
            this.chipsCatDropItem = true;
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            if (this.chipsCatWakeUp) {
                // empty if block
            }
            if (this.chipsCatDropItem) {
                event.cancelEvent();
            }
        }
        if (event.getPacket() instanceof C07PacketPlayerDigging && this.chipsCatWakeUp && ((C07PacketPlayerDigging)event.getPacket()).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
            event.cancelEvent();
        }
    }

    @Override
    public void onSlowDown(SlowDownEvent event) {
        if (this.chipsCatDropItem) {
            event.setStrafe(1.0f);
            event.setForward(1.0f);
        }
    }
}

