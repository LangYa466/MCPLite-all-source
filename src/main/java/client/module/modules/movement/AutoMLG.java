/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.movement;

import client.event.events.MotionEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.BlockData;
import client.utils.BlockUtils;
import client.utils.MovementUtils;
import client.utils.TimeUtil;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class AutoMLG
extends Module {
    @Settings(maxValue=10.0)
    private double height = 2.0;
    private double fallStartY = 0.0;
    private final TimeUtil timer = new TimeUtil();
    private BlockData blockBelowData;
    private boolean nextPlaceWater = false;
    private boolean nextRemoveWater = false;

    public AutoMLG() {
        super("AutoMLG", 0, false, ModuleType.MOVEMENT);
    }

    @Override
    public void onMotion(MotionEvent event) {
        if (event.isPre()) {
            if (!AutoMLG.mc.thePlayer.onGround && AutoMLG.mc.thePlayer.motionY < 0.0) {
                if (this.fallStartY < AutoMLG.mc.thePlayer.posY) {
                    this.fallStartY = AutoMLG.mc.thePlayer.posY;
                }
                if (this.fallStartY - AutoMLG.mc.thePlayer.posY > this.height) {
                    double x = AutoMLG.mc.thePlayer.posX + AutoMLG.mc.thePlayer.motionX * 1.25;
                    double y = AutoMLG.mc.thePlayer.posY - (double)AutoMLG.mc.thePlayer.getEyeHeight();
                    double z = AutoMLG.mc.thePlayer.posZ + AutoMLG.mc.thePlayer.motionZ * 1.25;
                    BlockPos blockBelow = new BlockPos(x, y, z);
                    IBlockState blockState = AutoMLG.mc.theWorld.getBlockState(blockBelow);
                    IBlockState underBlockState = AutoMLG.mc.theWorld.getBlockState(blockBelow.offsetDown());
                    if (underBlockState.getBlock().isBlockNormalCube() && !AutoMLG.mc.thePlayer.isSneaking() && (blockState.getBlock() == Blocks.air || blockState.getBlock() == Blocks.snow_layer || blockState.getBlock() == Blocks.tallgrass) && this.timer.delay(100.0f)) {
                        this.timer.reset();
                        this.blockBelowData = this.getBlockData(blockBelow);
                        if (this.blockBelowData != null) {
                            this.nextPlaceWater = true;
                            this.nextRemoveWater = false;
                            float[] rotations = MovementUtils.getRotationsBlock(this.blockBelowData.position, this.blockBelowData.face);
                            event.setYaw(rotations[0]);
                            event.setPitch(rotations[1]);
                        }
                    }
                }
            } else {
                this.fallStartY = AutoMLG.mc.thePlayer.posY;
            }
            if (this.blockBelowData != null && AutoMLG.mc.thePlayer.isInWater()) {
                this.nextRemoveWater = true;
                float[] rotations = MovementUtils.getRotationsBlock(this.blockBelowData.position, this.blockBelowData.face);
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            }
        } else if (this.blockBelowData != null && this.nextPlaceWater) {
            this.placeWater();
        } else if (this.blockBelowData != null && this.nextRemoveWater) {
            this.getWaterBack();
        }
    }

    private int swapToItem(int item) {
        AutoMLG.mc.rightClickDelayTimer = 2;
        int currentItem = AutoMLG.mc.thePlayer.inventory.currentItem;
        AutoMLG.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(item - 36));
        AutoMLG.mc.thePlayer.inventory.currentItem = item - 36;
        AutoMLG.mc.playerController.updateController();
        return currentItem;
    }

    private void placeWater() {
        for (Map.Entry<Integer, Item> item : this.getHotbarItems().entrySet()) {
            if (!item.getValue().equals(Items.water_bucket)) continue;
            int currentItem = this.swapToItem(item.getKey());
            AutoMLG.mc.playerController.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(AutoMLG.mc.thePlayer.inventory.getCurrentItem()));
            AutoMLG.mc.thePlayer.inventory.currentItem = currentItem;
            AutoMLG.mc.playerController.updateController();
            break;
        }
        this.nextPlaceWater = false;
    }

    private void getWaterBack() {
        for (Map.Entry<Integer, Item> item : this.getHotbarItems().entrySet()) {
            if (!item.getValue().equals(Items.bucket)) continue;
            int currentItem = this.swapToItem(item.getKey());
            AutoMLG.mc.playerController.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(AutoMLG.mc.thePlayer.inventory.getCurrentItem()));
            AutoMLG.mc.thePlayer.inventory.currentItem = currentItem;
            AutoMLG.mc.playerController.updateController();
            break;
        }
        this.blockBelowData = null;
        this.nextRemoveWater = false;
    }

    private HashMap<Integer, Item> getHotbarItems() {
        HashMap<Integer, Item> items = new HashMap<Integer, Item>();
        for (int i = 36; i < 45; ++i) {
            if (!AutoMLG.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;
            ItemStack itemStack = AutoMLG.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            items.put(i, itemStack.getItem());
        }
        return items;
    }

    private BlockData getBlockData(BlockPos pos) {
        if (!BlockUtils.getBlacklistedBlocks().contains(AutoMLG.mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        return null;
    }
}

