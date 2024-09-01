/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.movement;

import client.event.events.PacketReceiveSyncEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.BlinkUtils;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S45PacketTitle;

public class Clip
extends Module {
    @Settings(list={"HYT"})
    private String mode = "HYT";

    public Clip() {
        super("Clip", 0, false, ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (this.mode.equalsIgnoreCase("hyt")) {
            BlinkUtils.blink(new Class[0]);
        }
    }

    @Override
    public void onPreUpdate() {
        if (this.mode.equalsIgnoreCase("hyt")) {
            BlinkUtils.sendPacket(new C0FPacketConfirmTransaction(), false);
        }
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        S45PacketTitle wrapper;
        if (event.getPacket() instanceof S45PacketTitle && (wrapper = (S45PacketTitle)event.getPacket()).getType() == S45PacketTitle.Type.TITLE) {
            String s = wrapper.getMessage().getFormattedText();
            System.out.println(s);
            if (s.contains("\u6218\u6597\u5f00\u59cb...")) {
                this.setState(false);
            }
        }
    }

    @Override
    public void onDisable() {
        if (this.mode.equalsIgnoreCase("hyt")) {
            BlinkUtils.stopBlink();
        }
    }
}

