/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.world;

import client.Client;
import client.event.events.PacketReceiveSyncEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.combat.KillAura;
import client.module.modules.misc.Gapple;
import client.module.modules.misc.ItemManager;
import client.module.modules.world.Scaffold;
import client.utils.BlockUtils;
import client.utils.ClientUtils;
import client.utils.MSTimer;
import client.utils.rotation.RotationPriority;
import client.utils.rotation.RotationSetter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.util.vector.Vector2f;

public class AutoContainer
extends Module {
    @Settings(maxValue=5.0)
    private int range = 5;
    @Settings(maxValue=1000.0)
    private int delay = 500;
    @Settings
    private boolean autoCancel = true;
    private BlockPos theChest;
    private List<BlockPos> hasOpened = new ArrayList<BlockPos>();
    private MSTimer msTimer = new MSTimer();

    public AutoContainer() {
        super("AutoContainer", 0, false, ModuleType.WORLD);
    }

    @Override
    public void onTick() {
        if (ClientUtils.nullCheck()) {
            return;
        }
        KillAura killAura = (KillAura)Client.moduleManager.moduleMap.get(KillAura.class);
        Scaffold scaffold = (Scaffold)Client.moduleManager.moduleMap.get(Scaffold.class);
        ItemManager itemManager = (ItemManager)Client.moduleManager.moduleMap.get(ItemManager.class);
        Gapple gapple = (Gapple)Client.moduleManager.moduleMap.get(Gapple.class);
        if (killAura.target != null && this.autoCancel || scaffold.getState() && this.autoCancel || gapple.getState() && this.autoCancel) {
            this.msTimer.reset();
            return;
        }
        if (!this.msTimer.hasPassed(this.delay)) {
            return;
        }
        Vec3 eyesPos = AutoContainer.mc.thePlayer.getPositionEyes(1.0f);
        this.theChest = BlockUtils.searchBlocks(this.range + 1).keySet().stream().filter(e -> {
            if (!(BlockUtils.getBlock(e) instanceof BlockChest) && !(BlockUtils.getBlock(e) instanceof BlockBrewingStand)) {
                if (!(BlockUtils.getBlock(e) instanceof BlockFurnace)) return false;
            }
            if (this.hasOpened.contains(e)) return false;
            double d = (double)e.getX() + 0.5;
            double d2 = BlockUtils.getBlock(e) instanceof BlockBrewingStand ? (double)e.getY() : (double)e.getY() + 0.5;
            if (!(AutoContainer.mc.thePlayer.getDistance(d, d2, (double)e.getZ() + 0.5) < (double)this.range)) return false;
            return true;
        }).filter(e -> AutoContainer.mc.theWorld.rayTraceBlocks(eyesPos, new Vec3((double)e.getX() + 0.5, BlockUtils.getBlock(e) instanceof BlockBrewingStand ? (double)e.getY() : (double)e.getY() + 0.5, (double)e.getZ() + 0.5), false, true, false) != null).min(Comparator.comparingDouble(e -> AutoContainer.mc.thePlayer.getDistance((double)e.getX() + 0.5, BlockUtils.getBlock(e) instanceof BlockBrewingStand ? (double)e.getY() : (double)e.getY() + 0.5, (double)e.getZ() + 0.5))).orElse(null);
        if (this.theChest != null && itemManager.openScreen == null && itemManager.screenContainer == null) {
            Vec3 hitVec = new Vec3((double)this.theChest.getX() + 0.5, (double)this.theChest.getY() + 0.5, (double)this.theChest.getZ() + 0.5);
            if (BlockUtils.getBlock(this.theChest) instanceof BlockBrewingStand) {
                hitVec = new Vec3((double)this.theChest.getX() + 0.5, this.theChest.getY(), (double)this.theChest.getZ() + 0.5);
            }
            double diffX = hitVec.xCoord - eyesPos.xCoord;
            double diffY = hitVec.yCoord - eyesPos.yCoord;
            double diffZ = hitVec.zCoord - eyesPos.zCoord;
            double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
            Vector2f rotation = new Vector2f(MathHelper.wrapAngleTo180_float((float)(Math.toDegrees(MathHelper.atan2(diffZ, diffX)) - 90.0)), MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(MathHelper.atan2(diffY, diffXZ)))));
            RotationSetter.setRotation(rotation, 0, RotationPriority.LOW);
        }
    }

    @Override
    public void onUpdate() {
        BlockPos blockPos;
        KillAura killAura = (KillAura)Client.moduleManager.moduleMap.get(KillAura.class);
        Scaffold scaffold = (Scaffold)Client.moduleManager.moduleMap.get(Scaffold.class);
        Gapple gapple = (Gapple)Client.moduleManager.moduleMap.get(Gapple.class);
        if (killAura.target != null && this.autoCancel || scaffold.getState() && this.autoCancel || gapple.getState() && this.autoCancel) {
            this.msTimer.reset();
            return;
        }
        if (!this.msTimer.hasPassed(this.delay)) {
            return;
        }
        ItemManager itemManager = (ItemManager)Client.moduleManager.moduleMap.get(ItemManager.class);
        BlockPos blockPos2 = blockPos = AutoContainer.mc.objectMouseOver.getBlockPos() != null ? AutoContainer.mc.objectMouseOver.getBlockPos() : null;
        if (!(AutoContainer.mc.currentScreen instanceof GuiChest) && blockPos != null && this.theChest != null && blockPos.getY() == this.theChest.getY() && blockPos.getZ() == this.theChest.getZ() && blockPos.getX() == this.theChest.getX() && (!itemManager.getState() || itemManager.screenContainer == null && itemManager.openScreen == null) && AutoContainer.mc.playerController.onPlayerRightClick(AutoContainer.mc.thePlayer, AutoContainer.mc.theWorld, AutoContainer.mc.thePlayer.getHeldItem(), AutoContainer.mc.objectMouseOver.getBlockPos(), AutoContainer.mc.objectMouseOver.sideHit, AutoContainer.mc.objectMouseOver.hitVec)) {
            this.msTimer.reset();
            AutoContainer.mc.thePlayer.swingItem();
            this.hasOpened.add(this.theChest);
        }
    }

    @Override
    public void onDisable() {
        this.hasOpened.clear();
    }

    @Override
    public void onWorldLoad() {
        this.hasOpened.clear();
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        String s;
        S45PacketTitle wrapper;
        if (event.getPacket() instanceof S45PacketTitle && (wrapper = (S45PacketTitle)event.getPacket()).getType() == S45PacketTitle.Type.TITLE && (s = wrapper.getMessage().getFormattedText()).contains("\u6218\u6597\u5f00\u59cb...")) {
            this.hasOpened.clear();
        }
    }
}

