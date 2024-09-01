/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.utils.MinecraftInstance;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class PlayerUtils
extends MinecraftInstance {
    public static boolean onBlock() {
        return PlayerUtils.mc.thePlayer.onGround || !(PlayerUtils.mc.theWorld.getBlockState(PlayerUtils.mc.thePlayer.getPosition().down()).getBlock() instanceof BlockAir);
    }

    public static float getPlayerRelativeBlockHardness(EntityPlayer playerIn, World worldIn, BlockPos pos, int slot) {
        Block block = PlayerUtils.mc.theWorld.getBlockState(pos).getBlock();
        float f = block.getBlockHardness(worldIn, pos);
        return f < 0.0f ? 0.0f : (!PlayerUtils.canHeldItemHarvest(block, slot) ? PlayerUtils.getToolDigEfficiency(block, slot) / f / 100.0f : PlayerUtils.getToolDigEfficiency(block, slot) / f / 30.0f);
    }

    public static boolean canHeldItemHarvest(Block blockIn, int slot) {
        if (blockIn.getMaterial().isToolNotRequired()) {
            return true;
        }
        ItemStack itemstack = PlayerUtils.mc.thePlayer.inventory.getStackInSlot(slot);
        return itemstack != null && itemstack.canHarvestBlock(blockIn);
    }

    public static boolean holdingSword() {
        if (PlayerUtils.mc.thePlayer.getHeldItem() == null) {
            return false;
        }
        return PlayerUtils.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public static boolean isRest(Item item) {
        return item instanceof ItemFood || item instanceof ItemPotion;
    }

    public static boolean isBlockUnder(double height, boolean boundingBox) {
        if (boundingBox) {
            int offset = 0;
            while ((double)offset < height) {
                AxisAlignedBB bb = PlayerUtils.mc.thePlayer.getEntityBoundingBox().offset(0.0, -offset, 0.0);
                if (!PlayerUtils.mc.theWorld.getCollidingBoundingBoxes(PlayerUtils.mc.thePlayer, bb).isEmpty()) {
                    return true;
                }
                offset += 2;
            }
        } else {
            int offset = 0;
            while ((double)offset < height) {
                if (PlayerUtils.blockRelativeToPlayer(0.0, -offset, 0.0).isFullBlock()) {
                    return true;
                }
                ++offset;
            }
        }
        return false;
    }

    public static Block blockRelativeToPlayer(double offsetX, double offsetY, double offsetZ) {
        return PlayerUtils.mc.theWorld.getBlockState(new BlockPos(PlayerUtils.mc.thePlayer).add(offsetX, offsetY, offsetZ)).getBlock();
    }

    public static boolean colorTeam(EntityPlayer sb) {
        String targetName = sb.getDisplayName().getFormattedText().replace("\u00a7r", "");
        String clientName = PlayerUtils.mc.thePlayer.getDisplayName().getFormattedText().replace("\u00a7r", "");
        return targetName.startsWith("\u00a7" + clientName.charAt(1));
    }

    public static boolean armorTeam(EntityPlayer entityPlayer) {
        if (PlayerUtils.mc.thePlayer.inventory.armorInventory[3] != null && entityPlayer.inventory.armorInventory[3] != null) {
            ItemStack myHead = PlayerUtils.mc.thePlayer.inventory.armorInventory[3];
            ItemArmor myItemArmor = (ItemArmor)myHead.getItem();
            ItemStack entityHead = entityPlayer.inventory.armorInventory[3];
            ItemArmor entityItemArmor = (ItemArmor)entityHead.getItem();
            if (String.valueOf(entityItemArmor.getColor(entityHead)).equals("10511680")) {
                return true;
            }
            return myItemArmor.getColor(myHead) == entityItemArmor.getColor(entityHead);
        }
        return false;
    }

    public static boolean scoreTeam(EntityPlayer entityPlayer) {
        return PlayerUtils.mc.thePlayer.isOnSameTeam(entityPlayer);
    }

    public static ItemStack getCurrentItemInSlot(int slot) {
        return slot < 9 && slot >= 0 ? PlayerUtils.mc.thePlayer.inventory.mainInventory[slot] : null;
    }

    public static float getStrVsBlock(Block blockIn, int slot) {
        float f = 1.0f;
        if (PlayerUtils.mc.thePlayer.inventory.mainInventory[slot] != null) {
            f *= PlayerUtils.mc.thePlayer.inventory.mainInventory[slot].getStrVsBlock(blockIn);
        }
        return f;
    }

    public static float getToolDigEfficiency(Block blockIn, int slot) {
        float f = PlayerUtils.getStrVsBlock(blockIn, slot);
        if (f > 1.0f) {
            int i = EnchantmentHelper.getEfficiencyModifier(PlayerUtils.mc.thePlayer);
            ItemStack itemstack = PlayerUtils.getCurrentItemInSlot(slot);
            if (i > 0 && itemstack != null) {
                f += (float)(i * i + 1);
            }
        }
        if (PlayerUtils.mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            f *= 1.0f + (float)(PlayerUtils.mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2f;
        }
        if (PlayerUtils.mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
            float f1;
            switch (PlayerUtils.mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
                case 0: {
                    f1 = 0.3f;
                    break;
                }
                case 1: {
                    f1 = 0.09f;
                    break;
                }
                case 2: {
                    f1 = 0.0027f;
                    break;
                }
                default: {
                    f1 = 8.1E-4f;
                }
            }
            f *= f1;
        }
        if (PlayerUtils.mc.thePlayer.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(PlayerUtils.mc.thePlayer)) {
            f /= 5.0f;
        }
        if (!PlayerUtils.mc.thePlayer.onGround) {
            f /= 5.0f;
        }
        return f;
    }
}

