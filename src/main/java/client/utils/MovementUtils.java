/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.event.events.MotionEvent;
import client.event.events.MoveEvent;
import client.event.events.MoveInputEvent;
import client.event.events.PacketReceiveSyncEvent;
import client.event.events.PacketSendEvent;
import client.utils.ClientUtils;
import client.utils.MinecraftInstance;
import client.utils.PacketUtils;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public final class MovementUtils
extends MinecraftInstance {
    public static final MovementUtils INSTANCE = new MovementUtils();
    public static Boolean pre = false;
    public static boolean lastOnGround = false;
    public static boolean cancelMove = false;
    private static double motionX = 0.0;
    private static double motionY = 0.0;
    private static double motionZ = 0.0;
    private static float fallDistance = 0.0f;
    private static int moveTicks = 0;
    private static boolean changeState = false;
    private static double posX = 0.0;
    private static double posY = 0.0;
    private static double posZ = 0.0;
    private static double lastPosX = 0.0;
    private static double lastPosY = 0.0;
    private static double lastPosZ = 0.0;

    public static float getSpeed() {
        return (float)Math.sqrt(MovementUtils.mc.thePlayer.motionX * MovementUtils.mc.thePlayer.motionX + MovementUtils.mc.thePlayer.motionZ * MovementUtils.mc.thePlayer.motionZ);
    }

    public static void strafe() {
        MovementUtils.strafe(MovementUtils.getSpeed());
    }

    public static boolean isMove() {
        return MovementUtils.mc.thePlayer != null && (MovementUtils.mc.thePlayer.movementInput.moveForward != 0.0f || MovementUtils.mc.thePlayer.movementInput.moveStrafe != 0.0f);
    }

    public static boolean hasMotion() {
        return MovementUtils.mc.thePlayer.motionX != 0.0 && MovementUtils.mc.thePlayer.motionZ != 0.0 && MovementUtils.mc.thePlayer.motionY != 0.0;
    }

    public static void strafe(float speed) {
        if (!MovementUtils.isMove()) {
            return;
        }
        double yaw = MovementUtils.getDirection();
        MovementUtils.mc.thePlayer.motionX = -Math.sin(yaw) * (double)speed;
        MovementUtils.mc.thePlayer.motionZ = Math.cos(yaw) * (double)speed;
    }

    public static void forward(double length) {
        double yaw = Math.toRadians(MovementUtils.mc.thePlayer.rotationYaw);
        MovementUtils.mc.thePlayer.setPosition(MovementUtils.mc.thePlayer.posX + -Math.sin(yaw) * length, MovementUtils.mc.thePlayer.posY, MovementUtils.mc.thePlayer.posZ + Math.cos(yaw) * length);
    }

    public static double getDirection() {
        float rotationYaw = MovementUtils.mc.thePlayer.rotationYaw;
        if (MovementUtils.mc.thePlayer.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (MovementUtils.mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (MovementUtils.mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (MovementUtils.mc.thePlayer.moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (MovementUtils.mc.thePlayer.moveStrafing < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    public static float[] getRotationsBlock(BlockPos block, EnumFacing face) {
        double x = (double)block.getX() + 0.5 - MovementUtils.mc.thePlayer.posX + (double)face.getFrontOffsetX() / 2.0;
        double z = (double)block.getZ() + 0.5 - MovementUtils.mc.thePlayer.posZ + (double)face.getFrontOffsetZ() / 2.0;
        double y = (double)block.getY() + 0.5;
        double d1 = MovementUtils.mc.thePlayer.posY + (double)MovementUtils.mc.thePlayer.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(Math.atan2(d1, d3) * 180.0 / Math.PI);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[]{yaw, pitch};
    }

    public static void cancelMove() {
        if (MovementUtils.mc.thePlayer == null) {
            return;
        }
        if (cancelMove) {
            return;
        }
        cancelMove = true;
        motionX = MovementUtils.mc.thePlayer.motionX;
        motionY = MovementUtils.mc.thePlayer.motionY;
        motionZ = MovementUtils.mc.thePlayer.motionZ;
        fallDistance = MovementUtils.mc.thePlayer.fallDistance;
    }

    public static void resetMove() {
        cancelMove = false;
        moveTicks = 0;
    }

    public static boolean isMoveKeybind() {
        GameSettings gameSettings = MovementUtils.mc.gameSettings;
        return gameSettings.keyBindForward.isKeyDown() || gameSettings.keyBindBack.isKeyDown() || gameSettings.keyBindLeft.isKeyDown() || gameSettings.keyBindRight.isKeyDown();
    }

    public static double direction(float rotationYaw, double moveForward, double moveStrafing) {
        if (moveForward < 0.0) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (moveForward < 0.0) {
            forward = -0.5f;
        } else if (moveForward > 0.0) {
            forward = 0.5f;
        }
        if (moveStrafing > 0.0) {
            rotationYaw -= 90.0f * forward;
        }
        if (moveStrafing < 0.0) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    public static void fixMovement(MoveInputEvent event, float targetYaw, float motionYaw) {
        float forward = event.getForward();
        float strafe = event.getStrafe();
        double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MovementUtils.direction(motionYaw, forward, strafe)));
        if (forward == 0.0f && strafe == 0.0f) {
            return;
        }
        float closestForward = 0.0f;
        float closestStrafe = 0.0f;
        float closestDifference = Float.MAX_VALUE;
        for (float predictedForward = -1.0f; predictedForward <= 1.0f; predictedForward += 1.0f) {
            for (float predictedStrafe = -1.0f; predictedStrafe <= 1.0f; predictedStrafe += 1.0f) {
                double predictedAngle;
                double difference;
                if (predictedStrafe == 0.0f && predictedForward == 0.0f || !((difference = Math.abs(angle - (predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MovementUtils.direction(targetYaw, predictedForward, predictedStrafe)))))) < (double)closestDifference)) continue;
                closestDifference = (float)difference;
                closestForward = predictedForward;
                closestStrafe = predictedStrafe;
            }
        }
        event.setForward(closestForward);
        event.setStrafe(closestStrafe);
    }

    public void onMotion(MotionEvent event) {
        if (!event.isPre()) {
            pre = false;
        }
    }

    public void afterUpdate() {
    }

    public void onUpdate() {
        if (cancelMove) {
            if (moveTicks > 0) {
                return;
            }
            MovementUtils.mc.thePlayer.motionX = motionX;
            MovementUtils.mc.thePlayer.motionZ = motionZ;
            MovementUtils.mc.thePlayer.motionY = motionY;
            MovementUtils.mc.thePlayer.fallDistance = fallDistance;
        }
    }

    public void onPacket(PacketSendEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer && cancelMove) {
            if (moveTicks > 0) {
                lastPosZ = posZ;
                lastPosY = posY;
                lastPosX = posX;
                posY = MovementUtils.mc.thePlayer.posY;
                posZ = MovementUtils.mc.thePlayer.posZ;
                posX = MovementUtils.mc.thePlayer.posX;
                motionX = MovementUtils.mc.thePlayer.motionX;
                motionZ = MovementUtils.mc.thePlayer.motionZ;
                motionY = MovementUtils.mc.thePlayer.motionY;
                fallDistance = MovementUtils.mc.thePlayer.fallDistance;
                --moveTicks;
                return;
            }
            if (event.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
                // empty if block
            }
        }
    }

    public void onTick() {
        if (ClientUtils.nullCheck()) {
            MovementUtils.resetMove();
            return;
        }
        pre = true;
        if (cancelMove) {
            changeState = false;
            if (PacketUtils.noMovePackets >= 20) {
                MovementUtils.mc.thePlayer.motionX = motionX;
                MovementUtils.mc.thePlayer.motionY = motionY;
                MovementUtils.mc.thePlayer.motionZ = motionZ;
                MovementUtils.mc.thePlayer.fallDistance = fallDistance;
            }
            if (++moveTicks > 0) {
                return;
            }
            MovementUtils.mc.thePlayer.motionX = motionX;
            MovementUtils.mc.thePlayer.motionZ = motionZ;
            MovementUtils.mc.thePlayer.motionY = motionY;
            MovementUtils.mc.thePlayer.fallDistance = fallDistance;
        }
    }

    public void onMove(MoveEvent event) {
        if (cancelMove) {
            if (moveTicks > 0) {
                return;
            }
            event.cancelEvent();
        }
    }

    public void onPacketReceive(PacketReceiveSyncEvent event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity && cancelMove && ((S12PacketEntityVelocity)event.getPacket()).getEntityID() == MovementUtils.mc.thePlayer.getEntityId()) {
            MovementUtils.mc.thePlayer.motionX = motionX;
            MovementUtils.mc.thePlayer.motionY = motionY;
            MovementUtils.mc.thePlayer.motionZ = motionZ;
            MovementUtils.mc.thePlayer.fallDistance = fallDistance;
            ++moveTicks;
        }
    }
}

