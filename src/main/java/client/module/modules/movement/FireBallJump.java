/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.movement;

import client.Client;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.combat.Velocity;
import client.utils.ClientUtils;
import client.utils.MovementUtils;
import client.utils.PacketUtils;
import client.utils.rotation.RotationSetter;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

public class FireBallJump
extends Module {
    @Settings(list={"Strafe", "Hypixel"})
    private String modeValue = "Hypixel";
    @Settings(minValue=2.0, maxValue=10.0)
    private float strafeBoost = 2.0f;
    @Settings(minValue=0.41999998688697815, maxValue=3.0)
    private float strafeY = 2.0f;
    @Settings
    private boolean disableVelo = true;
    private int previtem = 0;
    private boolean flying = false;
    private boolean antikb = false;
    private boolean velo = false;
    private int ticks = 0;

    public FireBallJump() {
        super("FireBallJump", 0, false, ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
        Velocity velocity = (Velocity)Client.moduleManager.moduleMap.get(Velocity.class);
        this.flying = false;
        this.previtem = FireBallJump.mc.thePlayer.inventory.currentItem;
        if (this.disableVelo && velocity.getState()) {
            this.velo = true;
            velocity.setState(false);
        }
        this.ticks = 0;
    }

    @Override
    public void onDisable() {
        Velocity velocity = (Velocity)Client.moduleManager.moduleMap.get(Velocity.class);
        this.flying = false;
        FireBallJump.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
        FireBallJump.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(this.previtem));
        if (this.disableVelo && this.velo) {
            velocity.setState(true);
            this.velo = false;
        }
        this.ticks = 0;
    }

    @Override
    public void onUpdate() {
        ++this.ticks;
        if (this.getFBSlot() == -1) {
            ClientUtils.displayChatMessage("[Longjump] \u00a7CNO FIREBALL FOUND\u00a7F");
            this.setState(false);
            return;
        }
        if (FireBallJump.mc.thePlayer.hurtTime == 0) {
            RotationSetter.setRotation(new Vector2f(FireBallJump.mc.thePlayer.rotationYaw - 180.0f, 90.0f), 0);
            FireBallJump.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(this.getFBSlot()));
            FireBallJump.mc.thePlayer.inventory.currentItem = this.getFBSlot();
        }
        if (this.ticks == 2) {
            PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(FireBallJump.mc.thePlayer.inventory.getStackInSlot(this.getFBSlot())));
        }
        if (!this.flying) {
            if (this.modeValue.equals("Strafe")) {
                if (FireBallJump.mc.thePlayer.hurtTime == 9) {
                    MovementUtils.strafe(this.strafeBoost);
                    FireBallJump.mc.thePlayer.motionY = this.strafeY;
                    this.flying = true;
                }
            } else if (FireBallJump.mc.thePlayer.hurtTime == 9) {
                MovementUtils.strafe(2.0f);
                this.flying = true;
            }
        }
        if (this.flying && FireBallJump.mc.thePlayer.hurtTime < 7) {
            this.setState(false);
        }
    }

    private int getFBSlot() {
        for (int i = 36; i <= 44; ++i) {
            ItemStack stack = FireBallJump.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack == null || !(stack.getItem() instanceof ItemFireball)) continue;
            return i - 36;
        }
        return -1;
    }
}

