/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.utils.MinecraftInstance;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class PacketUtils
extends MinecraftInstance {
    public static final PacketUtils INSTANCE = new PacketUtils();
    public static boolean passEvent = false;
    public static int packetSlot = 0;
    public static int noMovePackets = 0;
    public static boolean packetBlocking = false;

    public static void sendPacketNoEvent(Packet<?> packet) {
        passEvent = true;
        if (mc.getNetHandler() == null) {
            return;
        }
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void packetEvent(Packet<?> packet) {
        if (packet instanceof C03PacketPlayer) {
            noMovePackets = ((C03PacketPlayer)packet).moving ? 0 : ++noMovePackets;
        }
        if (packetBlocking && packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)packet).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
            packetBlocking = false;
        }
        if (packet instanceof C09PacketHeldItemChange) {
            packetSlot = ((C09PacketHeldItemChange)packet).slotId;
        }
        if (PacketUtils.mc.thePlayer != null && PacketUtils.mc.thePlayer.getHeldItem() != null && PacketUtils.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && packet instanceof C08PacketPlayerBlockPlacement && ((C08PacketPlayerBlockPlacement)packet).getPosition().getY() == -1) {
            packetBlocking = true;
        }
    }

    public static void packetSend(Packet<?> packet) {
    }

    public static void sendOffHandUseItemPacket() {
        if (ViaLoadingBase.getInstance().getTargetVersion().getOriginalVersion() != 47 && Via.getManager().getConnectionManager().getConnections().iterator().hasNext()) {
            PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
            useItem.write(Type.VAR_INT, 1);
            PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
        }
    }

    public static void sendMainHandUseItemPacket() {
        if (ViaLoadingBase.getInstance().getTargetVersion().getOriginalVersion() != 47 && Via.getManager().getConnectionManager().getConnections().iterator().hasNext()) {
            PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
            useItem.write(Type.VAR_INT, 0);
            PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
        }
    }

    public static void sendSwingPacket() {
        if (Via.getManager().getConnectionManager().getConnections().iterator().hasNext()) {
            PacketWrapper swing = PacketWrapper.create(26, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
            swing.write(Type.VAR_INT, 0);
            PacketUtil.sendToServer(swing, Protocol1_8To1_9.class, true, true);
        }
    }
}

