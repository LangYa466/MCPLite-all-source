/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.utils.MinecraftInstance;
import client.utils.RenderUtils;
import java.awt.Color;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class ProjectileUtil
extends MinecraftInstance {
    public static ProjectileHit predict(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, double motionSlowdown, double size, double gravity, boolean draw) {
        MovingObjectPosition landingPosition = null;
        boolean hasLanded = false;
        boolean hitEntity = false;
        if (draw) {
            RenderUtils.enableRender3D(true);
            RenderUtils.color(new Color(230, 230, 230).getRGB());
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
        }
        while (!hasLanded && posY > -60.0) {
            Vec3 posBefore = new Vec3(posX, posY, posZ);
            Vec3 posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
            landingPosition = ProjectileUtil.mc.theWorld.rayTraceBlocks(posBefore, posAfter, false, true, false);
            posBefore = new Vec3(posX, posY, posZ);
            posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
            if (landingPosition != null) {
                hasLanded = true;
                posAfter = new Vec3(landingPosition.hitVec.xCoord, landingPosition.hitVec.yCoord, landingPosition.hitVec.zCoord);
            }
            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);
            List<Entity> entityList = ProjectileUtil.mc.theWorld.getEntitiesWithinAABB(Entity.class, arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
            for (int i = 0; i < entityList.size(); ++i) {
                AxisAlignedBB var2;
                MovingObjectPosition possibleEntityLanding;
                Entity var18 = entityList.get(i);
                if (!var18.canBeCollidedWith() || var18 == ProjectileUtil.mc.thePlayer || (possibleEntityLanding = (var2 = var18.getEntityBoundingBox().expand(size, size, size)).calculateIntercept(posBefore, posAfter)) == null) continue;
                hitEntity = true;
                hasLanded = true;
                landingPosition = possibleEntityLanding;
            }
            BlockPos var35 = new BlockPos(posX += motionX, posY += motionY, posZ += motionZ);
            Block var36 = ProjectileUtil.mc.theWorld.getBlockState(var35).getBlock();
            if (var36.getBlockState().getBlock().getMaterial() == Material.water) {
                motionX *= 0.6;
                motionY *= 0.6;
                motionZ *= 0.6;
            } else {
                motionX *= motionSlowdown;
                motionY *= motionSlowdown;
                motionZ *= motionSlowdown;
            }
            motionY -= gravity;
            if (!draw) continue;
            GL11.glVertex3d(posX - mc.getRenderManager().getRenderPosX(), posY - mc.getRenderManager().getRenderPosY(), posZ - mc.getRenderManager().getRenderPosZ());
        }
        return new ProjectileHit(posX, posY, posZ, hitEntity, hasLanded, landingPosition);
    }

    public static class EnderPearlPredictor {
        public double predictX;
        public double predictY;
        public double predictZ;
        public double minMotionY;
        public double maxMotionY;

        public EnderPearlPredictor(double predictX, double predictY, double predictZ, double minMotionY, double maxMotionY) {
            this.predictX = predictX;
            this.predictY = predictY;
            this.predictZ = predictZ;
            this.minMotionY = minMotionY;
            this.maxMotionY = maxMotionY;
        }

        public double assessRotation(Vector2f rotation) {
            double mul = 1.0;
            int cnt = 0;
            for (double rate = 0.0; rate <= 1.0; rate += 0.3333) {
                for (int yaw = -1; yaw <= 1; ++yaw) {
                    for (int pitch = -1; pitch <= 1; ++pitch) {
                        mul *= this.assessSingleRotation(new Vector2f(rotation.getX() + (float)yaw * 0.5f, rotation.getY() + (float)pitch * 0.5f), MathHelper.interpolate(this.minMotionY, this.maxMotionY, rate));
                        ++cnt;
                    }
                }
                if (this.minMotionY == this.maxMotionY) break;
            }
            return Math.pow(mul, 1.0 / (double)cnt);
        }

        private double assessSingleRotation(Vector2f rotation, double motionYOffset) {
            if (rotation.y > 90.0f) {
                rotation.y = 90.0f;
            }
            if (rotation.y < -90.0f) {
                rotation.y = -90.0f;
            }
            float motionFactor = 1.5f;
            float gravity = 0.03f;
            float size = 0.25f;
            float motionSlowdown = 0.99f;
            double posX = this.predictX - (double)(MathHelper.cos(rotation.x / 180.0f * (float)Math.PI) * 0.16f);
            double posY = this.predictY + (double)MinecraftInstance.mc.thePlayer.getEyeHeight() - (double)0.1f;
            double posZ = this.predictZ - (double)(MathHelper.sin(rotation.y / 180.0f * (float)Math.PI) * 0.16f);
            double motionX = (double)(-MathHelper.sin(rotation.x / 180.0f * (float)Math.PI) * MathHelper.cos(rotation.y / 180.0f * (float)Math.PI)) * 0.4;
            double motionY = (double)(-MathHelper.sin(rotation.y / 180.0f * (float)Math.PI)) * 0.4;
            double motionZ = (double)(MathHelper.cos(rotation.x / 180.0f * (float)Math.PI) * MathHelper.cos(rotation.y / 180.0f * (float)Math.PI)) * 0.4;
            float distance = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
            motionX /= (double)distance;
            motionY /= (double)distance;
            motionZ /= (double)distance;
            motionY *= 1.5;
            ProjectileHit projectileHit = ProjectileUtil.predict(posX, posY, posZ, motionX *= 1.5, motionY += motionYOffset, motionZ *= 1.5, 0.99f, 0.25, 0.03f, false);
            if (!projectileHit.hasLanded) {
                return 0.05;
            }
            EnumFacing facing = ((ProjectileHit)projectileHit).landingPosition.sideHit;
            BlockPos landPos = projectileHit.landingPosition.getBlockPos().add(facing.getDirectionVec());
            return (facing == EnumFacing.UP || facing == EnumFacing.DOWN ? this.assessPlainBlockPos(landPos) : this.assessSideBlockPos(landPos, facing)) * this.distanceFunction(new Vec3(this.predictX, this.predictY, this.predictZ).distanceTo(new Vec3(projectileHit.posX, projectileHit.posY, projectileHit.posZ)));
        }

        private double assessPlainBlockPos(BlockPos pos) {
            double mul = 1.0;
            mul *= Math.pow(this.assessSingleBlockPos(pos.add(0, 0, 0)), 2.0);
            mul *= this.assessSingleBlockPos(pos.add(1, 0, 0));
            mul *= this.assessSingleBlockPos(pos.add(-1, 0, 0));
            mul *= this.assessSingleBlockPos(pos.add(0, 0, 1));
            return Math.pow(mul *= this.assessSingleBlockPos(pos.add(0, 0, -1)), 0.16666666666666666);
        }

        private double assessSideBlockPos(BlockPos pos, EnumFacing facing) {
            double mul = 1.0;
            mul *= Math.pow(this.assessSingleBlockPos(pos.add(0, 0, 0)), 2.0);
            mul *= this.assessSingleBlockPos(pos.add(1, 0, 0));
            return Math.pow(mul *= this.assessSingleBlockPos(pos.add(facing.getDirectionVec())), 0.3333333333333333);
        }

        private double assessSingleBlockPos(BlockPos pos) {
            for (int y2 = 0; y2 >= -5; --y2) {
                IBlockState blockState = MinecraftInstance.mc.theWorld.getBlockState(pos.add(0, y2, 0));
                if (y2 == 0 && blockState.getBlock().isFullBlock()) {
                    return 0.4;
                }
                if (!blockState.getBlock().isFullBlock()) continue;
                return 1.0;
            }
            return 0.05;
        }

        private double distanceFunction(double d2) {
            return ((d2 /= 1000.0) + 3.0) / (d2 + 2.0) / 1.5;
        }
    }

    public static class ProjectileHit {
        private final double posX;
        private final double posY;
        private final double posZ;
        private final boolean hitEntity;
        private final boolean hasLanded;
        private final MovingObjectPosition landingPosition;

        public ProjectileHit(double posX, double posY, double posZ, boolean hitEntity, boolean hasLanded, MovingObjectPosition landingPosition) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.hitEntity = hitEntity;
            this.hasLanded = hasLanded;
            this.landingPosition = landingPosition;
        }

        public double getPosX() {
            return this.posX;
        }

        public double getPosY() {
            return this.posY;
        }

        public double getPosZ() {
            return this.posZ;
        }

        public boolean isHitEntity() {
            return this.hitEntity;
        }

        public boolean isHasLanded() {
            return this.hasLanded;
        }

        public MovingObjectPosition getLandingPosition() {
            return this.landingPosition;
        }

        public int hashCode() {
            return Objects.hash(this.posX, this.posY, this.posZ, this.hitEntity, this.hasLanded, this.landingPosition);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            ProjectileHit that = (ProjectileHit)obj;
            return Double.compare(that.posX, this.posX) == 0 && Double.compare(that.posY, this.posY) == 0 && Double.compare(that.posZ, this.posZ) == 0 && this.hitEntity == that.hitEntity && this.hasLanded == that.hasLanded && Objects.equals(this.landingPosition, that.landingPosition);
        }

        public String toString() {
            return "ProjectileHit{posX=" + this.posX + ", posY=" + this.posY + ", posZ=" + this.posZ + ", hitEntity=" + this.hitEntity + ", hasLanded=" + this.hasLanded + ", landingPosition=" + this.landingPosition + '}';
        }
    }
}

