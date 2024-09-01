/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.world;

import client.Client;
import client.event.events.MotionEvent;
import client.event.events.Render2DEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.movement.Stuck;
import client.ui.fastuni.FontLoader;
import client.utils.DebugUtil;
import client.utils.FallDistanceComponent;
import client.utils.InventoryUtils;
import client.utils.PlayerUtils;
import client.utils.ProjectileUtil;
import client.utils.TimeUtil;
import java.awt.Color;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector2f;

public class AutoPearl
extends Module {
    @Settings
    public boolean debug = false;
    @Settings
    public boolean once = true;
    private static final double T = 10.0;
    private static final double T_MIN = 1.0E-4;
    private static final double ALPHA = 0.997;
    private CalculateThread calculateThread;
    private final TimeUtil timer = new TimeUtil();
    private boolean attempted;
    private boolean calculating;
    private int bestPearlSlot;

    public AutoPearl() {
        super("AutoPearl", 0, true, ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
        this.attempted = false;
    }

    @Override
    public void onMotion(MotionEvent event) {
        if (AutoPearl.mc.thePlayer.onGround) {
            this.attempted = false;
            this.calculating = false;
            if (this.once) {
                this.setState(false);
                return;
            }
        }
        if (event.isPre() && this.calculating && (this.calculateThread == null || this.calculateThread.completed)) {
            this.calculating = false;
            Stuck.throwPearl(this.calculateThread.solution);
            if (this.once) {
                this.setState(false);
                return;
            }
        }
        boolean overVoid = !AutoPearl.mc.thePlayer.onGround && !PlayerUtils.isBlockUnder(30.0, true);
        boolean bl = overVoid;
        if (!this.attempted && (!AutoPearl.mc.thePlayer.onGround && overVoid && FallDistanceComponent.distance > 2.0f || this.once)) {
            FallDistanceComponent.distance = 0.0f;
            this.attempted = true;
            for (int slot = 5; slot < 45; ++slot) {
                ItemStack stack = AutoPearl.mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
                if (stack == null || !(stack.getItem() instanceof ItemEnderPearl) || slot < 36) continue;
                this.bestPearlSlot = slot;
                if (this.debug) {
                    DebugUtil.log("Found Pearl:" + (this.bestPearlSlot - 36));
                }
                if (this.bestPearlSlot - 36 == -37) continue;
                AutoPearl.mc.thePlayer.inventory.currentItem = this.bestPearlSlot - 36;
            }
            if (this.bestPearlSlot == 0) {
                return;
            }
            DebugUtil.log(this.bestPearlSlot);
            if (!(AutoPearl.mc.thePlayer.inventoryContainer.getSlot(this.bestPearlSlot).getStack().getItem() instanceof ItemEnderPearl)) {
                return;
            }
            this.calculating = true;
            this.calculateThread = new CalculateThread(AutoPearl.mc.thePlayer.posX, AutoPearl.mc.thePlayer.posY, AutoPearl.mc.thePlayer.posZ, 0.0, 0.0);
            this.calculateThread.start();
            Client.moduleManager.moduleMap.get(Stuck.class).setState(true);
        }
    }

    private void putItemInSlot(int slot, int slotIn) {
        InventoryUtils.windowClick(mc, slotIn, slot - 36, InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (!this.debug) {
            return;
        }
        FontLoader.simpleFont12.drawString("assessment: " + new ProjectileUtil.EnderPearlPredictor(AutoPearl.mc.thePlayer.posX, AutoPearl.mc.thePlayer.posY, AutoPearl.mc.thePlayer.posZ, AutoPearl.mc.thePlayer.motionY - 0.01, AutoPearl.mc.thePlayer.motionY + 0.02).assessRotation(new Vector2f(AutoPearl.mc.thePlayer.rotationYaw, AutoPearl.mc.thePlayer.rotationPitch)), 20.0f, 20.0f, Color.WHITE.getRGB());
        FontLoader.simpleFont12.drawString("(" + AutoPearl.mc.thePlayer.rotationYaw + ", " + AutoPearl.mc.thePlayer.rotationPitch + ")", 20.0f, 30.0f, Color.WHITE.getRGB());
    }

    private static class CalculateThread
    extends Thread {
        private int iteration;
        private boolean completed;
        private double temperature;
        private double energy;
        private double solutionE;
        private Vector2f solution;
        public boolean stop;
        private final ProjectileUtil.EnderPearlPredictor predictor;

        private CalculateThread(double predictX, double predictY, double predictZ, double minMotionY, double maxMotionY) {
            this.predictor = new ProjectileUtil.EnderPearlPredictor(predictX, predictY, predictZ, minMotionY, maxMotionY);
            this.iteration = 0;
            this.temperature = 10.0;
            this.energy = 0.0;
            this.stop = false;
            this.completed = false;
        }

        @Override
        public void run() {
            TimeUtil timer = new TimeUtil();
            timer.reset();
            Vector2f current = this.solution = new Vector2f(MathHelper.getRandomInRange(-180, 180), MathHelper.getRandomInRange(-90, 90));
            this.solutionE = this.energy = this.predictor.assessRotation(this.solution);
            while (this.temperature >= 1.0E-4 && !this.stop) {
                double assessment;
                double deltaE;
                Vector2f rotation = new Vector2f((float)((double)current.x + MathHelper.getRandomInRange(-this.temperature * 18.0, this.temperature * 18.0)), (float)((double)current.y + MathHelper.getRandomInRange(-this.temperature * 9.0, this.temperature * 9.0)));
                if (rotation.y > 90.0f) {
                    rotation.y = 90.0f;
                }
                if (rotation.y < -90.0f) {
                    rotation.y = -90.0f;
                }
                if ((deltaE = (assessment = this.predictor.assessRotation(rotation)) - this.energy) >= 0.0 || (double)MathHelper.getRandomInRange(0, 1) < Math.exp(-deltaE / this.temperature * 100.0)) {
                    this.energy = assessment;
                    current = rotation;
                    if (assessment > this.solutionE) {
                        this.solutionE = assessment;
                        this.solution = new Vector2f(rotation.x, rotation.y);
                        DebugUtil.log("Find a better solution: (" + this.solution.x + ", " + this.solution.y + "), value: " + this.solutionE);
                    }
                }
                this.temperature *= 0.997;
                ++this.iteration;
            }
            this.completed = true;
        }
    }
}

