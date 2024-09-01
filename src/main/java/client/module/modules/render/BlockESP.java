/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.render;

import client.event.events.PacketReceiveSyncEvent;
import client.event.events.PacketSendEvent;
import client.event.events.Render3DEventBeforeHand;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.BlockUtils;
import client.utils.MSTimer;
import client.utils.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockContainer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.BlockPos;

public class BlockESP
extends Module {
    @Settings(minValue=5.0, maxValue=120.0)
    private int radiusValue = 40;
    @Settings
    private boolean chest = false;
    @Settings
    private boolean furnace = false;
    @Settings
    private boolean bed = false;
    @Settings
    private boolean brewingStand = false;
    @Settings(maxValue=255.0)
    public int R = 255;
    @Settings(maxValue=255.0)
    public int G = 0;
    @Settings(maxValue=255.0)
    public int B = 0;
    @Settings(maxValue=255.0)
    public int openR = 0;
    @Settings(maxValue=255.0)
    public int openG = 255;
    @Settings(maxValue=255.0)
    public int openB = 0;
    @Settings(maxValue=255.0)
    public int bedR = 0;
    @Settings(maxValue=255.0)
    public int bedG = 255;
    @Settings(maxValue=255.0)
    public int bedB = 0;
    @Settings(maxValue=255.0)
    private int alpha = 26;
    private final MSTimer searchTimer = new MSTimer();
    private final List<BlockPos> posList = new ArrayList<BlockPos>();
    private Thread thread;
    private List<BlockPos> hasOpen = new ArrayList<BlockPos>();

    public BlockESP() {
        super("BlockESP", 0, false, ModuleType.RENDER);
    }

    @Override
    public void onUpdate() {
        if (this.searchTimer.hasPassed(1000L) && (this.thread == null || !this.thread.isAlive())) {
            int radius = this.radiusValue;
            this.thread = new Thread(() -> {
                ArrayList<BlockPos> blockList = new ArrayList<BlockPos>();
                for (int x = -radius; x < radius; ++x) {
                    for (int y = radius; y > -radius; --y) {
                        for (int z = -radius; z < radius; ++z) {
                            int xPos = (int)BlockESP.mc.thePlayer.posX + x;
                            int yPos = (int)BlockESP.mc.thePlayer.posY + y;
                            int zPos = (int)BlockESP.mc.thePlayer.posZ + z;
                            BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
                            Block block = BlockUtils.getBlock(blockPos);
                            if (this.chest && (block == Blocks.chest || block == Blocks.ender_chest)) {
                                blockList.add(blockPos);
                            }
                            if (this.furnace && block == Blocks.furnace) {
                                blockList.add(blockPos);
                            }
                            if (this.bed && block instanceof BlockBed) {
                                blockList.add(blockPos);
                            }
                            if (!this.brewingStand || !(block instanceof BlockBrewingStand)) continue;
                            blockList.add(blockPos);
                        }
                    }
                }
                this.searchTimer.reset();
                List<BlockPos> list = this.posList;
                synchronized (list) {
                    this.posList.clear();
                    this.posList.addAll(blockList);
                }
            }, "BlockESP-BlockFinder");
            this.thread.start();
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        C08PacketPlayerBlockPlacement c08;
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement && BlockUtils.getBlock((c08 = (C08PacketPlayerBlockPlacement)event.getPacket()).getPosition()) instanceof BlockContainer) {
            this.hasOpen.add(c08.getPosition());
        }
    }

    @Override
    public void onWorldLoad() {
        this.hasOpen.clear();
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        String s;
        S45PacketTitle wrapper;
        if (event.getPacket() instanceof S45PacketTitle && (wrapper = (S45PacketTitle)event.getPacket()).getType() == S45PacketTitle.Type.TITLE && (s = wrapper.getMessage().getFormattedText()).contains("\u6218\u6597\u5f00\u59cb...")) {
            this.hasOpen.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onRender3D(Render3DEventBeforeHand event) {
        List<BlockPos> list = this.posList;
        synchronized (list) {
            for (BlockPos blockPos : this.posList) {
                Color color = this.hasOpen.contains(blockPos) ? new Color(this.openR, this.openG, this.openB, this.alpha) : (BlockUtils.getBlock(blockPos) instanceof BlockBed ? new Color(this.bedR, this.bedG, this.bedB, this.alpha) : new Color(this.R, this.G, this.B, this.alpha));
                RenderUtils.drawBlockBox(blockPos, color, true);
            }
        }
    }
}

