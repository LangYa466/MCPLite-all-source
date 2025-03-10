/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowOwner
extends EntityAIBase {
    private EntityTameable thePet;
    private EntityLivingBase theOwner;
    World theWorld;
    private double followSpeed;
    private PathNavigate petPathfinder;
    private int field_75343_h;
    float maxDist;
    float minDist;
    private boolean field_75344_i;

    public EntityAIFollowOwner(EntityTameable thePetIn, double followSpeedIn, float minDistIn, float maxDistIn) {
        this.thePet = thePetIn;
        this.theWorld = thePetIn.worldObj;
        this.followSpeed = followSpeedIn;
        this.petPathfinder = thePetIn.getNavigator();
        this.minDist = minDistIn;
        this.maxDist = maxDistIn;
        this.setMutexBits(3);
        if (!(thePetIn.getNavigator() instanceof PathNavigateGround)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = this.thePet.getOwner();
        if (entitylivingbase == null) {
            return false;
        }
        if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer)entitylivingbase).isSpectator()) {
            return false;
        }
        if (this.thePet.isSitting()) {
            return false;
        }
        if (this.thePet.getDistanceSqToEntity(entitylivingbase) < (double)(this.minDist * this.minDist)) {
            return false;
        }
        this.theOwner = entitylivingbase;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        return !this.petPathfinder.noPath() && this.thePet.getDistanceSqToEntity(this.theOwner) > (double)(this.maxDist * this.maxDist) && !this.thePet.isSitting();
    }

    @Override
    public void startExecuting() {
        this.field_75343_h = 0;
        this.field_75344_i = ((PathNavigateGround)this.thePet.getNavigator()).getAvoidsWater();
        ((PathNavigateGround)this.thePet.getNavigator()).setAvoidsWater(false);
    }

    @Override
    public void resetTask() {
        this.theOwner = null;
        this.petPathfinder.clearPathEntity();
        ((PathNavigateGround)this.thePet.getNavigator()).setAvoidsWater(true);
    }

    private boolean func_181065_a(BlockPos p_181065_1_) {
        IBlockState iblockstate = this.theWorld.getBlockState(p_181065_1_);
        Block block = iblockstate.getBlock();
        return block == Blocks.air ? true : !block.isFullCube();
    }

    @Override
    public void updateTask() {
        this.thePet.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0f, this.thePet.getVerticalFaceSpeed());
        if (!this.thePet.isSitting() && --this.field_75343_h <= 0) {
            this.field_75343_h = 10;
            if (!this.petPathfinder.tryMoveToEntityLiving(this.theOwner, this.followSpeed) && !this.thePet.getLeashed() && this.thePet.getDistanceSqToEntity(this.theOwner) >= 144.0) {
                int i = MathHelper.floor_double(this.theOwner.posX) - 2;
                int j = MathHelper.floor_double(this.theOwner.posZ) - 2;
                int k = MathHelper.floor_double(this.theOwner.getEntityBoundingBox().minY);
                for (int l = 0; l <= 4; ++l) {
                    for (int i1 = 0; i1 <= 4; ++i1) {
                        if (l >= 1 && i1 >= 1 && l <= 3 && i1 <= 3 || !World.doesBlockHaveSolidTopSurface(this.theWorld, new BlockPos(i + l, k - 1, j + i1)) || !this.func_181065_a(new BlockPos(i + l, k, j + i1)) || !this.func_181065_a(new BlockPos(i + l, k + 1, j + i1))) continue;
                        this.thePet.setLocationAndAngles((float)(i + l) + 0.5f, k, (float)(j + i1) + 0.5f, this.thePet.rotationYaw, this.thePet.rotationPitch);
                        this.petPathfinder.clearPathEntity();
                        return;
                    }
                }
            }
        }
    }
}

