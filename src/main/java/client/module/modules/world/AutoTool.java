/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.world;

import client.Client;
import client.event.events.PacketSendEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.modules.world.Scaffold;
import java.util.Comparator;
import java.util.List;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;

public class AutoTool
extends Module {
    private BlockPos digPos = null;
    private boolean switchTool = false;

    public AutoTool() {
        super("AutoTool", 0, false, ModuleType.WORLD);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)event.getPacket()).getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
            this.switchTool = true;
            this.digPos = ((C07PacketPlayerDigging)event.getPacket()).getPosition();
        }
    }

    @Override
    public void onUpdate() {
        if (this.switchTool) {
            Scaffold scaffold = (Scaffold)Client.moduleManager.moduleMap.get(Scaffold.class);
            if (!scaffold.getState()) {
                List<Slot> slotList = AutoTool.mc.thePlayer.inventoryContainer.inventorySlots.subList(36, 44);
                slotList.stream().max(Comparator.comparingDouble(slot -> slot.getHasStack() ? (double)slot.getStack().getItem().getStrVsBlock(slot.getStack(), AutoTool.mc.theWorld.getBlockState(this.digPos).getBlock()) : 1.0)).ifPresent(best -> {
                    if (best.getHasStack() && best.getStack().getItem().getStrVsBlock(best.getStack(), AutoTool.mc.theWorld.getBlockState(this.digPos).getBlock()) > 1.0f) {
                        AutoTool.mc.thePlayer.inventory.currentItem = best.slotNumber - 36;
                    }
                });
                AutoTool.mc.playerController.updateController();
            }
            this.switchTool = false;
        }
    }
}

