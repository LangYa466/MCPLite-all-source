/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.misc;

import client.event.events.Render2DEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.visual.ClientColor;
import client.module.modules.visual.Noti;
import client.ui.font.FontLoaders;
import client.ui.notifi.Notifi;
import client.utils.BlinkUtils;
import client.utils.InventoryUtils;
import client.utils.MovementUtils;
import client.utils.RenderUtils;
import java.awt.Color;
import java.text.DecimalFormat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.util.ResourceLocation;

public class Gapple
extends Module {
    @Settings(minValue=1.0, maxValue=10.0)
    private int sendDelay = 3;
    @Settings(minValue=1.0, maxValue=5.0)
    private int sendOnceTicks = 1;
    @Settings
    private boolean stopMove = true;
    @Settings
    public boolean noCancelC02 = false;
    @Settings
    public boolean noC02 = false;
    @Settings(list={"NONE", "Noti", "Rect"})
    private String renderMode = "Noti";
    @Settings(list={"NONE", "Dragon", "SCP"})
    private String renderImageMode = "Dragon";
    @Settings
    private boolean autoGapple = false;
    private int slot = -1;
    private int c03s = 0;
    private int c02s = 0;
    private double smooth = 0.0;
    private double smooth1 = 0.0;
    private boolean canStart = false;
    ResourceLocation dragon;
    public static boolean eating = false;
    public static boolean pulsing = false;

    public Gapple() {
        super("Gapple", 0, ModuleType.MISC);
    }

    @Override
    public void onEnable() {
        this.smooth = 0.0;
        this.smooth1 = 0.0;
        this.dragon = new ResourceLocation("client/Dragon" + (Gapple.mc.thePlayer.ticksExisted % 2 == 0 ? ".png" : "1.png"));
        this.c03s = 0;
        this.slot = InventoryUtils.findItem(36, 45, Items.golden_apple);
        if (this.slot != -1) {
            this.slot -= 36;
        }
    }

    @Override
    public void onDisable() {
        eating = false;
        if (this.canStart) {
            pulsing = false;
            eating = false;
            BlinkUtils.stopBlink();
        }
        if (this.stopMove) {
            MovementUtils.resetMove();
        }
    }

    @Override
    public void onTick() {
        if (Gapple.mc.thePlayer == null || Gapple.mc.thePlayer.isDead) {
            BlinkUtils.stopBlink();
            this.setState(false);
            return;
        }
        if (this.slot == -1) {
            Noti.INSTANCE.notifiList.add(new Notifi("You haven't any gapple!", Noti.INSTANCE.moduleMoveX));
            this.setState(false);
            return;
        }
        if (eating) {
            if (this.stopMove) {
                MovementUtils.cancelMove();
            }
            if (!BlinkUtils.blinking) {
                BlinkUtils.blink(C09PacketHeldItemChange.class, C0EPacketClickWindow.class, C0DPacketCloseWindow.class);
                BlinkUtils.setCancelReturnPredicate(C07PacketPlayerDigging.class, it -> ((C07PacketPlayerDigging)it).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM);
                BlinkUtils.setCancelReturnPredicate(C08PacketPlayerBlockPlacement.class, it -> ((C08PacketPlayerBlockPlacement)it).getPosition().getY() == -1);
                BlinkUtils.setCancelReturnPredicate(C02PacketUseEntity.class, it -> this.noCancelC02);
                BlinkUtils.setCancelReturnPredicate(C0APacketAnimation.class, it -> this.noCancelC02);
                BlinkUtils.setCancelAction(C03PacketPlayer.class, packet -> ++this.c03s);
                BlinkUtils.setReleaseAction(C03PacketPlayer.class, packet -> --this.c03s);
                BlinkUtils.setReleaseReturnPredicateMap(C02PacketUseEntity.class, packet -> !eating && this.noC02);
                BlinkUtils.setCancelAction(C02PacketUseEntity.class, packet -> ++this.c02s);
                BlinkUtils.setReleaseAction(C02PacketUseEntity.class, packet -> --this.c02s);
                this.canStart = true;
            }
        } else {
            eating = true;
        }
        if (this.c03s >= 32) {
            eating = false;
            pulsing = true;
            BlinkUtils.resetBlackList();
            BlinkUtils.sendPacket(new C09PacketHeldItemChange(this.slot), true);
            System.out.println("Start!");
            BlinkUtils.sendPacket(new C08PacketPlayerBlockPlacement(Gapple.mc.thePlayer.inventoryContainer.getSlot(this.slot + 36).getStack()), true);
            BlinkUtils.stopBlink();
            System.out.println("Stop!");
            BlinkUtils.sendPacket(new C09PacketHeldItemChange(Gapple.mc.thePlayer.inventory.currentItem), true);
            pulsing = false;
            if (this.autoGapple) {
                this.smooth = 0.0;
                this.smooth1 = 0.0;
                this.dragon = new ResourceLocation("client/Dragon" + (Gapple.mc.thePlayer.ticksExisted % 2 == 0 ? ".png" : "1.png"));
                this.c03s = 0;
                this.slot = InventoryUtils.findItem(36, 45, Items.golden_apple);
                if (this.slot != -1) {
                    this.slot -= 36;
                }
            } else {
                this.setState(false);
            }
            if (this.renderMode.equalsIgnoreCase("noti")) {
                if (this.renderImageMode.equalsIgnoreCase("dragon")) {
                    Noti.INSTANCE.notifiList.add(new Notifi(this.dragon, 64, 64, 300.0f));
                }
                Noti.INSTANCE.notifiList.add(new Notifi("Gapple !", Noti.INSTANCE.moduleMoveX));
            }
            return;
        }
        if (Gapple.mc.thePlayer.ticksExisted % this.sendDelay == 0) {
            for (int i = 0; i < this.sendOnceTicks; ++i) {
                BlinkUtils.releasePacket(true);
            }
            if (this.renderMode.equalsIgnoreCase("noti")) {
                DecimalFormat decimalFormat = new DecimalFormat("###.0000%");
                Noti.INSTANCE.notifiList.add(new Notifi(decimalFormat.format((double)this.c03s / 37.0), Noti.INSTANCE.moduleMoveX));
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        float x = (float)((double)scaledResolution.getScaledWidth() / 2.0 - 50.0);
        float y = (float)((double)scaledResolution.getScaledHeight() / 2.0 + 10.0);
        float target = (float)(100.0 * ((double)this.c03s / 32.0));
        this.smooth1 += 100.0 * ((double)this.c03s / 32.0) < 99.0 ? ((double)(target + 10.0f) - this.smooth1) / 100.0 : (double)target - this.smooth1;
        if (this.renderMode.equalsIgnoreCase("rect")) {
            RenderUtils.drawRect(x, y, x + 100.0f, y + 5.0f, new Color(52, 51, 51, 153).getRGB());
            RenderUtils.drawRect(x, y, (float)((double)x + this.smooth1), y + 5.0f, ClientColor.INSTANCE.getMixColor().getRGB());
            DecimalFormat decimalFormat = new DecimalFormat("###.0%");
            FontLoaders.Bold18.drawCenteredString(decimalFormat.format((double)this.c03s / 32.0), (float)scaledResolution.getScaledWidth() / 2.0f, (float)scaledResolution.getScaledHeight() / 2.0f + 20.0f, ClientColor.INSTANCE.getMixColor().getRGB());
        }
        if (this.renderImageMode.equalsIgnoreCase("dragon")) {
            int height = (int)((double)scaledResolution.getScaledHeight() - (double)scaledResolution.getScaledHeight() * ((double)this.c03s / 32.0)) + 1;
            this.smooth += ((double)(height >= scaledResolution.getScaledHeight() / 2 ? height : scaledResolution.getScaledHeight() - height) - this.smooth) / 100.0;
            RenderUtils.drawImage(this.dragon, scaledResolution.getScaledWidth() / 2 - 32, (int)this.smooth, 64, 64);
        }
        if (this.renderImageMode.equalsIgnoreCase("SCP")) {
            int height = (int)((double)scaledResolution.getScaledHeight() - (double)scaledResolution.getScaledHeight() * ((double)this.c03s / 32.0)) + 1;
            this.smooth += ((double)(height >= scaledResolution.getScaledHeight() / 2 ? height : scaledResolution.getScaledHeight() - height) - this.smooth) / 100.0;
            RenderUtils.drawSCPAnimationC(scaledResolution.getScaledWidth() / 2, (float)scaledResolution.getScaledHeight() / 2.0f + 100.0f, 0.8f, (int)Math.min(360.0, 360.0 * (this.smooth1 / 90.0)));
        }
    }

    @Override
    public String getTag() {
        return this.autoGapple ? "Auto" : "Once";
    }
}

