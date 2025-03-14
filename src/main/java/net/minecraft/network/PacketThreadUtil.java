/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.network;

import client.Client;
import client.event.events.PacketReceiveSyncEvent;
import client.module.modules.misc.Disabler;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.src.Config;
import net.minecraft.util.IThreadListener;

public class PacketThreadUtil {
    public static int lastDimensionId = Integer.MIN_VALUE;

    public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> p_180031_0_, final T p_180031_1_, IThreadListener p_180031_2_) throws ThreadQuickExitException {
        if (!p_180031_2_.isCallingFromMinecraftThread()) {
            p_180031_2_.addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    if (!Disabler.INSTANCE.onSPacket(p_180031_0_, p_180031_1_)) {
                        return;
                    }
                    PacketReceiveSyncEvent packetReceiveSyncEvent = new PacketReceiveSyncEvent(p_180031_0_);
                    Client.eventManager.onPacketReceiveSync(packetReceiveSyncEvent);
                    if (packetReceiveSyncEvent.isCancelled()) {
                        return;
                    }
                    PacketThreadUtil.clientPreProcessPacket(packetReceiveSyncEvent.getPacket());
                    packetReceiveSyncEvent.getPacket().processPacket(p_180031_1_);
                }
            });
            throw ThreadQuickExitException.INSTANCE;
        }
        PacketThreadUtil.clientPreProcessPacket(p_180031_0_);
    }

    protected static void clientPreProcessPacket(Packet p_clientPreProcessPacket_0_) {
        if (p_clientPreProcessPacket_0_ instanceof S08PacketPlayerPosLook) {
            Config.getRenderGlobal().onPlayerPositionSet();
        }
        if (p_clientPreProcessPacket_0_ instanceof S07PacketRespawn) {
            S07PacketRespawn s07packetrespawn = (S07PacketRespawn)p_clientPreProcessPacket_0_;
            lastDimensionId = s07packetrespawn.getDimensionID();
        } else if (p_clientPreProcessPacket_0_ instanceof S01PacketJoinGame) {
            S01PacketJoinGame s01packetjoingame = (S01PacketJoinGame)p_clientPreProcessPacket_0_;
            lastDimensionId = s01packetjoingame.getDimension();
        } else {
            lastDimensionId = Integer.MIN_VALUE;
        }
    }
}

