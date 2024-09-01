/*
 * Decompiled with CFR 0.151.
 */
package client.utils.rotation;

import client.utils.MinecraftInstance;
import client.utils.rotation.RotationSetter;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import org.lwjgl.util.vector.Vector2f;

public class RotationUtils
extends MinecraftInstance {
    public static Vector2f toRotation(Vec3 vec, boolean predict) {
        Vec3 eyesPos = new Vec3(RotationUtils.mc.thePlayer.posX, RotationUtils.mc.thePlayer.getEntityBoundingBox().minY + (double)RotationUtils.mc.thePlayer.getEyeHeight(), RotationUtils.mc.thePlayer.posZ);
        if (predict) {
            eyesPos.addVector(RotationUtils.mc.thePlayer.motionX, RotationUtils.mc.thePlayer.motionY, RotationUtils.mc.thePlayer.motionZ);
        }
        double diffX = vec.getXCoord() - eyesPos.getXCoord();
        double diffY = vec.getYCoord() - eyesPos.getYCoord();
        double diffZ = vec.getZCoord() - eyesPos.getZCoord();
        return new Vector2f(MathHelper.wrapAngleTo180_float((float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f), MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))));
    }

    private static float getAngleDifference(float a, float b) {
        return ((a - b) % 360.0f + 540.0f) % 360.0f - 180.0f;
    }

    public static double getRotationDifference(Vector2f a, Vector2f b) {
        return Math.hypot(RotationUtils.getAngleDifference(a.x, b.y), a.x - b.y);
    }

    public static double getYawDiff(Vector2f a, Vector2f b) {
        return Math.abs(MathHelper.wrapAngleTo180_float(a.x) - MathHelper.wrapAngleTo180_float(b.x));
    }

    public static boolean isVisible(Vec3 vec3) {
        Vec3 eyesPos = new Vec3(RotationUtils.mc.thePlayer.posX, RotationUtils.mc.thePlayer.getEntityBoundingBox().getMinY() + (double)RotationUtils.mc.thePlayer.getEyeHeight(), RotationUtils.mc.thePlayer.posZ);
        return RotationUtils.mc.theWorld.rayTraceBlocks(eyesPos, vec3) == null;
    }

    public static Vector2f searchCenter(AxisAlignedBB bb, boolean predict, boolean throughWalls, float distance) {
        Vec3 vec;
        double dist;
        Vec3 eyes = RotationUtils.mc.thePlayer.getPositionEyes(1.0f);
        Vector2f vecRotation = null;
        for (double xSearch = 0.5; xSearch < 0.85; xSearch += 0.75) {
            for (double ySearch = 0.5; ySearch < 1.0; ySearch += 0.75) {
                for (double zSearch = 0.5; zSearch < 0.85; zSearch += 0.75) {
                    Vector2f v;
                    Vec3 vec3 = new Vec3(bb.getMinX() + (bb.maxX - bb.getMinX()) * xSearch, bb.getMinY() + (bb.getMaxY() - bb.getMinY()) * ySearch, bb.getMinZ() + (bb.getMaxZ() - bb.getMinZ()) * zSearch);
                    Vector2f rotation = RotationUtils.toRotation(vec3, predict);
                    double vecDist = eyes.distanceTo(vec3);
                    if (vecDist > (double)distance || !throughWalls && !RotationUtils.isVisible(vec3)) continue;
                    Vector2f currentVec = rotation;
                    Vector2f vector2f = v = RotationSetter.targetRotation != null ? RotationSetter.targetRotation : new Vector2f(RotationUtils.mc.thePlayer.rotationYaw, RotationUtils.mc.thePlayer.rotationPitch);
                    if (vecRotation != null && !(RotationUtils.getRotationDifference(currentVec, v) < RotationUtils.getRotationDifference(vecRotation, v))) continue;
                    vecRotation = currentVec;
                }
            }
        }
        if (vecRotation == null && (dist = eyes.distanceTo(vec = RotationUtils.getNearestPointBB(eyes, bb))) <= (double)distance && (throughWalls || RotationUtils.isVisible(vec))) {
            return new Vector2f(RotationUtils.toRotation(vec, predict));
        }
        return vecRotation;
    }

    public static Vec3 getNearestPointBB(Vec3 eye, AxisAlignedBB box) {
        double[] origin = new double[]{eye.xCoord, eye.yCoord, eye.zCoord};
        double[] destMins = new double[]{box.minX, box.minY, box.minZ};
        double[] destMaxs = new double[]{box.maxX, box.maxY, box.maxZ};
        for (int i = 0; i <= 2; ++i) {
            if (origin[i] > destMaxs[i]) {
                origin[i] = destMaxs[i];
                continue;
            }
            if (!(origin[i] < destMins[i])) continue;
            origin[i] = destMins[i];
        }
        return new Vec3(origin[0], origin[1], origin[2]);
    }

    public static float[] getRotationsToPosition(double x, double y, double z) {
        double deltaX = x - RotationUtils.mc.thePlayer.posX;
        double deltaY = y - RotationUtils.mc.thePlayer.posY - (double)RotationUtils.mc.thePlayer.getEyeHeight();
        double deltaZ = z - RotationUtils.mc.thePlayer.posZ;
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yaw = (float)Math.toDegrees(-Math.atan2(deltaX, deltaZ));
        float pitch = (float)Math.toDegrees(-Math.atan2(deltaY, horizontalDistance));
        return new float[]{yaw, pitch};
    }

    public static Entity raycastEntity(Double range, Float yaw, Float pitch) {
        Entity renderViewEntity = mc.getRenderViewEntity();
        if (renderViewEntity != null && RotationUtils.mc.theWorld != null) {
            double blockReachDistance = range;
            Vec3 eyePosition = renderViewEntity.getPositionEyes(1.0f);
            float yawCos = MathHelper.cos(-yaw.floatValue() * ((float)Math.PI / 180) - new Double(Math.PI).floatValue());
            float yawSin = MathHelper.sin(-yaw.floatValue() * ((float)Math.PI / 180) - new Double(Math.PI).floatValue());
            float pitchCos = -MathHelper.cos(-pitch.floatValue() * ((float)Math.PI / 180));
            float pitchSin = MathHelper.sin(-pitch.floatValue() * ((float)Math.PI / 180));
            Vec3 entityLook = new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
            Vec3 vector = eyePosition.addVector(entityLook.xCoord * blockReachDistance, entityLook.yCoord * blockReachDistance, entityLook.zCoord * blockReachDistance);
            List<Entity> entityList = RotationUtils.mc.theWorld.getEntitiesInAABBexcluding(renderViewEntity, renderViewEntity.getEntityBoundingBox().expand(entityLook.xCoord * blockReachDistance, entityLook.yCoord * blockReachDistance, entityLook.zCoord * blockReachDistance).expand(1.0, 1.0, 1.0), entity -> entity != null && (entity.getEntityId() != RotationUtils.mc.thePlayer.getEntityId() || !((EntityPlayer)entity).isSpectator()) && entity.canBeCollidedWith());
            Entity pointedEntity = null;
            for (Entity entity2 : entityList) {
                double eyeDistance;
                double collisionBorderSize = entity2.getCollisionBorderSize();
                AxisAlignedBB axisAlignedBB = entity2.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                MovingObjectPosition movingObjectPosition = axisAlignedBB.calculateIntercept(eyePosition, vector);
                if (axisAlignedBB.isVecInside(eyePosition)) {
                    if (!(blockReachDistance >= 0.0)) continue;
                    pointedEntity = entity2;
                    blockReachDistance = 0.0;
                    continue;
                }
                if (movingObjectPosition == null || !((eyeDistance = eyePosition.distanceTo(movingObjectPosition.hitVec)) < blockReachDistance) && blockReachDistance != 0.0) continue;
                if (entity2 == renderViewEntity.ridingEntity && !renderViewEntity.canBeCollidedWith()) {
                    if (blockReachDistance != 0.0) continue;
                    pointedEntity = entity2;
                    continue;
                }
                pointedEntity = entity2;
                blockReachDistance = eyeDistance;
            }
            return pointedEntity;
        }
        return null;
    }

    public static MovingObjectPosition rayCast(Vector2f rotation, double range) {
        return RotationUtils.rayCast(rotation, range, 0.0f);
    }

    public static MovingObjectPosition rayCast(Vector2f rotation, double range, float expand) {
        return RotationUtils.rayCast(rotation, range, expand, RotationUtils.mc.thePlayer);
    }

    public static MovingObjectPosition rayCast(Vector2f rotation, double range, float expand, Entity entity) {
        float partialTicks = RotationUtils.mc.timer.renderPartialTicks;
        if (entity != null && RotationUtils.mc.theWorld != null) {
            MovingObjectPosition objectMouseOver = entity.rayTraceCustom(range, rotation.x, rotation.y);
            double d1 = range;
            Vec3 vec3 = entity.getPositionEyes(partialTicks);
            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }
            Vec3 vec31 = RotationUtils.mc.thePlayer.getVectorForRotation(rotation.y, rotation.x);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
            Entity pointedEntity = null;
            Vec3 vec33 = null;
            float f = 1.0f;
            List<Entity> list = RotationUtils.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(1.0, 1.0, 1.0), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;
            for (Entity entity1 : list) {
                double d3;
                float f1 = entity1.getCollisionBorderSize() + expand;
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (!(d2 >= 0.0)) continue;
                    pointedEntity = entity1;
                    vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                    d2 = 0.0;
                    continue;
                }
                if (movingobjectposition == null || !((d3 = vec3.distanceTo(movingobjectposition.hitVec)) < d2) && d2 != 0.0) continue;
                pointedEntity = entity1;
                vec33 = movingobjectposition.hitVec;
                d2 = d3;
            }
            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
            }
            return objectMouseOver;
        }
        return null;
    }

    public static Vector2f calculate(Vector3d from, Vector3d to) {
        Vector3d diff = to.subtract(from);
        double distance = Math.hypot(diff.getX(), diff.getZ());
        float yaw = (float)(MathHelper.atan2(diff.getZ(), diff.getX()) * 57.2957763671875) - 90.0f;
        float pitch = (float)(-(MathHelper.atan2(diff.getY(), distance) * 57.2957763671875));
        return new Vector2f(yaw, pitch);
    }

    public static Vector2f calculate(Entity entity) {
        return RotationUtils.calculate(entity.getCustomPositionVector().add(0.0, Math.max(0.0, Math.min(RotationUtils.mc.thePlayer.posY - entity.posY + (double)RotationUtils.mc.thePlayer.getEyeHeight(), (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * 0.9)), 0.0));
    }

    public static Vector2f calculate(Entity entity, boolean adaptive, double range) {
        Vector2f normalRotations = RotationUtils.calculate(entity);
        if (!adaptive || RotationUtils.rayCast((Vector2f)normalRotations, (double)range).typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            return normalRotations;
        }
        for (double yPercent = 1.0; yPercent >= 0.0; yPercent -= 0.25) {
            for (double xPercent = 1.0; xPercent >= -0.5; xPercent -= 0.5) {
                for (double zPercent = 1.0; zPercent >= -0.5; zPercent -= 0.5) {
                    Vector2f adaptiveRotations = RotationUtils.calculate(entity.getCustomPositionVector().add((entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX) * xPercent, (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * yPercent, (entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ) * zPercent));
                    if (RotationUtils.rayCast((Vector2f)adaptiveRotations, (double)range).typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) continue;
                    return adaptiveRotations;
                }
            }
        }
        return normalRotations;
    }

    public static Vector2f calculate(Vec3 to, EnumFacing enumFacing) {
        return RotationUtils.calculate(new Vector3d(to.xCoord, to.yCoord, to.zCoord), enumFacing);
    }

    public static Vector2f calculate(Vec3 to) {
        return RotationUtils.calculate(RotationUtils.mc.thePlayer.getCustomPositionVector().add(0.0, RotationUtils.mc.thePlayer.getEyeHeight(), 0.0), new Vector3d(to.xCoord, to.yCoord, to.zCoord));
    }

    public static Vector2f calculate(Vector3d to) {
        return RotationUtils.calculate(RotationUtils.mc.thePlayer.getCustomPositionVector().add(0.0, RotationUtils.mc.thePlayer.getEyeHeight(), 0.0), to);
    }

    public static Vector2f calculate(Vector3d position, EnumFacing enumFacing) {
        double x = position.getX() + 0.5;
        double y = position.getY() + 0.5;
        double z = position.getZ() + 0.5;
        return RotationUtils.calculate(new Vector3d(x += (double)enumFacing.getDirectionVec().getX() * 0.5, y += (double)enumFacing.getDirectionVec().getY() * 0.5, z += (double)enumFacing.getDirectionVec().getZ() * 0.5));
    }
}

