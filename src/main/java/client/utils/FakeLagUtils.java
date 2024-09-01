/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.utils.MinecraftInstance;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class FakeLagUtils
extends MinecraftInstance {
    public static final LinkedBlockingQueue<Packet<INetHandlerPlayClient>> packets = new LinkedBlockingQueue();
    private static final Map<Class<?>, Predicate<Packet<INetHandlerPlayClient>>> cancelReturnPredicateMap = new HashMap();
    private static final Map<Class<?>, Consumer<Packet<INetHandlerPlayClient>>> cancelActionMap = new HashMap();
    private static final Map<Class<?>, Consumer<Packet<INetHandlerPlayClient>>> releaseActionMap = new HashMap();
    public static boolean lagOthersMovingPackets;
    public static boolean lagging;

    public static void setCancelAction(Class<?> clazz, Consumer<Packet<INetHandlerPlayClient>> packetConsumer) {
        boolean isIN = false;
        for (Class<?> classes : cancelActionMap.keySet()) {
            if (classes != clazz) continue;
            isIN = true;
            break;
        }
        if (isIN) {
            cancelActionMap.replace(clazz, packetConsumer);
        } else {
            cancelActionMap.put(clazz, packetConsumer);
        }
    }

    public static void setReleaseAction(Class<?> clazz, Consumer<Packet<INetHandlerPlayClient>> packetConsumer) {
        boolean isIN = false;
        for (Class<?> classes : releaseActionMap.keySet()) {
            if (classes != clazz) continue;
            isIN = true;
            break;
        }
        if (isIN) {
            releaseActionMap.replace(clazz, packetConsumer);
        } else {
            releaseActionMap.put(clazz, packetConsumer);
        }
    }

    public static void setCancelReturnPredicate(Class<INetHandlerPlayClient> clazz, Predicate<Packet<INetHandlerPlayClient>> predicate) {
        boolean isIN = false;
        for (Class<?> classes : cancelReturnPredicateMap.keySet()) {
            if (classes != clazz) continue;
            isIN = true;
            break;
        }
        if (isIN) {
            cancelReturnPredicateMap.replace(clazz, predicate);
        } else {
            cancelReturnPredicateMap.put(clazz, predicate);
        }
    }

    public static void startFakeLag() {
        if (lagging) {
            return;
        }
        releaseActionMap.clear();
        cancelActionMap.clear();
        lagging = true;
        lagOthersMovingPackets = true;
    }

    public static void stopFakeLag() {
        lagging = false;
    }

    public static boolean onCanceling(Packet<INetHandlerPlayClient> packet) {
        cancelActionMap.forEach((aClass, packetConsumer) -> {
            if (aClass.isAssignableFrom(packet.getClass())) {
                packetConsumer.accept(packet);
            }
        });
        if (!(lagOthersMovingPackets || packet instanceof S27PacketExplosion || packet instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity)packet).getEntityID() == FakeLagUtils.mc.thePlayer.getEntityId() || packet instanceof S32PacketConfirmTransaction)) {
            return false;
        }
        packets.add(packet);
        return true;
    }

    public static void onReleasing(Packet<INetHandlerPlayClient> packet) {
        releaseActionMap.forEach((aClass, packetConsumer) -> {
            if (aClass.isAssignableFrom(packet.getClass())) {
                packetConsumer.accept(packet);
            }
        });
    }
}

