/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.combat;

import client.Client;
import client.event.events.MotionEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.combat.KillAura;
import client.module.modules.misc.AntiBot;
import client.module.modules.misc.Gapple;
import client.module.modules.misc.Teams;
import client.module.modules.movement.NoSlow;
import client.utils.BlinkUtils;
import client.utils.MSTimer;
import client.utils.PacketUtils;
import client.utils.rotation.RotationPriority;
import client.utils.rotation.RotationSetter;
import client.utils.rotation.RotationUtils;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import org.lwjgl.util.vector.Vector2f;

public class AutoProjectile extends Module {
    @Settings(minValue=1.0, maxValue=20.0)
    private float range = 1.0f;
    @Settings
    private boolean rightClickThrow = false;
    @Settings(maxValue=2000.0, minValue=0.0)
    private int throwDelay = 50;
    @Settings
    private boolean noGapple = false;
    @Settings
    private boolean noBlinking = false;
    private final MSTimer throwTimer1 = new MSTimer();
    private final MSTimer throwTimer = new MSTimer();
    private final MSTimer projectilePullTimer = new MSTimer();
    private boolean projectileInUse = false;
    private int switchBack = -1;
    private Vector2f proRot = null;
    private boolean rot = false;
    private boolean click = false;

    public AutoProjectile() {
        super("AutoProjectile", 0, false, ModuleType.COMBAT);
    }

    @Override
    public void onTick() {
        if (AutoProjectile.mc.thePlayer == null || AutoProjectile.mc.theWorld == null) {
            return;
        }
        KillAura killAura = (KillAura)Client.moduleManager.moduleMap.get(KillAura.class);
        Gapple gapple = (Gapple)Client.moduleManager.moduleMap.get(Gapple.class);
        int projectile = this.findProjectile(36, 45);
        if (projectile == -1 || (Gapple.pulsing || gapple.getState()) && this.noGapple || BlinkUtils.blinking && this.noBlinking || killAura.target != null || !killAura.renderTargets.isEmpty()) {
            this.rot = false;
            this.throwTimer.reset();
            this.throwTimer1.reset();
            this.proRot = null;
            return;
        }
        if (this.rot) {
            this.throwProjectile();
            this.proRot = null;
            this.rot = false;
        }
        this.throwTimer1.reset();
        if (this.throwTimer.hasPassed(this.throwDelay) || GameSettings.isKeyDown(AutoProjectile.mc.gameSettings.keyBindUseItem) && !this.click && this.rightClickThrow) {
            Entity entity = null;
            for (Object obj : AutoProjectile.mc.theWorld.loadedEntityList) {
                EntityLivingBase e;
                if (!(obj instanceof EntityLivingBase) || (e = (EntityLivingBase)obj).getEntityId() == AutoProjectile.mc.thePlayer.getEntityId()) continue;
                if (AntiBot.INSTANCE.isBot(e)) {
                    return;
                }
                if (Teams.isSameTeam(e)) {
                    return;
                }
                if (!(AutoProjectile.mc.thePlayer.getDistanceToEntity(e) < this.range)) continue;
                entity = e;
            }
            if (entity == null) {
                return;
            }
            Vector2f rotation = RotationUtils.searchCenter(entity.getEntityBoundingBox().offset(((EntityLivingBase)entity).motionX, 1.0, ((EntityLivingBase)entity).motionZ), false, false, this.range);
            if (rotation == null) {
                return;
            }
            this.proRot = rotation;
            RotationSetter.setRotation(rotation, 0, RotationPriority.VERY_LOW);
            this.throwTimer.reset();
        }
        this.click = GameSettings.isKeyDown(AutoProjectile.mc.gameSettings.keyBindUseItem);
    }

    @Override
    public void onMotion(MotionEvent event) {
        if (event.isPre() && this.proRot != null && RotationSetter.targetRotation != null && RotationSetter.targetRotation.y == this.proRot.y && RotationSetter.targetRotation.x == this.proRot.x) {
            this.rot = true;
        }
    }

    private void throwProjectile() {
        int projectile = this.findProjectile(36, 45);
        if (projectile == -1) {
            return;
        }
        NoSlow noSlow = (NoSlow)Client.moduleManager.moduleMap.get(NoSlow.class);
        int prev = PacketUtils.packetSlot;
        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(projectile - 36));
        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(AutoProjectile.mc.thePlayer.inventoryContainer.getSlot(projectile).getStack()));
        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(prev));
        if (AutoProjectile.mc.thePlayer.isUsingItem()) {
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(AutoProjectile.mc.thePlayer.getHeldItem()));
        }
        this.projectileInUse = true;
        this.projectilePullTimer.reset();
    }

    private int findProjectile(int startSlot, int endSlot) {
        for (int i = startSlot; i < endSlot; ++i) {
            ItemStack stack;
            if (AutoProjectile.mc.thePlayer == null || AutoProjectile.mc.thePlayer.inventoryContainer == null || (stack = AutoProjectile.mc.thePlayer.inventoryContainer.getSlot(i).getStack()) == null || stack.getItem() != Items.snowball && stack.getItem() != Items.egg) continue;
            return i;
        }
        return -1;
    }

    @Override
    public void onDisable() {
        this.throwTimer.reset();
        this.projectilePullTimer.reset();
        this.projectileInUse = false;
        this.switchBack = -1;
    }
}

