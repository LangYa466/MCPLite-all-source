/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.village;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class VillageDoorInfo {
    private final BlockPos doorBlockPos;
    private final BlockPos insideBlock;
    private final EnumFacing insideDirection;
    private int lastActivityTimestamp;
    private boolean isDetachedFromVillageFlag;
    private int doorOpeningRestrictionCounter;

    public VillageDoorInfo(BlockPos pos, int p_i45871_2_, int p_i45871_3_, int p_i45871_4_) {
        this(pos, VillageDoorInfo.getFaceDirection(p_i45871_2_, p_i45871_3_), p_i45871_4_);
    }

    private static EnumFacing getFaceDirection(int deltaX, int deltaZ) {
        return deltaX < 0 ? EnumFacing.WEST : (deltaX > 0 ? EnumFacing.EAST : (deltaZ < 0 ? EnumFacing.NORTH : EnumFacing.SOUTH));
    }

    public VillageDoorInfo(BlockPos pos, EnumFacing facing, int timestamp) {
        this.doorBlockPos = pos;
        this.insideDirection = facing;
        this.insideBlock = pos.offset(facing, 2);
        this.lastActivityTimestamp = timestamp;
    }

    public int getDistanceSquared(int x, int y, int z) {
        return (int)this.doorBlockPos.distanceSq(x, y, z);
    }

    public int getDistanceToDoorBlockSq(BlockPos pos) {
        return (int)pos.distanceSq(this.getDoorBlockPos());
    }

    public int getDistanceToInsideBlockSq(BlockPos pos) {
        return (int)this.insideBlock.distanceSq(pos);
    }

    public boolean func_179850_c(BlockPos pos) {
        int i = pos.getX() - this.doorBlockPos.getX();
        int j = pos.getZ() - this.doorBlockPos.getY();
        return i * this.insideDirection.getFrontOffsetX() + j * this.insideDirection.getFrontOffsetZ() >= 0;
    }

    public void resetDoorOpeningRestrictionCounter() {
        this.doorOpeningRestrictionCounter = 0;
    }

    public void incrementDoorOpeningRestrictionCounter() {
        ++this.doorOpeningRestrictionCounter;
    }

    public int getDoorOpeningRestrictionCounter() {
        return this.doorOpeningRestrictionCounter;
    }

    public BlockPos getDoorBlockPos() {
        return this.doorBlockPos;
    }

    public BlockPos getInsideBlockPos() {
        return this.insideBlock;
    }

    public int getInsideOffsetX() {
        return this.insideDirection.getFrontOffsetX() * 2;
    }

    public int getInsideOffsetZ() {
        return this.insideDirection.getFrontOffsetZ() * 2;
    }

    public int getInsidePosY() {
        return this.lastActivityTimestamp;
    }

    public void func_179849_a(int timestamp) {
        this.lastActivityTimestamp = timestamp;
    }

    public boolean getIsDetachedFromVillageFlag() {
        return this.isDetachedFromVillageFlag;
    }

    public void setIsDetachedFromVillageFlag(boolean detached) {
        this.isDetachedFromVillageFlag = detached;
    }
}

