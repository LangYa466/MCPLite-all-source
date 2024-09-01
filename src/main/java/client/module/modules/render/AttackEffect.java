/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.render;

import client.Client;
import client.event.events.PacketSendEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.ClientUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

public class AttackEffect
extends Module {
    @Settings(list={"Attack", "Dead"})
    private String timing = "Attack";
    @Settings(list={"Vanilla", "Glass", "Skeet", "LightNing"})
    private String mode = "Vanilla";
    @Settings(maxValue=100.0)
    private int deadDelay = 0;
    @Settings
    private boolean fireSound = false;
    private final List<EntityLivingBase> entityLivingBases = new ArrayList<EntityLivingBase>();
    private final Map<EntityLivingBase, Integer> delayMap = new HashMap<EntityLivingBase, Integer>();

    public AttackEffect() {
        super("AttackEffect", 0, false, ModuleType.RENDER);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity)event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK && ((C02PacketUseEntity)event.getPacket()).getEntityFromWorld(AttackEffect.mc.theWorld) instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)((C02PacketUseEntity)event.getPacket()).getEntityFromWorld(AttackEffect.mc.theWorld);
            if (this.timing.equalsIgnoreCase("dead")) {
                if (!this.entityLivingBases.contains(entityLivingBase) && !entityLivingBase.isDead && entityLivingBase.getHealth() > 0.0f) {
                    this.delayMap.put(entityLivingBase, 0);
                    this.entityLivingBases.add(entityLivingBase);
                }
                return;
            }
            if (this.timing.equalsIgnoreCase("Attack")) {
                switch (this.mode) {
                    case "Vanilla": {
                        AttackEffect.mc.theWorld.playSound(entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ, entityLivingBase.getHurtSound(), entityLivingBase.getSoundVolume(), entityLivingBase.getSoundPitch(), false);
                        break;
                    }
                    case "Glass": {
                        Client.soundManager.glassSound.asyncPlay(entityLivingBase.getSoundVolume() * 100.0f, entityLivingBase.getSoundPitch() - 0.5f);
                        break;
                    }
                    case "Skeet": {
                        Client.soundManager.skeetSound.asyncPlay(entityLivingBase.getSoundVolume() * 100.0f, entityLivingBase.getSoundPitch() - 0.5f);
                        break;
                    }
                    case "LightNing": {
                        mc.getNetHandler().handleSpawnGlobalEntity(new S2CPacketSpawnGlobalEntity(new EntityLightningBolt(AttackEffect.mc.theWorld, entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ)));
                        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.explode"), 1.0f));
                        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("ambient.weather.thunder"), 1.0f));
                    }
                }
            }
        }
    }

    @Override
    public void onTick() {
        if (ClientUtils.nullCheck()) {
            this.entityLivingBases.clear();
            return;
        }
        try {
            this.entityLivingBases.removeIf(entityLivingBase -> {
                if (entityLivingBase.isDead || entityLivingBase.getHealth() <= 0.0f) {
                    if (this.delayMap.get(entityLivingBase) < this.deadDelay) {
                        this.delayMap.replace((EntityLivingBase)entityLivingBase, this.delayMap.get(entityLivingBase) + 1);
                        return false;
                    }
                    switch (this.mode) {
                        case "Vanilla": {
                            AttackEffect.mc.theWorld.playSound(entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ, entityLivingBase.getHurtSound(), entityLivingBase.getSoundVolume(), entityLivingBase.getSoundPitch(), false);
                            break;
                        }
                        case "Glass": {
                            Client.soundManager.glassSound.asyncPlay(entityLivingBase.getSoundVolume() * 100.0f, entityLivingBase.getSoundPitch() - 0.5f);
                            break;
                        }
                        case "Skeet": {
                            Client.soundManager.skeetSound.asyncPlay(entityLivingBase.getSoundVolume() * 100.0f, entityLivingBase.getSoundPitch() - 0.5f);
                            break;
                        }
                        case "LightNing": {
                            mc.getNetHandler().handleSpawnGlobalEntity(new S2CPacketSpawnGlobalEntity(new EntityLightningBolt(AttackEffect.mc.theWorld, entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ)));
                            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.explode"), 1.0f));
                            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("ambient.weather.thunder"), 1.0f));
                        }
                    }
                    if (this.fireSound) {
                        for (int i = 0; i < 200; ++i) {
                            Random rand = new Random();
                            AttackEffect.mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.FLAME.getParticleID(), entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ, rand.nextGaussian() * 0.75, rand.nextGaussian() * 0.75, rand.nextGaussian() * 0.75, new int[0]);
                        }
                        AttackEffect.mc.theWorld.playSound(AttackEffect.mc.thePlayer.posX, AttackEffect.mc.thePlayer.posY, AttackEffect.mc.thePlayer.posZ, "item.fireCharge.use", 1.0f, 1.0f, false);
                    }
                    return true;
                }
                return false;
            });
        }
        catch (Exception e) {
            this.entityLivingBases.clear();
            e.printStackTrace();
        }
    }

    @Override
    public void onWorldLoad() {
        this.entityLivingBases.clear();
    }

    @Override
    public String getTag() {
        return this.mode;
    }
}

