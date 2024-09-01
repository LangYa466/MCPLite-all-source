/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.Client;
import client.event.events.PacketSendEvent;
import client.utils.MinecraftInstance;
import client.utils.PacketUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S00PacketKeepAlive;

public class BlinkUtils
extends MinecraftInstance {
    public static final BlinkUtils INSTANCE = new BlinkUtils();
    public static boolean blinking = false;
    private static final List<Class<?>> blackList = new ArrayList();
    private static final Map<Class<?>, Predicate<Packet<?>>> addReturnPredicateMap = new HashMap();
    private static final Map<Class<?>, Predicate<Packet<?>>> cancelReturnPredicateMap = new HashMap();
    private static final Map<Class<?>, Predicate<Packet<?>>> releaseReturnPredicateMap = new HashMap();
    private static final Map<Class<?>, Consumer<Packet<?>>> cancelActionMap = new HashMap();
    private static final Map<Class<?>, Consumer<Packet<?>>> releaseActionMap = new HashMap();
    private static final List<Class<?>> whitList = new ArrayList();
    public static LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue();
    public static boolean passEvent = false;

    public static boolean blink(Class<?> ... fliterPackets) {
        if (blinking) {
            return false;
        }
        Arrays.asList(fliterPackets).forEach(e -> {
            blackList.add(e);
            cancelReturnPredicateMap.put(e, f -> true);
        });
        blinking = true;
        return true;
    }

    public static void addWhiteList(Class<?> ... classes) {
        whitList.addAll(Arrays.asList(classes));
    }

    public static void sendPacket(Packet<?> packet, boolean event) {
        if (event) {
            PacketSendEvent packetSendEvent = new PacketSendEvent(packet);
            Client.eventManager.onPacketSend(packetSendEvent);
            if (packetSendEvent.isCancelled()) {
                return;
            }
        }
        passEvent = true;
        PacketUtils.sendPacketNoEvent(packet);
        passEvent = false;
    }

    public static void removeBlackList(Class<?> packetClazz) {
        blackList.remove(packetClazz);
    }

    public static void resetBlackList() {
        blackList.clear();
    }

    public static void setCancelReturnPredicate(Class<?> clazz, Predicate<Packet<?>> predicate) {
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

    public static void setAddReturnPredicate(Class<?> clazz, Predicate<Packet<?>> predicate) {
        boolean isIN = false;
        for (Class<?> classes : addReturnPredicateMap.keySet()) {
            if (classes != clazz) continue;
            isIN = true;
            break;
        }
        if (isIN) {
            addReturnPredicateMap.replace(clazz, predicate);
        } else {
            addReturnPredicateMap.put(clazz, predicate);
        }
    }

    public static void setReleaseReturnPredicateMap(Class<?> clazz, Predicate<Packet<?>> predicate) {
        boolean isIN = false;
        for (Class<?> classes : releaseReturnPredicateMap.keySet()) {
            if (classes != clazz) continue;
            isIN = true;
            break;
        }
        if (isIN) {
            releaseReturnPredicateMap.replace(clazz, predicate);
        } else {
            releaseReturnPredicateMap.put(clazz, predicate);
        }
    }

    public static void setCancelAction(Class<?> clazz, Consumer<Packet<?>> packetConsumer) {
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

    public static void setReleaseAction(Class<?> clazz, Consumer<Packet<?>> packetConsumer) {
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

    public static void releasePacket(boolean sendOneTick) {
        BlinkUtils.releasePacket(packets.size(), true, sendOneTick);
    }

    public static void releasePacket() {
        BlinkUtils.releasePacket(packets.size(), true);
    }

    public static void releasePacket(int sendPackets, boolean noEvent) {
        BlinkUtils.releasePacket(sendPackets, noEvent, false);
    }

    public static void releasePacket(int sendPackets, boolean noEvent, boolean sendOneTick) {
        int sends = 0;
        try {
            block2: while (!packets.isEmpty()) {
                Packet<?> packet = packets.take();
                if (packet instanceof S00PacketKeepAlive) {
                    if (!sendOneTick) continue;
                } else {
                    for (Map.Entry<Class<?>, Predicate<Packet<?>>> entries : releaseReturnPredicateMap.entrySet()) {
                        if (!entries.getKey().isAssignableFrom(packet.getClass()) || !entries.getValue().test(packet)) continue;
                        continue block2;
                    }
                    releaseActionMap.forEach((key, value) -> {
                        if (key.isAssignableFrom(packet.getClass())) {
                            value.accept(packet);
                        }
                    });
                    ++sends;
                    if (noEvent) {
                        passEvent = true;
                        PacketUtils.sendPacketNoEvent(packet);
                        passEvent = false;
                    } else {
                        passEvent = true;
                        mc.getNetHandler().addToSendQueue(packet);
                        passEvent = false;
                    }
                    if (sends < sendPackets) continue;
                }
                break;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void stopBlink() {
        blinking = false;
        passEvent = false;
        BlinkUtils.releasePacket();
        blackList.clear();
        cancelReturnPredicateMap.clear();
        cancelActionMap.clear();
        releaseActionMap.clear();
        releaseReturnPredicateMap.clear();
        whitList.clear();
        packets.clear();
    }

    public static boolean onPacket(Packet<?> packet) {
        if (blinking && !passEvent) {
            cancelActionMap.forEach((aClass, packetConsumer) -> {
                if (aClass.isAssignableFrom(packet.getClass())) {
                    packetConsumer.accept(packet);
                }
            });
            for (Class<?> clazz : blackList) {
                if (!clazz.isAssignableFrom(packet.getClass())) continue;
                return true;
            }
            for (Map.Entry entry : cancelReturnPredicateMap.entrySet()) {
                if (!((Class)entry.getKey()).isAssignableFrom(packet.getClass()) || !((Predicate)entry.getValue()).test(packet)) continue;
                return true;
            }
            if (!whitList.isEmpty() && !whitList.contains(packet.getClass())) {
                return true;
            }
            boolean needAdd = true;
            for (Map.Entry<Class<?>, Predicate<Packet<?>>> entries : addReturnPredicateMap.entrySet()) {
                if (!entries.getKey().isAssignableFrom(packet.getClass()) || !entries.getValue().test(packet)) continue;
                needAdd = false;
            }
            if (needAdd) {
                packets.add(packet);
            }
            return false;
        }
        return true;
    }

    public void onTick() {
        if (mc.getNetHandler() == null) {
            BlinkUtils.stopBlink();
        }
        if (blinking) {
            packets.add(new S00PacketKeepAlive());
        }
    }
}

