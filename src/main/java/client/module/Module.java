/*
 * Decompiled with CFR 0.151.
 */
package client.module;

import client.Client;
import client.event.events.JumpEvent;
import client.event.events.KeyEvent;
import client.event.events.MessageSendEvent;
import client.event.events.MotionEvent;
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
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.visual.Noti;
import client.ui.notifi.Notifi;
import client.utils.ClientUtils;
import client.utils.MinecraftInstance;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public abstract class Module
extends MinecraftInstance {
    public ModuleType moduleType;
    private boolean state = false;
    @Settings
    public boolean hide = false;
    public int key;
    public String name;

    public String getTag() {
        return null;
    }

    public int getNameLong() {
        return this.getTag() != null ? this.name.length() + this.getTag().length() : this.name.length();
    }

    public boolean getState() {
        return this.state;
    }

    public void setKey(int keyNone) {
        this.key = keyNone;
    }

    ModuleType getType() {
        return this.moduleType;
    }

    public void setState(boolean state) {
        if (Client.configManager != null) {
            Client.configManager.saveConfig();
        }
        if (this.state == state) {
            return;
        }
        if (!ClientUtils.nullCheck()) {
            if (state) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.click"), 5.0f));
                Noti.INSTANCE.notifiList.add(new Notifi("Enabled " + this.name, Noti.INSTANCE.moduleMoveX));
                this.onEnable();
            } else {
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.click"), -5.0f));
                Noti.INSTANCE.notifiList.add(new Notifi("Disabled " + this.name, Noti.INSTANCE.moduleMoveX));
                this.onDisable();
            }
        }
        this.state = state;
    }

    public void toggle() {
        this.setState(!this.state);
    }

    public boolean handleEvents() {
        return this.state;
    }

    public void onEnable() {
    }

    public void HoldTicks() {
    }

    public void onDisable() {
    }

    public void onTick() {
    }

    public void onPreUpdate() {
    }

    public void onUpdate() {
    }

    public void onRespawn(RespawnEvent event) {
    }

    public void onMessageSend(MessageSendEvent event) {
    }

    public void onSlowDown(SlowDownEvent event) {
    }

    public void onMovementInput(MoveInputEvent event) {
    }

    public void onMotion(MotionEvent event) {
    }

    public void onPacketReceiveAsync(PacketReceiveAsyncEvent event) {
    }

    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
    }

    public void onPacketSend(PacketSendEvent event) {
    }

    public void onModulesInited() {
    }

    public void onPacketSendAsync(PacketSendAsyncEvent event) {
    }

    public void onJump(JumpEvent jumpEvent) {
    }

    public void onKey(KeyEvent event) {
    }

    public void onRender3D(Render3DEventBeforeHand event) {
    }

    public void onRender3D(Render3DEventAfterHand event) {
    }

    public void onStrafe(StrafeEvent event) {
    }

    public void onRender2D(Render2DEvent event) {
    }

    protected Module(String name, int key, boolean defaultState, ModuleType moduleType) {
        this.name = name;
        this.key = key;
        this.setState(defaultState);
        this.moduleType = moduleType;
    }

    protected Module(String name, int key, ModuleType moduleType) {
        this.name = name;
        this.key = key;
        this.moduleType = moduleType;
    }

    public void onPostUpdate() {
    }

    public void onWorldLoad() {
    }

    public ArrayList<Field> getBooleans() {
        ArrayList<Field> booleans = new ArrayList<Field>();
        for (Field fields : this.getFields()) {
            if (fields.getType() != Boolean.class && fields.getType() != Boolean.TYPE) continue;
            booleans.add(fields);
        }
        return booleans;
    }

    public ArrayList<Field> getFloats() {
        ArrayList<Field> floats = new ArrayList<Field>();
        for (Field fields : this.getFields()) {
            if (fields.getType() != Float.class && fields.getType() != Float.TYPE) continue;
            floats.add(fields);
        }
        return floats;
    }

    public ArrayList<Field> getDoubles() {
        ArrayList<Field> doubles = new ArrayList<Field>();
        for (Field fields : this.getFields()) {
            if (fields.getType() != Double.class && fields.getType() != Double.TYPE) continue;
            doubles.add(fields);
        }
        return doubles;
    }

    public ArrayList<Field> getStrings() {
        ArrayList<Field> string = new ArrayList<Field>();
        for (Field fields : this.getFields()) {
            if (fields.getType() != String.class) continue;
            string.add(fields);
        }
        return string;
    }

    public ArrayList<Field> getInts() {
        ArrayList<Field> ints = new ArrayList<Field>();
        for (Field fields : this.getFields()) {
            if (fields.getType() != Integer.class && fields.getType() != Integer.TYPE) continue;
            ints.add(fields);
        }
        return ints;
    }

    public Field[] getSettings() {
        ArrayList<Field> fields = new ArrayList<Field>();
        for (Field o : this.getClass().getDeclaredFields()) {
            o.setAccessible(true);
            if (!o.isAnnotationPresent(Settings.class)) continue;
            fields.add(o);
        }
        for (Field o : this.getClass().getSuperclass().getDeclaredFields()) {
            if (fields.contains(o) || !o.isAnnotationPresent(Settings.class)) continue;
            fields.add(o);
        }
        Field[] field = new Field[]{};
        return fields.toArray(field);
    }

    public Field[] getFields() {
        ArrayList<Field> fields = new ArrayList<Field>();
        for (Field o2 : this.getClass().getFields()) {
            fields.add(o2);
        }
        Arrays.stream(this.getSettings()).forEach(o -> {
            if (!fields.contains(o)) {
                fields.add((Field)o);
            }
        });
        Field[] field = new Field[]{};
        return fields.toArray(field);
    }
}

