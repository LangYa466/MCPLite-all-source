/*
 * Decompiled with CFR 0.151.
 */
package client.event;

import client.Client;
import client.event.events.JumpEvent;
import client.event.events.KeyEvent;
import client.event.events.MessageSendEvent;
import client.event.events.MotionEvent;
import client.event.events.MoveEvent;
import client.event.events.MoveInputEvent;
import client.event.events.PacketReceiveAsyncEvent;
import client.event.events.PacketReceiveSyncEvent;
import client.event.events.PacketSendAsyncEvent;
import client.event.events.PacketSendEvent;
import client.event.events.Render2DEvent;
import client.event.events.Render3DEventAfterHand;
import client.event.events.Render3DEventBeforeHand;
import client.event.events.RespawnEvent;
import client.event.events.SlowDownEvent;
import client.event.events.StrafeEvent;
import client.utils.BlinkUtils;
import client.utils.FallDistanceComponent;
import client.utils.MovementUtils;
import client.utils.rotation.RotationSetter;

public class EventManager {
    public static final EventManager INSTANCE = new EventManager();

    private EventManager() {
    }

    public void onTick() {
        MovementUtils.INSTANCE.onTick();
        Client.moduleManager.onTick();
        BlinkUtils.INSTANCE.onTick();
    }

    public void onUpdate() {
        Client.moduleManager.onUpdate();
    }

    public void onPreUpdate() {
        Client.moduleManager.onPreUpdate();
    }

    public void onMessageSend(MessageSendEvent event) {
        Client.moduleManager.onMessageSend(event);
        Client.commandManager.commandReceive(event);
    }

    public void onRespawn(RespawnEvent event) {
        Client.moduleManager.onRespawn(event);
    }

    public void onSlowDown(SlowDownEvent event) {
        Client.moduleManager.onSlowDown(event);
    }

    public void onMovementInput(MoveInputEvent event) {
        Client.moduleManager.onMovementInput(event);
        RotationSetter.onMoveInput(event);
    }

    public void onMotion(MotionEvent event) {
        MovementUtils.INSTANCE.onMotion(event);
        Client.moduleManager.onMotion(event);
        RotationSetter.onMotion(event);
        FallDistanceComponent.INSTANCE.onMotion(event);
    }

    public void onPacketReceiveAsync(PacketReceiveAsyncEvent event) {
        Client.moduleManager.onPacketReceiveAsync(event);
    }

    public void onWorldLoad() {
        Client.moduleManager.onWorldLoad();
    }

    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        MovementUtils.INSTANCE.onPacketReceive(event);
        Client.moduleManager.onPacketReceiveSync(event);
    }

    public void onPacketSend(PacketSendEvent event) {
        MovementUtils.INSTANCE.onPacket(event);
        Client.moduleManager.onPacketSend(event);
        RotationSetter.onPacket(event);
    }

    public void onPacketSendAsync(PacketSendAsyncEvent event) {
        Client.moduleManager.onPacketSendAsync(event);
    }

    public void onKey(KeyEvent event) {
        if (!event.hasScreen) {
            Client.moduleManager.onKey(event);
        } else {
            Client.commandManager.onKey(event.keyCode);
        }
    }

    public void onRender3D(Render3DEventBeforeHand event) {
        Client.moduleManager.onRender3D(event);
    }

    public void onRender3D(Render3DEventAfterHand event) {
        Client.moduleManager.onRender3D(event);
    }

    public void onRender2D(Render2DEvent event) {
        Client.moduleManager.onRender2D(event);
    }

    public void onStrafe(StrafeEvent event) {
        Client.moduleManager.onStrafe(event);
        RotationSetter.onStrafe(event);
    }

    public void onMove(MoveEvent moveEvent) {
        MovementUtils.INSTANCE.onMove(moveEvent);
    }

    public void onJump(JumpEvent event) {
        Client.moduleManager.onJump(event);
        RotationSetter.onJump(event);
    }
}

