/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.movement;

import client.module.Module;
import client.module.ModuleType;
import client.utils.BlockUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class FastLadder
extends Module {
    private List<BlockPos> blockLadder = new ArrayList<BlockPos>();
    public boolean cancel = false;
    private boolean normalClimb = false;

    public FastLadder() {
        super("FastLadder", 0, ModuleType.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (!FastLadder.mc.thePlayer.isOnLadder()) {
            this.normalClimb = false;
            this.cancel = false;
            this.blockLadder.clear();
        }
        if (!FastLadder.mc.thePlayer.isOnLadder() && !this.cancel) {
            return;
        }
        if (this.normalClimb && FastLadder.mc.thePlayer.isOnLadder()) {
            return;
        }
        if (FastLadder.mc.thePlayer.isOnLadder() && FastLadder.mc.gameSettings.keyBindJump.isKeyDown()) {
            this.blockLadder.clear();
            this.cancel = false;
            this.normalClimb = true;
            return;
        }
        for (Map.Entry<BlockPos, Block> entry : this.searchBlocks(4).entrySet()) {
            BlockPos block = entry.getKey();
            Block value = entry.getValue();
            if (!(value instanceof BlockLadder) || this.blockLadder.contains(block)) continue;
            this.blockLadder.add(block);
        }
        if (!this.blockLadder.isEmpty()) {
            for (BlockPos block : this.blockLadder) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, block, EnumFacing.DOWN));
                mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block, EnumFacing.DOWN));
            }
            if (FastLadder.mc.thePlayer.isOnLadder()) {
                this.cancel = true;
            }
        }
    }

    public Map<BlockPos, Block> searchBlocks(int radius) {
        HashMap<BlockPos, Block> blocks = new HashMap<BlockPos, Block>();
        EntityPlayerSP thePlayer = FastLadder.mc.thePlayer;
        if (thePlayer == null) {
            return blocks;
        }
        for (int x = radius; x >= -radius + 1; --x) {
            for (int y = radius; y >= -radius + 1; --y) {
                for (int z = radius; z >= -radius + 1; --z) {
                    BlockPos blockPos = new BlockPos(thePlayer.posX + (double)x, thePlayer.posY + (double)y, thePlayer.posZ + (double)z);
                    Block block = BlockUtils.getBlock(blockPos);
                    if (block == null) continue;
                    blocks.put(blockPos, block);
                }
            }
        }
        return blocks;
    }
}

