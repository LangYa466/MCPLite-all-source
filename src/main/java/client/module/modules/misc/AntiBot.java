/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.misc;

import client.event.events.PacketReceiveSyncEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S14PacketEntity;

public class AntiBot
extends Module {
    public static AntiBot INSTANCE = new AntiBot();
    @Settings
    private boolean checkOnGround = false;
    @Settings
    private boolean checkSwing = false;
    private final List<Integer> hasBeenGround = new ArrayList<Integer>();
    private final List<Integer> hasSwing = new ArrayList<Integer>();

    protected AntiBot() {
        super("AntiBot", 0, false, ModuleType.MISC);
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        if (event.getPacket() instanceof S14PacketEntity) {
            Entity swingEntity;
            S14PacketEntity s14Packet = (S14PacketEntity)event.getPacket();
            Entity entity = s14Packet.getEntity(AntiBot.mc.theWorld);
            if (entity instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer)entity;
                if (entity.onGround) {
                    this.hasBeenGround.add(entityPlayer.getEntityId());
                }
            }
            if (event.getPacket() instanceof S0BPacketAnimation && (swingEntity = AntiBot.mc.theWorld.getEntityByID(((S0BPacketAnimation)event.getPacket()).entityId)) instanceof EntityPlayer) {
                this.hasSwing.add(swingEntity.getEntityId());
            }
        }
    }

    public boolean isBot(EntityLivingBase entityLivingBase) {
        if (!this.getState()) {
            return false;
        }
        if (this.checkOnGround && !this.hasBeenGround.contains(entityLivingBase.getEntityId())) {
            return true;
        }
        return this.checkSwing && !this.hasSwing.contains(entityLivingBase.getEntityId());
    }
}

