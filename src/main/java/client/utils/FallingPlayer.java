/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class FallingPlayer {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final double GRAVITY = 0.08;
    private static final double AIR_FRICTION = (double)0.98f;
    private static final double WATER_FRICTION = (double)0.91f;
    private static final float PI = (float)Math.PI;
    public double x;
    public double y;
    public double z;
    private double motionX;
    private double motionY;
    private double motionZ;
    private final float yaw;
    private final float strafe;
    private final float forward;

    public FallingPlayer(EntityPlayer player) {
        this(player.posX, player.posY, player.posZ, player.motionX, player.motionY, player.motionZ, player.rotationYaw, player.moveStrafing, player.moveForward);
    }

    public FallingPlayer(double x, double y, double z, double motionX, double motionY, double motionZ, float yaw, float strafe, float forward) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.yaw = yaw;
        this.strafe = strafe;
        this.forward = forward;
    }

    private void calculateForTick() {
        float v = this.strafe * this.strafe + this.forward * this.forward;
        if (v >= 1.0E-4f) {
            v = Math.max((float)Math.sqrt(v), 1.0f);
            v = FallingPlayer.mc.thePlayer.jumpMovementFactor / v;
            float strafeForce = this.strafe * v;
            float forwardForce = this.forward * v;
            float f1 = (float)Math.sin(this.yaw * (float)Math.PI / 180.0f);
            float f2 = (float)Math.cos(this.yaw * (float)Math.PI / 180.0f);
            this.motionX += (double)(strafeForce * f2 - forwardForce * f1);
            this.motionZ += (double)(forwardForce * f2 + strafeForce * f1);
        }
        this.motionY -= 0.08;
        this.motionX *= (double)0.91f;
        this.motionY *= (double)0.98f;
        this.motionZ *= (double)0.91f;
        this.x += this.motionX;
        this.y += this.motionY;
        this.z += this.motionZ;
    }

    public CollisionResult findCollision(int ticks) {
        for (int i = 0; i < ticks; ++i) {
            Vec3 start = new Vec3(this.x, this.y, this.z);
            this.calculateForTick();
            Vec3 end = new Vec3(this.x, this.y, this.z);
            float w = FallingPlayer.mc.thePlayer.width / 2.0f;
            BlockPos raytracedBlock = this.rayTraceWide(start, end, w);
            if (raytracedBlock == null) continue;
            return new CollisionResult(raytracedBlock, i);
        }
        return null;
    }

    private BlockPos rayTraceWide(Vec3 start, Vec3 end, float width) {
        Vec3[] checkVectors;
        for (Vec3 vec : checkVectors = new Vec3[]{new Vec3(width, 0.0, width), new Vec3(-width, 0.0, width), new Vec3(width, 0.0, -width), new Vec3(-width, 0.0, -width), new Vec3(width, 0.0, width / 2.0f), new Vec3(-width, 0.0, width / 2.0f), new Vec3(width / 2.0f, 0.0, width), new Vec3(width / 2.0f, 0.0, -width)}) {
            BlockPos block = this.rayTrace(start.add(vec), end);
            if (block == null) continue;
            return block;
        }
        return null;
    }

    private BlockPos rayTrace(Vec3 start, Vec3 end) {
        MovingObjectPosition result = FallingPlayer.mc.theWorld.rayTraceBlocks(start, end, true);
        if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && result.sideHit == EnumFacing.UP) {
            return result.getBlockPos();
        }
        return null;
    }

    public static class CollisionResult {
        private final BlockPos pos;
        private final int tick;

        public CollisionResult(BlockPos pos, int tick) {
            this.pos = pos;
            this.tick = tick;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public int getTick() {
            return this.tick;
        }
    }
}

