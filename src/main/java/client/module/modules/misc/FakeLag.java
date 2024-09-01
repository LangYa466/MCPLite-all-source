/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.misc;

import client.event.events.PacketReceiveAsyncEvent;
import client.event.events.PacketReceiveSyncEvent;
import client.event.events.PacketSendEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.FakeLagUtils;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class FakeLag
extends Module {
    @Settings
    private boolean onlyVelocity = false;

    public FakeLag() {
        super("FakeLag", 0, ModuleType.MISC);
    }

    @Override
    public void onEnable() {
        FakeLagUtils.startFakeLag();
        FakeLagUtils.setCancelAction(S32PacketConfirmTransaction.class, packet -> mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(0, 0, false)));
        FakeLagUtils.lagOthersMovingPackets = !this.onlyVelocity;
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
    }

    @Override
    public void onPacketReceiveAsync(PacketReceiveAsyncEvent event) {
    }

    @Override
    public void onPostUpdate() {
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
    }

    @Override
    public void onDisable() {
        FakeLagUtils.stopFakeLag();
    }

    @Override
    public String getTag() {
        return this.onlyVelocity ? "OnlyVelocityPackets" : "AllServerPackets";
    }
}

