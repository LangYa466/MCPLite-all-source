/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.world;

import client.Client;
import client.event.events.PacketSendEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.modules.world.Scaffold;
import client.utils.ItemUtils;
import java.util.Comparator;
import java.util.Optional;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class AutoWeapon
extends Module {
    private boolean switchSword = false;

    public AutoWeapon() {
        super("AutoWeapon", 0, false, ModuleType.WORLD);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (AutoWeapon.mc.thePlayer != null && (AutoWeapon.mc.thePlayer.getHeldItem() == null || !(AutoWeapon.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) && event.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity)event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
            this.switchSword = true;
        }
    }

    @Override
    public void onUpdate() {
        if (this.switchSword) {
            Scaffold scaffold = (Scaffold)Client.moduleManager.moduleMap.get(Scaffold.class);
            if (!scaffold.getState()) {
                Optional<Slot> bestAxe = AutoWeapon.mc.thePlayer.inventoryContainer.inventorySlots.stream().filter(slot -> slot.slotNumber >= 36 && slot.getHasStack() && slot.getStack().getItem() instanceof ItemAxe && 1.25 * (double)ItemUtils.getEnchantment(slot.getStack(), Enchantment.sharpness) > 13.0).findFirst();
                if (bestAxe.isPresent()) {
                    AutoWeapon.mc.thePlayer.inventory.currentItem = bestAxe.get().slotNumber - 36;
                    AutoWeapon.mc.playerController.updateController();
                } else {
                    Optional<Slot> bestSword = AutoWeapon.mc.thePlayer.inventoryContainer.inventorySlots.stream().filter(slot1 -> slot1.slotNumber >= 36 && slot1.getStack() != null && slot1.getStack().getItem() instanceof ItemSword).max(Comparator.comparingDouble(sword -> (double)((ItemSword)sword.getStack().getItem()).attackDamage + 1.25 * (double)ItemUtils.getEnchantment(sword.getStack(), Enchantment.sharpness)));
                    if (bestSword.isPresent()) {
                        AutoWeapon.mc.thePlayer.inventory.currentItem = bestSword.get().slotNumber - 36;
                        AutoWeapon.mc.playerController.updateController();
                    }
                }
            }
            this.switchSword = false;
        }
    }
}

