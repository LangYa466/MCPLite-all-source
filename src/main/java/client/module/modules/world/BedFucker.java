/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.world;

import client.event.events.PacketReceiveSyncEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.BlockUtils;
import client.utils.ClientUtils;
import client.utils.PlayerUtils;
import client.utils.rotation.RotationPriority;
import client.utils.rotation.RotationSetter;
import client.utils.rotation.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import org.lwjgl.util.vector.Vector2f;

public class BedFucker
extends Module {
    @Settings
    public boolean instant = false;
    @Settings
    public boolean throughWalls = false;
    @Settings
    public boolean emptySurrounding = false;
    @Settings
    public boolean rotationOnlyBed = true;
    @Settings
    public boolean rotations = false;
    @Settings
    public boolean whiteListOwnBed = true;
    private Vector3d block;
    private Vector3d lastBlock;
    private Vector3d home;
    private double damage;
    private boolean checkHome;

    public BedFucker() {
        super("BedFucker", 45, ModuleType.WORLD);
    }

    @Override
    public void onTick() {
        if (ClientUtils.nullCheck()) {
            return;
        }
        this.lastBlock = this.block;
        this.block = this.block();
        if (this.block == null) {
            return;
        }
        if (this.rotations) {
            this.rotate();
        }
        if (this.lastBlock == null || !this.lastBlock.equals(this.block)) {
            this.damage = 0.0;
        }
        this.destroy();
    }

    public void rotate() {
        if (this.rotationOnlyBed && !(BlockUtils.block(this.block.x, this.block.y, this.block.z) instanceof BlockBed)) {
            return;
        }
        Vector2f rotations = RotationUtils.calculate(this.block);
        RotationSetter.setRotation(rotations, 0, RotationPriority.HIGH);
    }

    public Vector3d block() {
        if (this.home != null && BedFucker.mc.thePlayer.getDistanceSq(this.home.getX(), this.home.getY(), this.home.getZ()) < 1225.0 && this.whiteListOwnBed) {
            return null;
        }
        Vector3d pos = null;
        for (int x = -5; x <= 5; ++x) {
            for (int y = -5; y <= 5; ++y) {
                for (int z = -5; z <= 5; ++z) {
                    MovingObjectPosition movingObjectPosition;
                    Block block = BlockUtils.blockRelativeToPlayer(x, y, z);
                    Vector3d position = new Vector3d(BedFucker.mc.thePlayer.posX + (double)x, BedFucker.mc.thePlayer.posY + (double)y, BedFucker.mc.thePlayer.posZ + (double)z);
                    if (!(block instanceof BlockBed) || (movingObjectPosition = RotationUtils.rayCast(RotationUtils.calculate(position), 4.5)) == null || movingObjectPosition.hitVec.distanceTo(new Vec3(BedFucker.mc.thePlayer.posX, BedFucker.mc.thePlayer.posY, BedFucker.mc.thePlayer.posZ)) > 4.5) continue;
                    if (!this.throughWalls) {
                        BlockPos blockPos = movingObjectPosition.getBlockPos();
                        if (blockPos != null && !blockPos.equalsVector(position)) {
                            pos = new Vector3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                            continue;
                        }
                    } else if (this.emptySurrounding) {
                        Vector3d addVec = position;
                        double hardness = Double.MAX_VALUE;
                        boolean empty = false;
                        for (int addX = -1; addX <= 1; ++addX) {
                            for (int addY = 0; addY <= 1; ++addY) {
                                for (int addZ = -1; addZ <= 1; ++addZ) {
                                    Block possibleBlock;
                                    if (empty || BedFucker.mc.thePlayer.getDistanceSq(position.getX() + (double)addX, position.getY() + (double)addY, position.getZ() + (double)addZ) + 4.0 > 20.25 || Math.abs(addX) + Math.abs(addY) + Math.abs(addZ) != 1 || (possibleBlock = BlockUtils.block(position.getX() + (double)addX, position.getY() + (double)addY, position.getZ() + (double)addZ)) instanceof BlockBed) continue;
                                    if (possibleBlock instanceof BlockAir) {
                                        empty = true;
                                        continue;
                                    }
                                    double possibleHardness = possibleBlock.getBlockHardness();
                                    if (!(possibleHardness < hardness)) continue;
                                    hardness = possibleHardness;
                                    addVec = position.add(new Vector3d(addX, addY, addZ));
                                }
                            }
                        }
                        if (!empty) {
                            if (addVec.equals(position)) {
                                return null;
                            }
                            return addVec;
                        }
                    }
                    return position;
                }
            }
        }
        return pos;
    }

    public void updateDamage(BlockPos blockPos, double hardness) {
        this.damage += hardness;
        BedFucker.mc.theWorld.sendBlockBreakProgress(BedFucker.mc.thePlayer.getEntityId(), blockPos, (int)(this.damage * 10.0 - 1.0));
    }

    public void destroy() {
        BlockPos blockPos = new BlockPos(this.block.getX(), this.block.getY(), this.block.getZ());
        double hardness = PlayerUtils.getPlayerRelativeBlockHardness(BedFucker.mc.thePlayer, BedFucker.mc.theWorld, blockPos, BedFucker.mc.thePlayer.inventory.currentItem);
        if (this.instant) {
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.UP));
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.UP));
            BedFucker.mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN);
        } else {
            if (this.damage <= 0.0) {
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.UP));
                if (hardness >= 1.0) {
                    BedFucker.mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN);
                    this.damage = 0.0;
                }
                this.updateDamage(blockPos, hardness);
            } else if (this.damage > 1.0) {
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.UP));
                BedFucker.mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN);
                this.damage = 0.0;
                this.updateDamage(blockPos, hardness);
            } else {
                this.updateDamage(blockPos, hardness);
            }
            BedFucker.mc.thePlayer.swingItem();
        }
    }

    @Override
    public void onWorldLoad() {
        this.checkHome = true;
    }

    @Override
    public void onPacketReceiveSync(PacketReceiveSyncEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && this.checkHome) {
            S08PacketPlayerPosLook posLook = (S08PacketPlayerPosLook)event.getPacket();
            double distance = BedFucker.mc.thePlayer.getDistance(posLook.getX(), posLook.getY(), posLook.getZ());
            if (distance > 40.0) {
                this.home = new Vector3d(posLook.getX(), posLook.getY(), posLook.getZ());
            }
            this.checkHome = false;
        }
    }
}

