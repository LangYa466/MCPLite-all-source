/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.world;

import client.Client;
import client.event.events.PacketReceiveAsyncEvent;
import client.event.events.PacketReceiveSyncEvent;
import client.event.events.PacketSendEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.combat.KillAura;
import client.utils.FakeLagUtils;
import client.utils.MSTimer;
import client.utils.MovementUtils;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class BalanceTimer
extends Module {
    @Settings(minValue=0.10000000149011612, maxValue=10.0)
    private float timerSpeed = 1.0f;
    private long currentTime = 0L;
    private long balanceTime = 0L;
    private MSTimer timer = new MSTimer();
    private boolean onGround = false;
    private boolean attack = false;
    private int c0fs = 0;

    public BalanceTimer() {
        super("BalanceTimer", 0, ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
        this.c0fs = 0;
        this.timer.reset();
        this.balanceTime = 0L;
        this.currentTime = System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        BalanceTimer.mc.timer.timerSpeed = 1.0f;
        FakeLagUtils.stopFakeLag();
    }

    @Override
    public void onTick() {
        KillAura killAura = (KillAura)Client.moduleManager.moduleMap.get(KillAura.class);
        if (killAura.target != null) {
            this.stopBalance();
            return;
        }
        if (BalanceTimer.mc.thePlayer == null || BalanceTimer.mc.thePlayer.isDead || BalanceTimer.mc.thePlayer.getHealth() == 0.0f) {
            this.setState(false);
            return;
        }
        if (this.timer.hasPassed(9000L)) {
            this.stopBalance();
            if (FakeLagUtils.packets.isEmpty()) {
                this.timer.reset();
            }
        } else if (!MovementUtils.isMoveKeybind() && !FakeLagUtils.lagging) {
            this.c0fs = 0;
            this.timer.reset();
            this.balanceTime = 0L;
            this.currentTime = System.currentTimeMillis();
            this.startBalance();
        }
        BalanceTimer.mc.timer.timerSpeed = this.balanceTime > 100L && MovementUtils.isMoveKeybind() ? this.timerSpeed : 1.0f;
    }

    @Override
    public void onPostUpdate() {
        if (this.balanceTime < 90L && this.c0fs > 20) {
            this.c0fs = 0;
            this.timer.reset();
            this.balanceTime = 0L;
            this.currentTime = System.currentTimeMillis();
            this.stopBalance();
        }
        if (this.attack) {
            if (FakeLagUtils.lagging) {
                this.stopBalance();
            }
            this.attack = false;
        }
    }

    @Override
    public void onPacketReceiveAsync(PacketReceiveAsyncEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            this.stopBalance();
        }
        if (event.getPacket() instanceof S32PacketConfirmTransaction) {
            mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(0, 0, false));
            ++this.c0fs;
        }
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
    }

    @Override
    public void onWorldLoad() {
        this.stopBalance();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = (C03PacketPlayer)event.getPacket();
            if (FakeLagUtils.lagging) {
                if (!packet.isMoving() && !packet.getRotating() && packet.onGround == this.onGround) {
                    event.cancelEvent();
                }
                if (!event.isCancelled()) {
                    this.balanceTime -= 50L;
                }
                this.balanceTime += System.currentTimeMillis() - this.currentTime;
                this.currentTime = System.currentTimeMillis();
            }
            this.onGround = packet.onGround;
        }
        if (event.getPacket() instanceof C02PacketUseEntity && !FakeLagUtils.packets.isEmpty()) {
            this.attack = true;
            event.cancelEvent();
        }
    }

    private void startBalance() {
        FakeLagUtils.startFakeLag();
        FakeLagUtils.lagOthersMovingPackets = false;
    }

    private void stopBalance() {
        BalanceTimer.mc.timer.timerSpeed = 1.0f;
        this.c0fs = 0;
        this.timer.reset();
        this.balanceTime = 0L;
        this.currentTime = System.currentTimeMillis();
        if (FakeLagUtils.lagging) {
            FakeLagUtils.stopFakeLag();
        }
    }

    @Override
    public String getTag() {
        return this.balanceTime + "";
    }
}

