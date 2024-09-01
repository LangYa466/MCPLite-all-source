/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.misc;

import client.event.events.MotionEvent;
import client.event.events.PacketReceiveSyncEvent;
import client.event.events.PacketSendEvent;
import client.event.events.Render2DEvent;
import client.event.events.Render3DEventAfterHand;
import client.event.events.Render3DEventBeforeHand;
import client.module.Module;
import client.module.ModuleType;
import client.ui.notifi.Notifi;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;

public class Test
extends Module {
    public List<Notifi> notifiList = new ArrayList<Notifi>();
    private int hasMove = 0;
    public static Test INSTANCE = new Test();

    private Test() {
        super("Test", 0, true, ModuleType.MISC);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onPreUpdate() {
    }

    @Override
    public void onMotion(MotionEvent event) {
    }

    @Override
    public void onPostUpdate() {
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
    }

    @Override
    public void onRender3D(Render3DEventBeforeHand event) {
    }

    @Override
    public void onRender2D(Render2DEvent event) {
    }

    @Override
    public void onRender3D(Render3DEventAfterHand event) {
    }

    public Vector2f getCirclePoint(float circleX, float circleY, float radius, int angle) {
        return new Vector2f((float)((double)circleX + Math.cos((double)angle * Math.PI / 180.0) * (double)(radius * 1.001f)), (float)((double)circleY + Math.sin((double)angle * Math.PI / 180.0) * (double)(radius * 1.001f)));
    }
}

