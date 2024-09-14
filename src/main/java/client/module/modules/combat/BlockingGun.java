/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.combat;

import client.module.Module;
import client.module.ModuleType;
import client.utils.BlinkUtils;
import client.utils.ForTest;
import client.utils.InventoryUtils;
import net.minecraft.item.ItemAxe;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class BlockingGun
extends Module {
    private boolean switchBack = false;

    public BlockingGun() {
        super("BlockingGun", 0, ModuleType.COMBAT);
    }

    @Override
    public void onEnable() {
        this.switchBack = false;
        ForTest.anInt = 0;
    }

    @Override
    public void onDisable() {
        BlinkUtils.stopBlink();
    }

    @Override
    public void onUpdate() {
        // TODO 死循环笑死我了
        int slot = InventoryUtils.findItem(36, 45, ItemAxe.class);
        if (slot != -1 && !this.switchBack) {
            BlinkUtils.blink(new Class[0]);
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(slot - 36));
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(null));
            this.switchBack = true;
        } else if (++ForTest.anInt > 4) {
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(BlockingGun.mc.thePlayer.inventory.currentItem));
            this.switchBack = false;
            this.setState(false);
        }
    }
}

