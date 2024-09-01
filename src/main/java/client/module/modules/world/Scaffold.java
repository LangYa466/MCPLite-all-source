/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.world;

import client.event.events.JumpEvent;
import client.event.events.MotionEvent;
import client.event.events.MoveInputEvent;
import client.event.events.PacketReceiveSyncEvent;
import client.event.events.Render2DEvent;
import client.event.events.StrafeEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.BlockUtils;
import client.utils.ClientUtils;
import client.utils.FallingPlayer;
import client.utils.InventoryUtils;
import client.utils.MSTimer;
import client.utils.MovementUtils;
import client.utils.PlayerUtils;
import client.utils.rotation.RotationPriority;
import client.utils.rotation.RotationSetter;
import client.utils.rotation.RotationUtils;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class Scaffold
extends Module {
    @Settings
    private boolean telly = false;
    @Settings(list={"Normal", "Strict"})
    private String precision = "Normal";
    @Settings(list={"NONE", "Novo", "Opal"})
    private String hypixel = "NONE";
    @Settings(list={"Normal", "Back"})
    private String rotationMode = "Normal";
    @Settings(name="Grim1_12_2Bypass")
    public boolean grimTest = false;
    @Settings(maxValue=10.0)
    public int airTickDelay = 3;
    @Settings(list={"Legit", "Switch"})
    String autoBlockMode = "Legit";
    @Settings
    private boolean parkour = false;
    @Settings
    private boolean towers = false;
    @Settings
    private boolean jump = false;
    @Settings
    private boolean sameY = false;
    @Settings
    public boolean keepRot = false;
    @Settings
    private boolean forHytBedWars = false;
    @Settings
    private boolean swing = false;
    @Settings(list={"NONE", "Simple", "XXX"})
    private String blocksRenderMode = "NONE";
    @Settings
    private boolean bypassDRP = false;
    @Settings
    private boolean Debug = false;
    @Settings
    private boolean BlockAlert = true;
    @Settings(maxValue=64.0)
    public int BlockAlertValue = 10;
    @Settings(maxValue=64.0)
    public int StopMoveValue = 3;
    @Settings
    private boolean StopMoveAlert = true;
    @Settings(maxValue=20.0)
    public int AlertDelay = 15;
    private int blocksPlaced;
    private BlockPos targetBlock;
    private boolean tower = false;
    private int ticks = 0;
    private int holdticks = 0;
    private EnumFacing targetFacing;
    private Vector2f targetRotation;
    private Vec3 targetVec;
    private int playerIntY = 0;
    private int airTick = 0;
    private int t = 0;
    private int t2 = 0;
    private boolean isUpTelly = false;
    private boolean stopMove = false;
    private boolean eatingBlock = false;
    private MSTimer eating = new MSTimer();
    private int inHandSlot = 0;
    private ItemStack itemStack = null;
    private int sprintTicks;
    private boolean placing;
    private double placeY;
    private HashMap<BlockPos, EnumFacing> info;
    private boolean overAir;
    private boolean jumpTick;
    private double oldY;
    private BlockPos lastPlacePosition = new BlockPos(0, 0, 0);

    public Scaffold() {
        super("Scaffold", 34, ModuleType.WORLD);
    }

    public void onTower() {
        boolean isKeyDown;
        boolean bl = isKeyDown = Scaffold.mc.gameSettings.keyBindJump.isKeyDown() && MovementUtils.isMove();
        if (isKeyDown) {
            ++this.holdticks;
            this.tower = true;
            ++this.ticks;
            if (Scaffold.mc.thePlayer.onGround) {
                this.ticks = 0;
            }
            if (this.holdticks < 19) {
                BlockPos pos = Scaffold.mc.thePlayer.getPosition();
                Scaffold.mc.thePlayer.motionY = 0.41965;
                float speed = 0.241f;
                EntityPlayerSP thePlayer = Scaffold.mc.thePlayer;
                float rotationYaw = thePlayer.rotationYaw;
                if (MovementUtils.isMoveKeybind()) {
                    float yaw = (float)Math.toRadians(rotationYaw);
                    thePlayer.motionX = -Math.sin(yaw) * (double)speed;
                    thePlayer.motionZ = Math.cos(yaw) * (double)speed;
                }
                if (this.ticks == 1) {
                    Scaffold.mc.thePlayer.motionY = 0.33;
                }
                if (this.ticks == 2) {
                    Scaffold.mc.thePlayer.motionY = 1.0 - Scaffold.mc.thePlayer.posY % 1.0;
                }
                if (this.ticks == 3) {
                    this.ticks = 0;
                }
            }
        }
        if (this.ticks >= 3) {
            this.ticks = 0;
        }
        if (!(!this.tower || Scaffold.mc.gameSettings.keyBindJump.isKeyDown() && Scaffold.mc.gameSettings.keyBindSprint.isKeyDown())) {
            Scaffold.mc.thePlayer.motionX = 0.0;
            this.ticks = 0;
            this.tower = false;
        }
        if (this.holdticks > 23 && Scaffold.mc.thePlayer.onGround) {
            this.holdticks = 0;
        }
    }

    @Override
    public void onEnable() {
        this.airTick = 0;
        this.t = 0;
        this.t2 = 0;
        if (!Scaffold.mc.thePlayer.onGround) {
            this.airTick = 114514;
            this.isUpTelly = true;
        }
        this.playerIntY = (int)(Scaffold.mc.thePlayer.posY + 114514.0);
    }

    @Override
    public void onDisable() {
        RotationSetter.setCurrentPriority(RotationPriority.VERY_LOW);
        RotationSetter.setRotation(new Vector2f(Scaffold.mc.thePlayer.rotationYaw, Scaffold.mc.thePlayer.rotationPitch), 0);
        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(Scaffold.mc.thePlayer.inventory.currentItem));
        if (this.stopMove) {
            MovementUtils.resetMove();
            this.stopMove = false;
        }
        Scaffold.mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(Scaffold.mc.gameSettings.keyBindJump);
        MovementUtils.resetMove();
    }

    private void hypixelJumpScaffold() {
        if (Scaffold.mc.thePlayer.onGround || GameSettings.isKeyDown(Scaffold.mc.gameSettings.keyBindJump)) {
            this.placeY = Scaffold.mc.thePlayer.posY;
            this.oldY = this.placeY - 0.05;
            Scaffold.mc.gameSettings.keyBindJump.pressed = true;
        } else if (!GameSettings.isKeyDown(Scaffold.mc.gameSettings.keyBindJump)) {
            Scaffold.mc.gameSettings.keyBindJump.pressed = false;
        }
        if (this.hypixel.equalsIgnoreCase("Novo")) {
            BlockPos pos = new BlockPos(Scaffold.mc.thePlayer.posX, this.placeY - 1.0, Scaffold.mc.thePlayer.posZ);
            this.info = BlockUtils.getBlockInfo(pos, 3);
            this.overAir = BlockUtils.isCantStand(pos);
            if (!this.overAir) {
                pos = new BlockPos(Scaffold.mc.thePlayer.posX - Scaffold.mc.thePlayer.motionX * 4.0, this.placeY, Scaffold.mc.thePlayer.posZ - Scaffold.mc.thePlayer.motionZ * 4.0);
                this.info = BlockUtils.getBlockInfo(pos, 3);
                this.overAir = BlockUtils.isCantStand(pos);
                this.placing = true;
            }
            if (BlockUtils.isCantStand(pos) && this.info != null) {
                Vec3 vec3 = BlockUtils.getVec3ClosestFromRots((BlockPos)this.info.keySet().toArray()[0], (EnumFacing)this.info.values().toArray()[0], true, RotationSetter.getCurrentRotation().x, RotationSetter.getCurrentRotation().y);
                float[] rots = RotationUtils.getRotationsToPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord);
                RotationSetter.setRotation(new Vector2f(rots[0], rots[1]), 1);
                this.checkAndPlace(vec3);
                this.placing = true;
            } else {
                this.placing = false;
            }
            Scaffold.mc.gameSettings.keyBindUseItem.pressed = false;
            Scaffold.mc.objectMouseOver = null;
        } else if (this.hypixel.equalsIgnoreCase("Opal")) {
            BlockPos pos = new BlockPos(Scaffold.mc.thePlayer.posX, this.placeY - 1.0, Scaffold.mc.thePlayer.posZ);
            this.info = BlockUtils.getBlockInfo(pos, 3);
            this.overAir = BlockUtils.isCantStand(pos);
            if (!this.jumpTick) {
                Scaffold.mc.thePlayer.motionZ = 0.0;
                Scaffold.mc.thePlayer.motionX *= 0.0;
            }
            if (this.sprintTicks >= 1) {
                this.jumpTick = true;
            }
            if (Scaffold.mc.thePlayer.motionY + Scaffold.mc.thePlayer.posY < this.placeY + 2.0 && Scaffold.mc.thePlayer.motionY < -0.15 && this.jumpTick) {
                pos = new BlockPos(Scaffold.mc.thePlayer.posX - Scaffold.mc.thePlayer.motionX, this.placeY, Scaffold.mc.thePlayer.posZ - Scaffold.mc.thePlayer.motionZ);
            } else if (!this.jumpTick) {
                this.placeY = this.oldY;
            }
            if (this.jumpTick) {
                this.info = BlockUtils.getBlockInfo(pos, 3);
                this.overAir = BlockUtils.isCantStand(pos);
                this.placing = true;
            }
            if (this.info != null && this.jumpTick) {
                Vec3 vec3 = BlockUtils.getVec3ClosestFromRots((BlockPos)this.info.keySet().toArray()[0], (EnumFacing)this.info.values().toArray()[0], true, RotationSetter.getCurrentRotation().x, RotationSetter.getCurrentRotation().y);
                float[] rots = RotationUtils.getRotationsToPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord);
                RotationSetter.setRotation(new Vector2f(Scaffold.mc.thePlayer.rotationYaw + 180.0f, rots[1]), 1);
                this.checkAndPlace(vec3);
                this.placing = true;
            } else {
                this.placing = false;
            }
            Scaffold.mc.gameSettings.keyBindUseItem.pressed = false;
            Scaffold.mc.objectMouseOver = null;
        }
    }

    public int getBlocksAmount() {
        if (ClientUtils.nullCheck()) {
            return 0;
        }
        AtomicInteger amount = new AtomicInteger();
        Scaffold.mc.thePlayer.inventoryContainer.inventorySlots.forEach(slot -> {
            if (slot.getHasStack() && slot.getStack().getItem() instanceof ItemBlock && slot.slotNumber < 45 && slot.slotNumber > 35) {
                amount.addAndGet(slot.getStack().stackSize);
            }
        });
        return amount.get();
    }

    @Override
    public void onTick() {
        boolean shouldFindNewBlock;
        ++this.t;
        ++this.t2;
        if (this.Debug) {
            ClientUtils.displayChatMessage("[scaffold debug]\u5f53\u524d\u65b9\u5757\u6570\u91cf: " + this.getBlocksAmount());
            ClientUtils.displayChatMessage("[scaffold debug]t=" + this.t);
            ClientUtils.displayChatMessage("[scaffold debug]t2=" + this.t2);
        }
        if (this.BlockAlert && this.getBlocksAmount() < this.BlockAlertValue && this.t >= this.AlertDelay) {
            ClientUtils.displayChatMessage("[scaffold]\u8b66\u544a \u5f53\u524d\u65b9\u5757\u8fc7\u5c11: " + this.getBlocksAmount() + " < " + this.BlockAlertValue);
            this.t = 0;
        }
        if (this.getBlocksAmount() <= this.StopMoveValue) {
            if (this.StopMoveAlert && this.t2 >= this.AlertDelay) {
                ClientUtils.displayChatMessage("[scaffold]\u8b66\u544a \u5f53\u524d\u65b9\u5757\u8fc7\u5c11 \u5df2\u505c\u6b62\u79fb\u52a8: " + this.getBlocksAmount() + " <= " + this.StopMoveValue);
                this.t2 = 0;
            }
            MovementUtils.cancelMove();
        } else {
            MovementUtils.resetMove();
        }
        if (ClientUtils.nullCheck()) {
            return;
        }
        if (this.eatingBlock && this.eating.hasPassed(1000L)) {
            this.eatingBlock = false;
            this.grimTest = false;
            this.keepRot = false;
        }
        if (this.stopMove && PlayerUtils.onBlock() && MovementUtils.cancelMove) {
            this.stopMove = false;
            MovementUtils.resetMove();
        }
        int lastI = -1;
        ItemStack heldItem = Scaffold.mc.thePlayer.getHeldItem();
        boolean bl = shouldFindNewBlock = heldItem == null || !(heldItem.getItem() instanceof ItemBlock) || heldItem.stackSize == 0 || BlockUtils.isUnwantedBlock(((ItemBlock)heldItem.getItem()).getBlock());
        if (shouldFindNewBlock) {
            for (int i = 0; i < 9; ++i) {
                Block block;
                ItemStack stack = Scaffold.mc.thePlayer.inventory.getStackInSlot(i);
                if (stack == null || !(stack.getItem() instanceof ItemBlock) || BlockUtils.isUnwantedBlock(block = ((ItemBlock)stack.getItem()).getBlock()) || stack.stackSize <= 0) continue;
                lastI = i;
                break;
            }
        }
        if (lastI != -1) {
            this.inHandSlot = Scaffold.mc.thePlayer.inventory.currentItem;
            if ("legit".equalsIgnoreCase(this.autoBlockMode)) {
                Scaffold.mc.thePlayer.inventory.currentItem = lastI;
                Scaffold.mc.playerController.syncCurrentPlayItem();
                this.itemStack = Scaffold.mc.thePlayer.getHeldItem();
            } else if ("switch".equalsIgnoreCase(this.autoBlockMode)) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(lastI));
                this.itemStack = Scaffold.mc.thePlayer.inventory.getStackInSlot(lastI);
            }
        }
        if (!this.hypixel.equalsIgnoreCase("none")) {
            this.hypixelJumpScaffold();
            return;
        }
        if (this.towers) {
            this.onTower();
        }
        if (this.telly) {
            if (Scaffold.mc.thePlayer.onGround) {
                this.lastPlacePosition = Scaffold.mc.thePlayer.getPosition();
                RotationSetter.setCurrentPriority(RotationPriority.VERY_LOW);
                RotationSetter.setRotation(new Vector2f(Scaffold.mc.thePlayer.rotationYaw, Scaffold.mc.thePlayer.rotationPitch), 0, RotationPriority.VERY_LOW);
                this.isUpTelly = GameSettings.isKeyDown(Scaffold.mc.gameSettings.keyBindJump);
                this.playerIntY = (int)Scaffold.mc.thePlayer.posY;
                if (this.airTick > 0) {
                    this.airTick = 0;
                }
            } else {
                ++this.airTick;
                if (this.grimTest) {
                    if (this.airTick >= this.airTickDelay) {
                        this.checkAndPlace();
                        this.searchAndRotation();
                    }
                } else if (this.airTick >= this.airTickDelay || this.forHytBedWars) {
                    this.searchAndRotation();
                }
            }
        } else if (this.grimTest) {
            this.checkAndPlace();
            this.searchAndRotation();
        } else {
            this.searchAndRotation();
            this.checkAndPlace();
        }
    }

    @Override
    public void onJump(JumpEvent event) {
        if (this.hypixel.equalsIgnoreCase("Nove") || this.hypixel.equalsIgnoreCase("Opal")) {
            ++this.sprintTicks;
            if (this.sprintTicks % 2 == 0) {
                event.setBoostAmount(0.201f);
            }
            if (this.sprintTicks <= 1) {
                return;
            }
            event.setBoosting(true);
        }
    }

    @Override
    public void onStrafe(StrafeEvent event) {
        if (this.hypixel.equalsIgnoreCase("Nove") || this.hypixel.equalsIgnoreCase("Opal")) {
            if (Scaffold.mc.thePlayer.onGround || Scaffold.mc.thePlayer.isSprinting()) {
                // empty if block
            }
            Scaffold.mc.gameSettings.keyBindSprint.pressed = MovementUtils.isMove();
            Scaffold.mc.thePlayer.setSprinting(MovementUtils.isMove());
            if (!Scaffold.mc.thePlayer.onGround || !Scaffold.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                return;
            }
            float speed = (float)(Scaffold.mc.thePlayer.isSprinting() ? 0.13 : 0.1);
            float f4 = Scaffold.mc.theWorld.getBlockState((BlockPos)new BlockPos((int)MathHelper.floor_double((double)Scaffold.mc.thePlayer.posX), (int)(MathHelper.floor_double((double)Scaffold.mc.thePlayer.getEntityBoundingBox().minY) - 1), (int)MathHelper.floor_double((double)Scaffold.mc.thePlayer.posZ))).getBlock().slipperiness * 0.91f;
            float f = 0.16277136f / (f4 * f4 * f4);
            float f5 = speed * f;
            event.setFriction(f5);
        }
    }

    @Override
    public void onUpdate() {
        if (ClientUtils.nullCheck()) {
            return;
        }
        if (Arrays.stream(Scaffold.mc.thePlayer.inventory.mainInventory).noneMatch(e -> e != null && this.itemStack != null && e.getItem().equals(this.itemStack.getItem()))) {
            this.itemStack = null;
        }
        if (this.parkour) {
            boolean bl = Scaffold.mc.gameSettings.keyBindJump.pressed = Scaffold.mc.thePlayer.onGround && !Scaffold.mc.thePlayer.isSneaking() && !Scaffold.mc.gameSettings.keyBindSneak.isKeyDown() && (Scaffold.mc.theWorld.getCollidingBoundingBoxes(Scaffold.mc.thePlayer, Scaffold.mc.thePlayer.getEntityBoundingBox().offset(0.0, -0.5, 0.0).expand(-0.001, 0.001, -0.001)).isEmpty() || Scaffold.mc.theWorld.getCollidingBoundingBoxes(Scaffold.mc.thePlayer, Scaffold.mc.thePlayer.getEntityBoundingBox().offset(0.0, -0.5, 0.0).expand(-0.001, 0.001, -0.001)).isEmpty());
        }
        if (this.jump) {
            Scaffold.mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(Scaffold.mc.gameSettings.keyBindForward) ? true : GameSettings.isKeyDown(Scaffold.mc.gameSettings.keyBindJump);
        }
        if (!(this.grimTest || !this.hypixel.equalsIgnoreCase("None") || this.airTick < this.airTickDelay || this.forHytBedWars && this.airTick < 4)) {
            this.checkAndPlace();
        }
    }

    @Override
    public void onMovementInput(MoveInputEvent event) {
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        if (event.getPacket() instanceof S22PacketMultiBlockChange && ((S22PacketMultiBlockChange)event.getPacket()).getChangedBlocks().length == 1) {
            this.eating.reset();
            ClientUtils.displayChatMessage("卡方块");

            this.grimTest = true;
            this.keepRot = true;
            this.eatingBlock = true;

            if (!this.stopMove) {
                MovementUtils.cancelMove();
                this.stopMove = true;
            }
        }
    }

    private void checkAndPlace() {
        this.checkAndPlace(null);
    }

    private void checkAndPlace(Vec3 pos) {
        FallingPlayer fallingPlayer;
        if (!this.hypixel.equalsIgnoreCase("none")) {
            if (this.info != null && Scaffold.mc.thePlayer.getHeldItem() != null && Scaffold.mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock && Scaffold.mc.playerController.onPlayerRightClick(Scaffold.mc.thePlayer, Scaffold.mc.theWorld, Scaffold.mc.thePlayer.getHeldItem(), (BlockPos)this.info.keySet().toArray()[0], (EnumFacing)this.info.values().toArray()[0], pos)) {
                Scaffold.mc.thePlayer.swingItem();
                ++this.blocksPlaced;
            }
            return;
        }
        if (this.targetBlock == null || this.targetFacing == null || this.targetVec == null) {
            if (this.stopMove) {
                this.stopMove = false;
                MovementUtils.resetMove();
            }
            return;
        }
        Vector2f rotation = null;
        rotation = RotationSetter.targetRotation != null ? RotationSetter.targetRotation : new Vector2f(Scaffold.mc.thePlayer.rotationYaw, Scaffold.mc.thePlayer.rotationPitch);
        if (this.grimTest) {
            rotation = RotationSetter.packetRotation;
        }
        if (rotation == null) {
            return;
        }
        EntityPlayerSP player = Scaffold.mc.thePlayer;
        Vec3 eyesPos = new Vec3(player.posX, player.getEntityBoundingBox().minY + (double)player.getEyeHeight(), player.posZ);
        Vec3 rotVec = BlockUtils.getVectorForRotation(rotation);
        Vec3 vector = eyesPos.addVector(rotVec.xCoord * 5.0, rotVec.yCoord * 5.0, rotVec.zCoord * 5.0);
        MovingObjectPosition movingObjectPosition = Scaffold.mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);
        ItemStack stack = null;
        switch (this.autoBlockMode.toLowerCase()) {
            case "legit": {
                stack = Scaffold.mc.thePlayer.getHeldItem();
                break;
            }
            case "switch": {
                stack = this.itemStack;
            }
        }
        if (stack == null || stack.stackSize == 0 || stack.getItem() == null || !(stack.getItem() instanceof ItemBlock)) {
            return;
        }
        if (this.grimTest) {
            if (this.rotationMode.equalsIgnoreCase("back") || movingObjectPosition.getBlockPos().getY() == this.targetBlock.getY() && movingObjectPosition.getBlockPos().getX() == this.targetBlock.getX() && movingObjectPosition.getBlockPos().getZ() == this.targetBlock.getZ()) {
                if (Scaffold.mc.playerController.onPlayerRightClick(Scaffold.mc.thePlayer, Scaffold.mc.theWorld, stack, this.targetBlock, this.targetFacing, this.targetVec)) {
                    fallingPlayer = new FallingPlayer(Scaffold.mc.thePlayer);
                    fallingPlayer.findCollision(2);
                    if (fallingPlayer.y < (double)this.targetBlock.getY() && !PlayerUtils.onBlock() && !MovementUtils.cancelMove) {
                        this.stopMove = true;
                        MovementUtils.cancelMove();
                    }
                    if (this.swing) {
                        Scaffold.mc.thePlayer.swingItem();
                    }
                    this.lastPlacePosition = this.targetBlock;
                } else if (this.stopMove) {
                    this.stopMove = false;
                    MovementUtils.resetMove();
                }
            }
        } else if (this.rotationMode.equalsIgnoreCase("back") || movingObjectPosition.getBlockPos().getY() == this.targetBlock.getY() && movingObjectPosition.getBlockPos().getX() == this.targetBlock.getX() && movingObjectPosition.getBlockPos().getZ() == this.targetBlock.getZ() && (!this.precision.equalsIgnoreCase("strict") || movingObjectPosition.sideHit == this.targetFacing)) {
            if (Scaffold.mc.playerController.onPlayerRightClick(Scaffold.mc.thePlayer, Scaffold.mc.theWorld, stack, this.targetBlock, this.targetFacing, this.targetVec)) {
                fallingPlayer = new FallingPlayer(Scaffold.mc.thePlayer);
                fallingPlayer.findCollision(2);
                if (fallingPlayer.y < (double)this.targetBlock.getY() && !PlayerUtils.onBlock() && !MovementUtils.cancelMove) {
                    this.stopMove = true;
                    MovementUtils.cancelMove();
                }
                if (this.swing) {
                    Scaffold.mc.thePlayer.swingItem();
                }
                this.lastPlacePosition = this.targetBlock;
            } else if (this.stopMove) {
                this.stopMove = false;
                MovementUtils.resetMove();
            }
        }
    }

    private void searchAndRotation() {
        Vec3 vec3 = null;
        if (!this.hypixel.equalsIgnoreCase("none")) {
            return;
        }
        if (this.rotationMode.equalsIgnoreCase("back")) {
            if (this.keepRot) {
                RotationSetter.setRotation(new Vector2f(Scaffold.mc.thePlayer.rotationYaw - 180.0f, 75.0f), 100, RotationPriority.VERY_HIGH);
            } else {
                RotationSetter.setRotation(new Vector2f(Scaffold.mc.thePlayer.rotationYaw - 180.0f, 75.0f), 0, RotationPriority.VERY_HIGH);
            }
        }
        EntityPlayerSP player = Scaffold.mc.thePlayer;
        WorldClient world = Scaffold.mc.theWorld;
        double posX = player.posX;
        double posZ = player.posZ;
        double minY = player.getEntityBoundingBox().minY;
        vec3 = this.grimTest ? BlockUtils.getPlacePossibility(player.motionX * 0.91, player.motionY * 0.91, player.motionZ * 0.91, this.isUpTelly, new BlockPos(0, this.playerIntY, 0)) : BlockUtils.getPlacePossibility(0.0, 0.0, 0.0, this.isUpTelly, new BlockPos(0.0, Math.min((double)(this.playerIntY - 1), Scaffold.mc.thePlayer.getEntityBoundingBox().minY), 0.0));
        if (vec3 == null) {
            return;
        }
        BlockPos pos = new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord);
        if (pos.getY() == this.lastPlacePosition.getY() && Scaffold.mc.thePlayer.getDistance((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) > Scaffold.mc.thePlayer.getDistance((double)this.lastPlacePosition.getX() + 0.5, (double)this.lastPlacePosition.getY() + 0.5, (double)this.lastPlacePosition.getZ() + 0.5)) {
            return;
        }
        if (!Scaffold.mc.theWorld.getBlockState(pos).getBlock().getMaterial().isReplaceable()) {
            return;
        }
        if (this.grimTest) {
            posX += player.motionX * 0.91;
            minY += player.motionY * 0.91;
            posZ += player.motionZ * 0.91;
        }
        Vector2f strictRotation = new Vector2f(0.0f, 0.0f);
        for (EnumFacing facingType : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(facingType);
            if (!BlockUtils.canBeClick(neighbor)) continue;
            Vec3 dirVec = new Vec3(facingType.getDirectionVec());
            for (double xSearch = 0.5; xSearch <= 0.5; xSearch += 0.01) {
                for (double ySearch = 0.5; ySearch <= 0.5; ySearch += 0.01) {
                    double zSearch = 0.5;
                    while (zSearch <= 0.5) {
                        double diff;
                        Vec3 eyesPos = new Vec3(posX, minY + (double)player.getEyeHeight(), posZ);
                        Vec3 posVec = new Vec3(pos).addVector(xSearch, ySearch, zSearch);
                        Vec3 hitVec = posVec.add(new Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5));
                        double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        if (eyesPos.distanceTo(hitVec) > 5.0 || distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) || world.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null) {
                            zSearch += 0.01;
                            continue;
                        }
                        double diffX = hitVec.xCoord - eyesPos.xCoord;
                        double diffY = hitVec.yCoord - eyesPos.yCoord;
                        double diffZ = hitVec.zCoord - eyesPos.zCoord;
                        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
                        if (facingType != EnumFacing.UP && facingType != EnumFacing.DOWN && (diff = facingType == EnumFacing.NORTH || facingType == EnumFacing.SOUTH ? Math.abs(diffZ) : Math.abs(diffX)) < 0.0) {
                            zSearch += 0.01;
                            continue;
                        }
                        Vector2f rotation = new Vector2f(MathHelper.wrapAngleTo180_float((float)(Math.toDegrees(MathHelper.atan2(diffZ, diffX)) - 90.0)), MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(MathHelper.atan2(diffY, diffXZ)))));
                        if (this.bypassDRP) {
                            rotation.x += (float)(new Random(System.currentTimeMillis()).nextInt() % 50 * 50 % 165 * 360);
                        }
                        Vec3 rotVec = BlockUtils.getVectorForRotation(rotation);
                        Vec3 vector = eyesPos.addVector(rotVec.xCoord * 5.0, rotVec.yCoord * 5.0, rotVec.zCoord * 5.0);
                        MovingObjectPosition obj = world.rayTraceBlocks(eyesPos, vector, false, false, true);
                        if (obj == null) continue;
                        if (obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || obj.getBlockPos().getX() != neighbor.getX() || obj.getBlockPos().getZ() != neighbor.getZ() || obj.getBlockPos().getY() != neighbor.getY() || obj.sideHit != facingType.getOpposite()) {
                            zSearch += 0.01;
                            continue;
                        }
                        this.targetBlock = neighbor;
                        this.targetFacing = facingType.getOpposite();
                        this.targetVec = obj.hitVec;
                        strictRotation = rotation;
                        zSearch += 0.01;
                    }
                }
            }
        }
        if (this.targetBlock == null || this.targetFacing == null || this.targetVec == null) {
            return;
        }
        if (this.precision.equalsIgnoreCase("normal")) {
            Vec3 eyesPos = Scaffold.mc.thePlayer.getPositionEyes(1.0f);
            Vec3 hitVec = new Vec3((double)this.targetBlock.getX() + 0.5, (double)this.targetBlock.getY() + 0.5, (double)this.targetBlock.getZ() + 0.5);
            double diffX = hitVec.xCoord - eyesPos.xCoord;
            double diffY = hitVec.yCoord - eyesPos.yCoord;
            double diffZ = hitVec.zCoord - eyesPos.zCoord;
            double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
            Vector2f rotation = new Vector2f(MathHelper.wrapAngleTo180_float((float)(Math.toDegrees(MathHelper.atan2(diffZ, diffX)) - 90.0)), MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(MathHelper.atan2(diffY, diffXZ)))));
            pos = this.targetBlock;
            double x = (double)pos.getX() + 0.5;
            double y = (double)pos.getY() + 0.5;
            double z = (double)pos.getZ() + 0.5;
            this.targetVec = new Vec3(x, y, z);
            this.targetRotation = rotation;
            if (this.bypassDRP) {
                this.targetRotation.x -= (float)(new Random(System.currentTimeMillis()).nextInt() % 10 * 5 % 165 * 360);
            }
            Vec3 rotVec = BlockUtils.getVectorForRotation(this.targetRotation);
            Vec3 vector = eyesPos.addVector(rotVec.xCoord * 5.0, rotVec.yCoord * 5.0, rotVec.zCoord * 5.0);
            MovingObjectPosition movingObjectPosition = Scaffold.mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);
            BlockPos overPos = movingObjectPosition.getBlockPos();
            if (overPos == null || overPos.getY() != this.targetBlock.getY() || overPos.getZ() != this.targetBlock.getZ() || overPos.getX() != this.targetBlock.getX()) {
                this.targetRotation = strictRotation;
            }
        }
        // TODO targetRotation空指针 笑死我了 还得我修
        if (targetRotation == null) return;
        if (this.bypassDRP) {
            this.targetRotation.x -= (float)(new Random(System.currentTimeMillis()).nextInt() % 5000 * 500 % 165 * 360);
        }
        if (this.precision.equalsIgnoreCase("strict")) {
            this.targetRotation = strictRotation;
        }
        if (this.rotationMode.equalsIgnoreCase("back")) {
            if (this.keepRot) {
                RotationSetter.setRotation(new Vector2f(Scaffold.mc.thePlayer.rotationYaw - 180.0f, 75.0f), 100, RotationPriority.VERY_HIGH);
            } else {
                RotationSetter.setRotation(new Vector2f(Scaffold.mc.thePlayer.rotationYaw - 180.0f, 75.0f), 0, RotationPriority.VERY_HIGH);
            }
            return;
        }
        if (this.keepRot || this.forHytBedWars) {
            if (this.bypassDRP) {
                this.targetRotation.x += (float)(new Random(System.currentTimeMillis()).nextInt() % 14514 * 51 % 165 * 360);
            }
            RotationSetter.setRotation(this.targetRotation, 100, RotationPriority.VERY_HIGH);
        } else {
            if (this.bypassDRP) {
                this.targetRotation.x += (float)(new Random(System.currentTimeMillis()).nextInt() % 502 * 545 % 165 * 360);
            }
            RotationSetter.setRotation(this.targetRotation, 0, RotationPriority.VERY_HIGH);
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int amount = 0;
        for (int i = 36; i <= 44; ++i) {
            Item itemStackItem;
            Slot slot = Scaffold.mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack itemStack = slot.getStack();
            if (itemStack == null || !((itemStackItem = itemStack.getItem()) instanceof ItemBlock)) continue;
            Block block = ((ItemBlock)itemStackItem).getBlock();
            ItemStack heldItem = Scaffold.mc.thePlayer.getHeldItem();
            if ((heldItem == null || !heldItem.equals(itemStack)) && (InventoryUtils.BLOCK_BLACKLIST.contains(block) || block instanceof BlockBush)) continue;
            amount += itemStack.stackSize;
        }
        if (this.itemStack != null) {
            switch (this.blocksRenderMode) {
                case "Simple": {
                    float f = (float)this.itemStack.animationsToGo - event.partialTicks;
                    int xPos = scaledResolution.getScaledWidth() / 2 - 20;
                    int yPos = scaledResolution.getScaledHeight() / 2 + 5;
                    GlStateManager.pushMatrix();
                    float f1 = 1.0f + f / 5.0f;
                    GL11.glTranslated(xPos, yPos, 0.0);
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemIntoGUI(this.itemStack, 0, 0);
                    GlStateManager.popMatrix();
                    Scaffold.mc.fontRendererObj.drawStringWithShadow("Blocks: " + amount, scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() / 2 + 10, Color.white.getRGB());
                    break;
                }
            }
        }
    }

    @Override
    public void onMotion(MotionEvent event) {
        if (!event.isPre() && this.autoBlockMode.equalsIgnoreCase("switch")) {
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(Scaffold.mc.thePlayer.inventory.currentItem));
        }
    }

    @Override
    public String getTag() {
        return this.autoBlockMode;
    }
}

