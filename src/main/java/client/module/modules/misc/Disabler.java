/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.misc;

import client.Client;
import client.event.events.PacketReceiveAsyncEvent;
import client.event.events.PacketReceiveSyncEvent;
import client.event.events.PacketSendEvent;
import client.event.events.RespawnEvent;
import client.module.Module;
import client.module.ModuleManager;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.combat.KillAura;
import client.module.modules.misc.ClientSettings;
import client.module.modules.movement.Fly;
import client.module.modules.world.Scaffold;
import client.utils.ClientUtils;
import client.utils.FakeLagUtils;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class Disabler
extends Module {
    public static Disabler INSTANCE;
    @Settings
    private boolean debug = false;
    @Settings
    public boolean repeatC0B = true;
    @Settings
    public boolean repeatC09 = true;
    @Settings
    public boolean uselessC07 = true;
    @Settings
    public boolean grimPost = true;
    @Settings
    public boolean grimNewFastBreak = true;
    @Settings
    public boolean hytFastClick = true;
    @Settings
    public boolean badS2FPacket = true;
    public boolean fuck = false;
    private S12PacketEntityVelocity fuckVelocity = null;
    private int lastSlot = 0;
    private boolean tmj = false;
    private boolean lastSprint = false;
    public boolean startFly = false;
    private LinkedBlockingQueue<Packet<INetHandlerPlayClient>> oldGrimFlyPackets = new LinkedBlockingQueue();
    private List<C09PacketHeldItemChange> changeList = new ArrayList<C09PacketHeldItemChange>();
    private List<Packet<?>> preC07s = new ArrayList();
    private List<Packet<?>> postC07s = new ArrayList();
    private int stopInt = 0;
    public final LinkedBlockingQueue<Packet<INetHandlerPlayServer>> prePackets = new LinkedBlockingQueue();
    public final LinkedBlockingQueue<Packet<INetHandlerPlayServer>> postPackets = new LinkedBlockingQueue();
    private final LinkedBlockingQueue<Packet<INetHandlerPlayServer>> c03Packets = new LinkedBlockingQueue();
    public final LinkedBlockingQueue<Packet<INetHandlerPlayClient>> serverPackets = new LinkedBlockingQueue();
    private final List<Class<?>> specialPackets = Lists.newArrayList(S00PacketKeepAlive.class, C00Handshake.class, S02PacketLoginSuccess.class, C00PacketLoginStart.class, S00PacketDisconnect.class, S40PacketDisconnect.class, S01PacketJoinGame.class);
    public static boolean postState;
    public boolean pass = false;

    public Disabler() {
        super("Disabler", 0, true, ModuleType.MISC);
    }

    @Override
    public void onModulesInited() {
        INSTANCE = (Disabler)Client.moduleManager.moduleMap.get(Disabler.class);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        Packet<INetHandlerPlayServer> packet;
        KillAura killAura;
        if (event.isCancelled()) {
            return;
        }
        if (event.getPacket() instanceof C0EPacketClickWindow && this.debug) {
            ClientUtils.displayChatMessage(((C0EPacketClickWindow)event.getPacket()).slotId + " " + ((C0EPacketClickWindow)event.getPacket()).getMode() + " " + ((C0EPacketClickWindow)event.getPacket()).getSlotId() + "  " + ((C0EPacketClickWindow)event.getPacket()).getUsedButton() + "  " + ((C0EPacketClickWindow)event.getPacket()).actionNumber);
        }
        if ((killAura = (KillAura)ModuleManager.getModuleByClass(KillAura.class)).getState() && killAura.target != null && event.getPacket() instanceof C02PacketUseEntity && (((C02PacketUseEntity)event.getPacket()).getAction() == C02PacketUseEntity.Action.INTERACT || ((C02PacketUseEntity)event.getPacket()).getAction() == C02PacketUseEntity.Action.INTERACT_AT)) {
            event.cancelEvent();
        }
        if (this.grimNewFastBreak && event.getPacket() instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)event.getPacket()).getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
            BlockPos blockPos = ((C07PacketPlayerDigging)event.getPacket()).getPosition();
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
        }
        if (this.repeatC0B && event.getPacket() instanceof C0BPacketEntityAction) {
            packet = (C0BPacketEntityAction)event.getPacket();
            if (((C0BPacketEntityAction)packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                if (!this.fuck) {
                    if (this.lastSprint) {
                        if (!this.tmj) {
                            this.tmj = true;
                            return;
                        }
                        if (this.debug) {
                            ClientUtils.displayChatMessage("Cancelled C0B Start Sprint");
                        }
                        event.cancelEvent();
                    }
                    this.lastSprint = true;
                } else {
                    event.cancelEvent();
                }
                if (!event.isCancelled()) {
                    Disabler.mc.thePlayer.setSprinting(true);
                    Disabler.mc.thePlayer.serverSprintState = true;
                }
            } else if (((C0BPacketEntityAction)packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                if (!this.fuck) {
                    if (!this.lastSprint) {
                        if (!this.tmj) {
                            this.tmj = true;
                            return;
                        }
                        if (this.debug) {
                            ClientUtils.displayChatMessage("Cancelled C0B Stop Sprint");
                        }
                        event.cancelEvent();
                    }
                } else {
                    event.cancelEvent();
                }
                if (!event.isCancelled()) {
                    Disabler.mc.thePlayer.setSprinting(false);
                    Disabler.mc.thePlayer.serverSprintState = false;
                }
                this.lastSprint = false;
            }
        }
        if (this.repeatC09 && event.getPacket() instanceof C09PacketHeldItemChange) {
            packet = (C09PacketHeldItemChange)event.getPacket();
            if (((C09PacketHeldItemChange)packet).slotId == this.lastSlot) {
                event.cancelEvent();
            }
            this.lastSlot = ((C09PacketHeldItemChange)packet).slotId;
        }
    }

    @Override
    public void onWorldLoad() {
        if (this.grimPost) {
            if (!ClientUtils.nullCheck()) {
                Disabler.mc.thePlayer.setSprinting(false);
                Disabler.mc.thePlayer.serverSprintState = false;
            }
            this.lastSprint = false;
            this.tmj = true;
            this.lastSlot = -1;
        }
    }

    @Override
    public void onRespawn(RespawnEvent event) {
        if (this.grimPost) {
            Disabler.mc.thePlayer.setSprinting(true);
            Disabler.mc.thePlayer.serverSprintState = true;
            this.lastSprint = false;
            this.tmj = true;
            this.lastSlot = -1;
            if (this.fuckVelocity != null) {
                mc.getNetHandler().handleEntityVelocity(this.fuckVelocity);
            }
            this.fuck = false;
        }
    }

    @Override
    public void onPacketReceiveAsync(PacketReceiveAsyncEvent event) {
        Fly fly = (Fly)ModuleManager.getModuleByClass(Fly.class);
        if (event.getPacket() instanceof S40PacketDisconnect) {
            System.out.println("KICK");
        }
        if (fly.getState() && fly.isGrimFly) {
            if ((event.getPacket() instanceof S08PacketPlayerPosLook || event.getPacket() instanceof S2BPacketChangeGameState || event.getPacket() instanceof S39PacketPlayerAbilities) && this.debug) {
                ClientUtils.displayChatMessage(event.getPacket().getClass().getSimpleName());
            }
            if (event.getPacket() instanceof S39PacketPlayerAbilities) {
                this.startFly = true;
                ClientUtils.displayChatMessage("StartFly");
            }
        }
        if (this.startFly) {
            if (event.getPacket() instanceof S32PacketConfirmTransaction) {
                event.cancelEvent();
                mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(114, 514, false));
                this.oldGrimFlyPackets.add(event.getPacket());
            }
            if (event.getPacket() instanceof S14PacketEntity && ((S14PacketEntity)event.getPacket()).getEntity(Disabler.mc.theWorld).getEntityId() == Disabler.mc.thePlayer.getEntityId()) {
                event.cancelEvent();
                this.oldGrimFlyPackets.add(event.getPacket());
            }
        }
        if (this.debug && event.getPacket() instanceof S08PacketPlayerPosLook) {
            ClientUtils.displayChatMessage("S08");
        }
        if (event.getPacket() instanceof S2FPacketSetSlot && this.badS2FPacket) {
            S2FPacketSetSlot packet;
            if (this.debug) {
                ClientUtils.displayChatMessage("S2F");
            }
            if ((packet = (S2FPacketSetSlot)event.getPacket()).getSlot() >= 36 && packet.getSlot() <= 44) {
                if (Disabler.mc.thePlayer == null) {
                    return;
                }
                Scaffold scaffold = (Scaffold)Client.moduleManager.moduleMap.get(Scaffold.class);
                if (packet.getStack() == null) {
                    return;
                }
                if (packet.getStack().getItem() instanceof ItemBlock) {
                    return;
                }
                if (!scaffold.getState()) {
                    return;
                }
                int i = 44;
                if (Disabler.mc.thePlayer.inventory.getStackInSlot(8 - (44 - packet.getSlot())) == null) {
                    return;
                }
                ClientUtils.displayChatMessage(Disabler.mc.thePlayer.inventory.getStackInSlot(8 - (44 - packet.getSlot())).getDisplayName());
                if (!(Disabler.mc.thePlayer.inventory.getStackInSlot(8 - (44 - packet.getSlot())).getItem() instanceof ItemBlock)) {
                    return;
                }
                ClientUtils.displayChatMessage("Cancel BAD S2F");
                event.cancelEvent();
            }
        }
    }

    @Override
    public void onTick() {
        Fly fly = (Fly)ModuleManager.getModuleByClass(Fly.class);
        if (!ClientUtils.nullCheck() && this.grimPost && !this.fuck && (Disabler.mc.thePlayer.isDead || Disabler.mc.thePlayer.getHealth() == 0.0f)) {
            this.fuck = true;
        }
        if (this.startFly && !fly.getState()) {
            ++this.stopInt;
            if (this.stopInt > 5) {
                ClientUtils.displayChatMessage("Stop Fly");
                while (!this.oldGrimFlyPackets.isEmpty()) {
                    this.oldGrimFlyPackets.poll().processPacket(mc.getNetHandler());
                }
                this.stopInt = 0;
                this.startFly = false;
            }
        }
    }

    public boolean onCPacket(Packet packet) {
        if (INSTANCE == null || !INSTANCE.getState() || !Disabler.INSTANCE.grimPost || this.pass || this.specialPackets.contains(packet.getClass()) || mc.isSingleplayer()) {
            return true;
        }
        if (packet instanceof C09PacketHeldItemChange) {
            this.changeList.add((C09PacketHeldItemChange)packet);
        }
        if (!(packet instanceof C03PacketPlayer)) {
            if (postState) {
                this.postPackets.add(packet);
                if (packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)packet).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                    this.postC07s.add(packet);
                }
            } else {
                if (packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)packet).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                    this.preC07s.add(packet);
                }
                this.prePackets.add(packet);
            }
        } else {
            this.c03Packets.add(packet);
        }
        return false;
    }

    public boolean onSPacket(Packet packet, INetHandler handler) {
        if (INSTANCE == null || !INSTANCE.getState() || !Disabler.INSTANCE.grimPost || this.pass || this.specialPackets.contains(packet.getClass()) || mc.isSingleplayer()) {
            return true;
        }
        this.serverPackets.add(packet);
        return false;
    }

    public void releaseTick() {
        try {
            Packet<?> packet;
            while (!this.prePackets.isEmpty()) {
                packet = this.prePackets.take();
                if (this.preC07s.contains(packet) && this.preC07s.indexOf(packet) != this.preC07s.size() - 1 && this.uselessC07) continue;
                this.send((Packet<INetHandlerPlayServer>) packet);
            }
            while (!this.c03Packets.isEmpty()) {
                if (this.fuck) {
                    this.c03Packets.take();
                    continue;
                }
                this.send(this.c03Packets.take());
            }
            while (!this.serverPackets.isEmpty()) {
                packet = this.serverPackets.take();
                if (FakeLagUtils.lagging && FakeLagUtils.onCanceling((Packet<INetHandlerPlayClient>) packet)) {
                    return;
                }
                while (!FakeLagUtils.packets.isEmpty() && !FakeLagUtils.lagging) {
                    Packet<INetHandlerPlayClient> playClientPacket = FakeLagUtils.packets.take();
                    FakeLagUtils.onReleasing(playClientPacket);
                    this.handle(playClientPacket);
                }
                if (this.fuck && packet instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity)packet).getEntityID() == Disabler.mc.thePlayer.getEntityId()) {
                    this.fuckVelocity = (S12PacketEntityVelocity)packet;
                }
                this.handle((Packet<INetHandlerPlayClient>) packet);
            }
            while (!this.postPackets.isEmpty()) {
                packet = this.postPackets.take();
                if (this.postC07s.contains(packet) && this.postC07s.indexOf(packet) != this.postC07s.size() - 1 && this.uselessC07) continue;
                this.send((Packet<INetHandlerPlayServer>) packet);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.changeList.clear();
        this.preC07s.clear();
        this.postC07s.clear();
    }

    private void send(Packet<INetHandlerPlayServer> packet) {
        if (mc.getNetHandler() == null) {
            return;
        }
        try {
            this.pass = true;
            if (mc.getNetHandler() != null) {
                if (packet instanceof C09PacketHeldItemChange && ClientSettings.INSTANCE.onHyt) {
                    mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(114, 514, true));
                }
                mc.getNetHandler().addToSendQueue(packet);
            }
            this.pass = false;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            this.pass = false;
        }
    }

    private void handle(Packet<INetHandlerPlayClient> packet) {
        try {
            this.pass = true;
            PacketReceiveSyncEvent packetReceiveSyncEvent = new PacketReceiveSyncEvent(packet);
            Client.eventManager.onPacketReceiveSync(packetReceiveSyncEvent);
            if (packetReceiveSyncEvent.isCancelled()) {
                return;
            }
            packet.processPacket(mc.getNetHandler());
            this.pass = false;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            this.pass = false;
        }
    }

    static {
        postState = false;
    }
}

