/*
 * Decompiled with CFR 0.151.
 */
package client.module;

import client.Client;
import client.event.Event;
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
import client.module.Module;
import client.module.ModuleType;
import client.module.modules.combat.AntiFireBall;
import client.module.modules.combat.AutoPot;
import client.module.modules.combat.AutoProjectile;
import client.module.modules.combat.Backtrack;
import client.module.modules.combat.BlockHit;
import client.module.modules.combat.BlockingGun;
import client.module.modules.combat.KillAura;
import client.module.modules.combat.Velocity;
import client.module.modules.misc.AntiBot;
import client.module.modules.misc.AntiExploit;
import client.module.modules.misc.Blink;
import client.module.modules.misc.ClientSettings;
import client.module.modules.misc.DelayRemover;
import client.module.modules.misc.Disabler;
import client.module.modules.misc.FakeLag;
import client.module.modules.misc.Gapple;
import client.module.modules.misc.ItemManager;
import client.module.modules.misc.NoRotSet;
import client.module.modules.misc.Teams;
import client.module.modules.misc.Test;
import client.module.modules.movement.AutoMLG;
import client.module.modules.movement.ChipsCatNoSlow;
import client.module.modules.movement.Clip;
import client.module.modules.movement.FastLadder;
import client.module.modules.movement.FireBallJump;
import client.module.modules.movement.Fly;
import client.module.modules.movement.InvMove;
import client.module.modules.movement.NoGround;
import client.module.modules.movement.NoLiquid;
import client.module.modules.movement.NoSlow;
import client.module.modules.movement.NoWeb;
import client.module.modules.movement.Speed;
import client.module.modules.movement.Sprint;
import client.module.modules.movement.Stuck;
import client.module.modules.render.Animation;
import client.module.modules.render.AttackEffect;
import client.module.modules.render.BlockESP;
import client.module.modules.render.ESP;
import client.module.modules.render.EntityBody;
import client.module.modules.render.FullBright;
import client.module.modules.render.ItemDrop;
import client.module.modules.render.NoHurtCam;
import client.module.modules.render.PlayerServerRotations;
import client.module.modules.render.WorldSettings;
import client.module.modules.visual.ArrayList;
import client.module.modules.visual.ClickGUI;
import client.module.modules.visual.ClientColor;
import client.module.modules.visual.EffectRender;
import client.module.modules.visual.Logo;
import client.module.modules.visual.Noti;
import client.module.modules.visual.OldAndroidMotionBlur;
import client.module.modules.visual.SessionHUD;
import client.module.modules.visual.TargetHUD;
import client.module.modules.world.AutoContainer;
import client.module.modules.world.AutoPearl;
import client.module.modules.world.AutoRespawn;
import client.module.modules.world.AutoTool;
import client.module.modules.world.AutoWeapon;
import client.module.modules.world.BalanceTimer;
import client.module.modules.world.BedFucker;
import client.module.modules.world.FastBreak;
import client.module.modules.world.Nofall;
import client.module.modules.world.Scaffold;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    public final Map<Class<?>, Module> moduleMap = new HashMap();
    public final List<Module> modules = new java.util.ArrayList<Module>();

    private ModuleManager() {
        this.loadModules();
    }

    private void loadModule(Module module) {
        this.moduleMap.put(module.getClass(), module);
        this.modules.add(module);
    }

    private void loadModules() {
        this.loadModule(new OldAndroidMotionBlur());
        this.loadModule(new FullBright());
        this.loadModule(new TargetHUD());
        this.loadModule(new SessionHUD());
        this.loadModule(new Logo());
        this.loadModule(ClientSettings.INSTANCE);
        this.loadModule(new Blink());
        this.loadModule(new ClickGUI());
        this.loadModule(new AntiFireBall());
        this.loadModule(new ArrayList());
        this.loadModule(new ESP());
        this.loadModule(new Nofall());
        this.loadModule(new Fly());
        this.loadModule(new Sprint());
        this.loadModule(new KillAura());
        this.loadModule(Test.INSTANCE);
        this.loadModule(new NoSlow());
        this.loadModule(new Scaffold());
        this.loadModule(new Velocity());
        this.loadModule(BlockHit.INSTANCE);
        this.loadModule(new AutoPearl());
        this.loadModule(new FastBreak());
        this.loadModule(new AntiExploit());
        this.loadModule(new Teams());
        this.loadModule(new InvMove());
        this.loadModule(new DelayRemover());
        this.loadModule(new BedFucker());
        this.loadModule(new BalanceTimer());
        this.loadModule(new Gapple());
        this.loadModule(new ItemManager());
        this.loadModule(new Disabler());
        this.loadModule(new Animation());
        this.loadModule(Stuck.INSTANCE);
        this.loadModule(new ChipsCatNoSlow());
        this.loadModule(new AutoProjectile());
        this.loadModule(new Speed());
        this.loadModule(new PlayerServerRotations());
        this.loadModule(new Clip());
        this.loadModule(new WorldSettings());
        this.loadModule(new EntityBody());
        this.loadModule(new ItemDrop());
        this.loadModule(new AttackEffect());
        this.loadModule(new NoGround());
        this.loadModule(new AutoMLG());
        this.loadModule(new AutoTool());
        this.loadModule(new AutoWeapon());
        this.loadModule(new Backtrack());
        this.loadModule(new NoWeb());
        this.loadModule(new AutoContainer());
        this.loadModule(new AutoRespawn());
        this.loadModule(Noti.INSTANCE);
        this.loadModule(NoRotSet.INSTANCE);
        this.loadModule(AntiBot.INSTANCE);
        this.loadModule(ClientColor.INSTANCE);
        this.loadModule(new FakeLag());
        this.loadModule(new NoHurtCam());
        this.loadModule(new EffectRender());
        this.loadModule(new FastLadder());
        this.loadModule(new AutoPot());
        this.loadModule(new BlockESP());
        this.loadModule(new BlockingGun());
        this.loadModule(new FireBallJump());
        this.loadModule(NoLiquid.INSTANCE);
        System.out.println("ModuleManager loaded!");
    }

    private void callEvent(Consumer<Module> handler) {
        for (Module module : this.modules) {
            try {
                if (!module.handleEvents()) continue;
                handler.accept(module);
            }
            catch (Exception e) {
                System.out.println("Exception while executing module " + module.name + ": ");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private <T extends Event> void callEvent(BiConsumer<Module, T> handler, T event) {
        for (Module module : this.modules) {
            try {
                if (!module.handleEvents()) continue;
                handler.accept(module, (event));
            }
            catch (Exception e) {
                System.out.println("Exception while executing module " + module.name + ": ");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void onModuleInited() {
        this.callEvent(Module::onModulesInited);
    }

    public void onTick() {
        this.callEvent(Module::onTick);
    }

    public void onPreUpdate() {
        this.callEvent(Module::onPreUpdate);
    }

    public void onUpdate() {
        this.callEvent(Module::onUpdate);
    }

    public void onRespawn(RespawnEvent event) {
        this.callEvent(Module::onRespawn, event);
    }

    public void onMessageSend(MessageSendEvent event) {
        this.callEvent(Module::onMessageSend, event);
    }

    public void onSlowDown(SlowDownEvent event) {
        this.callEvent(Module::onSlowDown, event);
    }

    public void onMovementInput(MoveInputEvent event) {
        this.callEvent(Module::onMovementInput, event);
    }

    public void onMotion(MotionEvent event) {
        this.callEvent(Module::onMotion, event);
    }

    public void onPacketReceiveAsync(PacketReceiveAsyncEvent event) {
        this.callEvent(Module::onPacketReceiveAsync, event);
    }

    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        this.callEvent(Module::onPacketReceiveSync, event);
    }

    public void onPacketSend(PacketSendEvent event) {
        this.callEvent(Module::onPacketSend, event);
    }

    public void onPacketSendAsync(PacketSendAsyncEvent event) {
        this.callEvent(Module::onPacketSendAsync, event);
    }

    public void onStrafe(StrafeEvent event) {
        this.callEvent(Module::onStrafe, event);
    }

    public void onRender3D(Render3DEventBeforeHand event) {
        this.callEvent(Module::onRender3D, event);
    }

    public void onRender3D(Render3DEventAfterHand event) {
        this.callEvent(Module::onRender3D, event);
    }

    public void onPostUpdate() {
        this.callEvent(Module::onPostUpdate);
    }

    public void onWorldLoad() {
        this.callEvent(Module::onWorldLoad);
    }

    public void onRender2D(Render2DEvent event) {
        this.callEvent(Module::onRender2D, event);
    }

    public void onJump(JumpEvent event) {
        this.callEvent(Module::onJump, event);
    }

    public void onKey(KeyEvent event) {
        for (Module module : this.modules) {
            try {
                if (module.handleEvents()) {
                    module.onKey(event);
                }
                if (module.key != event.keyCode) continue;
                module.toggle();
            }
            catch (Exception e) {
                System.out.println("Exception while executing module " + module.name + ": ");
                e.printStackTrace();
            }
        }
    }

    public static Module getModuleByString(String string) {
        Module module = null;
        for (Module mod : Client.moduleManager.modules) {
            if (!mod.name.equalsIgnoreCase(string)) continue;
            module = mod;
        }
        return module;
    }

    public static Module getModuleByClass(Class clzz) {
        Module module = null;
        for (Module mod : Client.moduleManager.modules) {
            if (!mod.getClass().getName().equalsIgnoreCase(clzz.getName())) continue;
            module = mod;
        }
        return module;
    }

    public static List<Module> getModulesInType(ModuleType t) {
        java.util.ArrayList<Module> output = new java.util.ArrayList<Module>();
        for (Module m : Client.moduleManager.modules) {
            if (m.getType() != t) continue;
            output.add(m);
        }
        return output;
    }
}

