/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.utils.MinecraftInstance;
import client.utils.MovementUtils;
import client.utils.rotation.RotationUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.BlockWeb;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.util.vector.Vector2f;

public class BlockUtils
extends MinecraftInstance {
    private static final List<Block> blacklistedBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever);

    public static boolean canBeClick(BlockPos pos) {
        return MinecraftInstance.mc.theWorld.getBlockState(pos).getBlock().canCollideCheck(MinecraftInstance.mc.theWorld.getBlockState(pos), false) && MinecraftInstance.mc.theWorld.getWorldBorder().contains(pos);
    }

    public static Vec3 getVectorForRotation(Vector2f rotation) {
        float yawCos = (float)Math.cos(-rotation.x * ((float)Math.PI / 180) - (float)Math.PI);
        float yawSin = (float)Math.sin(-rotation.x * ((float)Math.PI / 180) - (float)Math.PI);
        float pitchCos = (float)(-Math.cos(-rotation.y * ((float)Math.PI / 180)));
        float pitchSin = (float)Math.sin(-rotation.y * ((float)Math.PI / 180));
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

    public static Map<BlockPos, Block> searchBlocks(int radius) {
        HashMap<BlockPos, Block> blocks = new HashMap<BlockPos, Block>();
        for (int x = radius; x >= -radius + 1; --x) {
            for (int y = radius; y >= -radius + 1; --y) {
                for (int z = radius; z >= -radius + 1; --z) {
                    BlockPos blockPos = new BlockPos(BlockUtils.mc.thePlayer.posX + (double)x, BlockUtils.mc.thePlayer.posY + (double)y, BlockUtils.mc.thePlayer.posZ + (double)z);
                    Block block = BlockUtils.getBlock(blockPos);
                    if (block == null) continue;
                    blocks.put(blockPos, block);
                }
            }
        }
        return blocks;
    }

    public static Block getBlock(BlockPos blockPos) {
        return BlockUtils.mc.theWorld.getBlockState(blockPos) != null ? BlockUtils.mc.theWorld.getBlockState(blockPos).getBlock() : null;
    }

    public static boolean isUnwantedBlock(Block block) {
        return block instanceof BlockContainer || block instanceof BlockCactus || block instanceof BlockWeb || block instanceof BlockFlower || block instanceof BlockSnow || block instanceof BlockTNT || block instanceof BlockLadder || block instanceof BlockChest;
    }

    public static Vec3 getPlacePossibility(double offsetX, double offsetY, double offsetZ, boolean searchUP, BlockPos lastPos) {
        ArrayList<Vec3> possibilities = new ArrayList<Vec3>();
        ArrayList reals = new ArrayList();
        int range = (int)(6.0 + (Math.abs(offsetX) + Math.abs(offsetZ)));
        Vec3 playerPos = new Vec3(MinecraftInstance.mc.thePlayer.posX + offsetX, MinecraftInstance.mc.thePlayer.posY - 1.0 + offsetY, MinecraftInstance.mc.thePlayer.posZ + offsetZ);
        if (!(MinecraftInstance.mc.theWorld.getBlockState(new BlockPos(playerPos)).getBlock() instanceof BlockAir)) {
            return playerPos;
        }
        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= 0; ++y) {
                for (int z = -range; z <= range; ++z) {
                    Block block = MinecraftInstance.mc.theWorld.getBlockState(new BlockPos(MinecraftInstance.mc.thePlayer).add(x, y, z)).getBlock();
                    if (block instanceof BlockAir) continue;
                    for (int x2 = -1; x2 <= 1; x2 += 2) {
                        possibilities.add(new Vec3(MinecraftInstance.mc.thePlayer.posX + (double)x + (double)x2, MinecraftInstance.mc.thePlayer.posY + (double)y, MinecraftInstance.mc.thePlayer.posZ + (double)z));
                    }
                    for (int y2 = -1; y2 <= 1; y2 += 2) {
                        possibilities.add(new Vec3(MinecraftInstance.mc.thePlayer.posX + (double)x, MinecraftInstance.mc.thePlayer.posY + (double)y + (double)y2, MinecraftInstance.mc.thePlayer.posZ + (double)z));
                    }
                    for (int z2 = -1; z2 <= 1; z2 += 2) {
                        possibilities.add(new Vec3(MinecraftInstance.mc.thePlayer.posX + (double)x, MinecraftInstance.mc.thePlayer.posY + (double)y, MinecraftInstance.mc.thePlayer.posZ + (double)z + (double)z2));
                    }
                }
            }
        }
        possibilities.removeIf(vec3 -> {
            BlockPos blockPos = new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            if (!(MinecraftInstance.mc.theWorld.getBlockState(blockPos).getBlock() instanceof BlockAir)) {
                reals.add(vec3);
            }
            if (BlockUtils.mc.thePlayer.getPosition().getX() == blockPos.getX() && BlockUtils.mc.thePlayer.getPosition().getY() == blockPos.getY() && BlockUtils.mc.thePlayer.getPosition().getZ() == blockPos.getZ()) {
                return true;
            }
            BlockPos position = new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            return MinecraftInstance.mc.thePlayer.getDistance((double)position.getX() + 0.5, (double)position.getY() + 0.5, (double)position.getZ() + 0.5) > 6.0 || !(MinecraftInstance.mc.theWorld.getBlockState(new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord)).getBlock() instanceof BlockAir);
        });
        possibilities.removeIf(e -> {
            boolean hasBlock = false;
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos position;
                if (facing == EnumFacing.UP || facing == EnumFacing.DOWN && !searchUP || BlockUtils.mc.theWorld.getBlockState((position = new BlockPos(e.xCoord, e.yCoord, e.zCoord)).offset(facing)) == null || BlockUtils.mc.theWorld.getBlockState(position.offset(facing)).getBlock() instanceof BlockAir) continue;
                BlockPos facePos = position.offset(facing);
                if (BlockUtils.mc.thePlayer.getDistance((double)position.getX() + 0.5, (double)position.getY() + 0.5, (double)position.getZ() + 0.5) > BlockUtils.mc.thePlayer.getDistance((double)facePos.getX() + 0.5, (double)facePos.getY() + 0.5, (double)facePos.getZ() + 0.5)) {
                    return true;
                }
                hasBlock = true;
            }
            if (e.yCoord > BlockUtils.mc.thePlayer.getEntityBoundingBox().minY && !searchUP) {
                return true;
            }
            return !hasBlock;
        });
        if (possibilities.isEmpty()) {
            return null;
        }
        possibilities.sort(Comparator.comparingDouble(vec3 -> {
            double d0 = MinecraftInstance.mc.thePlayer.posX + offsetX - vec3.xCoord;
            double d1 = MinecraftInstance.mc.thePlayer.posY - 1.0 + offsetY - vec3.yCoord;
            double d2 = MinecraftInstance.mc.thePlayer.posZ + offsetZ - vec3.zCoord;
            return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
        }));
        return (Vec3)possibilities.get(0);
    }

    public static Block blockRelativeToPlayer(double offsetX, double offsetY, double offsetZ) {
        return MinecraftInstance.mc.theWorld.getBlockState(new BlockPos(MinecraftInstance.mc.thePlayer).add(offsetX, offsetY, offsetZ)).getBlock();
    }

    public static Block block(double x, double y, double z) {
        return MinecraftInstance.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static boolean isCantStand(BlockPos pos) {
        Block block = MinecraftInstance.mc.theWorld.getBlockState(pos).getBlock();
        return block instanceof BlockAir || block instanceof BlockLiquid;
    }

    public static EnumFacing getHorizontalFacing(float yaw) {
        return EnumFacing.getHorizontal(MathHelper.floor_double((double)(yaw * 4.0f / 360.0f) + 0.5) & 3);
    }

    public static Vec3 getVec3ClosestFromRots(BlockPos pos, EnumFacing facing, boolean randomised, float yaw, float pitch) {
        Vec3 originalVec3;
        double smallestDiff = Double.MAX_VALUE;
        Vec3 finalVec3 = null;
        Vec3 modifiedVec3 = originalVec3 = new Vec3(pos);
        if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
            boolean y = facing == EnumFacing.UP;
            double random1 = randomised ? Math.random() * 0.01 - 0.005 : 0.0;
            double random2 = randomised ? Math.random() * 0.04 : 0.0;
            for (double amount1 = 0.05; amount1 <= 0.95; amount1 += 0.025) {
                for (double amount2 = 0.05; amount2 <= 0.95; amount2 += 0.025) {
                    double pitchDiff;
                    modifiedVec3 = originalVec3.addVector(amount1 + random1, 0.0, amount2 + random2);
                    float[] rots = RotationUtils.getRotationsToPosition(modifiedVec3.xCoord, modifiedVec3.yCoord, modifiedVec3.zCoord);
                    double yawDiff = Math.abs(rots[0] - yaw);
                    double diff = Math.hypot(yawDiff, pitchDiff = (double)Math.abs(rots[1] - pitch));
                    if (!(diff < smallestDiff)) continue;
                    smallestDiff = diff;
                    finalVec3 = modifiedVec3;
                }
            }
        } else {
            double random1 = randomised ? Math.random() * 0.01 - 0.005 : 0.0;
            double random2 = randomised ? Math.random() * 0.04 : 0.0;
            for (double amount = 0.05; amount <= 0.95; amount += 0.025) {
                if (facing == EnumFacing.EAST) {
                    modifiedVec3 = originalVec3.addVector(1.0, 0.05 + random2, amount + random1);
                } else if (facing == EnumFacing.WEST) {
                    modifiedVec3 = originalVec3.addVector(0.0, 0.05 + random2, amount + random1);
                } else if (facing == EnumFacing.NORTH) {
                    modifiedVec3 = originalVec3.addVector(amount + random1, 0.05 + random2, 0.0);
                } else if (facing == EnumFacing.SOUTH) {
                    modifiedVec3 = originalVec3.addVector(amount + random1, 0.05 + random2, 1.0);
                }
                float[] rots = RotationUtils.getRotationsToPosition(modifiedVec3.xCoord, modifiedVec3.yCoord, modifiedVec3.zCoord);
                double yawDiff = Math.abs(rots[0] - yaw);
                double pitchDiff = Math.abs(rots[1] - pitch);
                double diff = Math.hypot(yawDiff, pitchDiff);
                if (!(diff < smallestDiff)) continue;
                smallestDiff = diff;
                finalVec3 = modifiedVec3;
            }
        }
        return finalVec3;
    }

    public static HashMap<BlockPos, EnumFacing> getBlockInfo(BlockPos pos, int maxRange) {
        EnumFacing playerDirectionFacing = BlockUtils.getHorizontalFacing((float)MovementUtils.getDirection()).getOpposite();
        ArrayList<EnumFacing> facingValues = new ArrayList<EnumFacing>();
        facingValues.add(playerDirectionFacing);
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == playerDirectionFacing || facing == EnumFacing.UP) continue;
            facingValues.add(facing);
        }
        CopyOnWriteArrayList<BlockPos> aaa = new CopyOnWriteArrayList<BlockPos>();
        aaa.add(pos);
        for (int i = 0; i < maxRange; ++i) {
            ArrayList ccc = new ArrayList(aaa);
            if (!aaa.isEmpty()) {
                for (BlockPos bbbb : aaa) {
                    for (EnumFacing facing : facingValues) {
                        BlockPos n = bbbb.offset(facing);
                        if (BlockUtils.isCantStand(n)) {
                            aaa.add(n);
                            continue;
                        }
                        HashMap<BlockPos, EnumFacing> map = new HashMap<BlockPos, EnumFacing>();
                        map.put(n, facing.getOpposite());
                        return map;
                    }
                }
            }
            for (Object dddd : ccc) {
                aaa.remove(dddd);
            }
            ccc.clear();
        }
        return null;
    }

    public static List<Block> getBlacklistedBlocks() {
        return blacklistedBlocks;
    }
}

