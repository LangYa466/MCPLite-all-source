/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.inventory;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class InventoryHelper {
    private static final Random RANDOM = new Random();

    public static void dropInventoryItems(World worldIn, BlockPos pos, IInventory inventory) {
        InventoryHelper.dropInventoryItems(worldIn, pos.getX(), pos.getY(), pos.getZ(), inventory);
    }

    public static void dropInventoryItems(World worldIn, Entity entityAt, IInventory inventory) {
        InventoryHelper.dropInventoryItems(worldIn, entityAt.posX, entityAt.posY, entityAt.posZ, inventory);
    }

    private static void dropInventoryItems(World worldIn, double x, double y, double z, IInventory inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = inventory.getStackInSlot(i);
            if (itemstack == null) continue;
            InventoryHelper.spawnItemStack(worldIn, x, y, z, itemstack);
        }
    }

    private static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack) {
        float f = RANDOM.nextFloat() * 0.8f + 0.1f;
        float f1 = RANDOM.nextFloat() * 0.8f + 0.1f;
        float f2 = RANDOM.nextFloat() * 0.8f + 0.1f;
        while (stack.stackSize > 0) {
            int i = RANDOM.nextInt(21) + 10;
            if (i > stack.stackSize) {
                i = stack.stackSize;
            }
            stack.stackSize -= i;
            EntityItem entityitem = new EntityItem(worldIn, x + (double)f, y + (double)f1, z + (double)f2, new ItemStack(stack.getItem(), i, stack.getMetadata()));
            if (stack.hasTagCompound()) {
                entityitem.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
            }
            float f3 = 0.05f;
            entityitem.motionX = RANDOM.nextGaussian() * (double)f3;
            entityitem.motionY = RANDOM.nextGaussian() * (double)f3 + (double)0.2f;
            entityitem.motionZ = RANDOM.nextGaussian() * (double)f3;
            worldIn.spawnEntityInWorld(entityitem);
        }
    }
}

