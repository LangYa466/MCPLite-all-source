/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S36PacketSignEditorOpen
implements Packet<INetHandlerPlayClient> {
    private BlockPos signPosition;

    public S36PacketSignEditorOpen() {
    }

    public S36PacketSignEditorOpen(BlockPos signPositionIn) {
        this.signPosition = signPositionIn;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSignEditorOpen(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.signPosition = buf.readBlockPos();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeBlockPos(this.signPosition);
    }

    public BlockPos getSignPosition() {
        return this.signPosition;
    }
}

