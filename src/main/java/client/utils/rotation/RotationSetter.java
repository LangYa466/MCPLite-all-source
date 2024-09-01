/*
 * Decompiled with CFR 0.151.
 */
package client.utils.rotation;

import client.event.events.JumpEvent;
import client.event.events.MotionEvent;
import client.event.events.MoveInputEvent;
import client.event.events.PacketSendEvent;
import client.event.events.StrafeEvent;
import client.module.modules.misc.ClientSettings;
import client.utils.MovementUtils;
import client.utils.rotation.RotationPriority;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.util.vector.Vector2f;

public class RotationSetter {
    public static Vector2f targetRotation;
    private static int maxKeepTicks;
    public static Vector2f lastPacketRotation;
    public static Vector2f packetRotation;
    private static int keep;
    private static int currentPriority;
    private static boolean follow;

    public static void setCurrentPriority(RotationPriority rotationPriority) {
        currentPriority = rotationPriority.ordinal();
    }

    public static void setRotation(Vector2f silentRotation, int keeptick) {
        if (currentPriority > RotationPriority.NORMAL.ordinal()) {
            return;
        }
        targetRotation = silentRotation;
        maxKeepTicks = keeptick;
        keep = 0;
        follow = false;
        currentPriority = RotationPriority.NORMAL.ordinal();
    }

    public static void setRotation(Vector2f silentRotation, int keeptick, RotationPriority priority) {
        if (currentPriority > priority.ordinal()) {
            return;
        }
        targetRotation = silentRotation;
        maxKeepTicks = keeptick;
        keep = 0;
        follow = false;
        currentPriority = priority.ordinal();
    }

    public static void setRotation(Vector2f silentRotation, int keeptick, boolean follow) {
        if (currentPriority > RotationPriority.NORMAL.ordinal()) {
            return;
        }
        targetRotation = silentRotation;
        maxKeepTicks = keeptick;
        keep = 0;
        RotationSetter.follow = follow;
        currentPriority = RotationPriority.NORMAL.ordinal();
    }

    public static void setRotation(Vector2f silentRotation, int keeptick, boolean strafeSilent, RotationPriority priority) {
        if (currentPriority > priority.ordinal()) {
            return;
        }
        targetRotation = silentRotation;
        maxKeepTicks = keeptick;
        keep = 0;
        follow = strafeSilent;
        currentPriority = priority.ordinal();
    }

    public static void setFollow(boolean follow) {
        if (targetRotation != null) {
            RotationSetter.follow = follow;
        }
    }

    public static void reset() {
        targetRotation = null;
        maxKeepTicks = 0;
        keep = 0;
        follow = false;
        currentPriority = -114514;
    }

    public static void onMotion(MotionEvent event) {
        if (targetRotation != null) {
            event.setYaw(RotationSetter.targetRotation.x);
            event.setPitch(RotationSetter.targetRotation.y);
            if (!event.isPre() && ++keep > maxKeepTicks) {
                RotationSetter.reset();
            }
        }
    }

    public static void onStrafe(StrafeEvent event) {
        if (targetRotation != null && ClientSettings.INSTANCE.moveFix) {
            event.yaw = RotationSetter.targetRotation.x;
        }
    }

    public static void onMoveInput(MoveInputEvent event) {
        if (!follow && targetRotation != null && ClientSettings.INSTANCE.moveFix) {
            MovementUtils.fixMovement(event, targetRotation.getX(), Minecraft.getMinecraft().thePlayer.rotationYaw);
        }
    }

    public static void onPacket(PacketSendEvent event) {
        C03PacketPlayer packetPlayer;
        if (event.getPacket() instanceof C03PacketPlayer && (packetPlayer = (C03PacketPlayer)event.getPacket()).getRotating()) {
            lastPacketRotation = packetRotation;
            packetRotation = new Vector2f(packetPlayer.getYaw(), packetPlayer.getPitch());
        }
    }

    public static void onJump(JumpEvent event) {
        if (targetRotation != null && ClientSettings.INSTANCE.moveFix) {
            event.setYaw(RotationSetter.targetRotation.x);
        }
    }

    public static Vector2f getCurrentRotation() {
        return targetRotation != null ? targetRotation : new Vector2f(Minecraft.getMinecraft().thePlayer.rotationYaw, Minecraft.getMinecraft().thePlayer.rotationPitch);
    }

    static {
        lastPacketRotation = new Vector2f(0.0f, 0.0f);
        packetRotation = new Vector2f(0.0f, 0.0f);
        currentPriority = 0;
    }
}

