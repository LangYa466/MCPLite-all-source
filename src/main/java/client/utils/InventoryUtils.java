/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.utils.BlockUtils;
import client.utils.ItemUtils;
import client.utils.MinecraftInstance;
import client.utils.PacketUtils;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

public final class InventoryUtils
extends MinecraftInstance {
    public static final List<Block> BLOCK_BLACKLIST = Arrays.asList(Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest, Blocks.trapped_chest, Blocks.anvil, Blocks.sand, Blocks.web, Blocks.torch, Blocks.crafting_table, Blocks.furnace, Blocks.waterlily, Blocks.dispenser, Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.noteblock, Blocks.dropper, Blocks.tnt, Blocks.standing_banner, Blocks.wall_banner, Blocks.redstone_torch);

    public static int findItem(int startSlot, int endSlot, Item item) {
        for (int i = startSlot; i < endSlot; ++i) {
            ItemStack stack = InventoryUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack == null || stack.getItem() != item) continue;
            return i;
        }
        return -1;
    }

    public static int findItem(int startSlot, int endSlot, Class<?> item) {
        for (int i = startSlot; i < endSlot; ++i) {
            ItemStack stack = InventoryUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack == null || !stack.getItem().getClass().isAssignableFrom(item)) continue;
            return i;
        }
        return -1;
    }

    public static int findItem(int startSlot, int endSlot, Item item, List<Integer> blackList) {
        block0: for (int i = startSlot; i < endSlot; ++i) {
            ItemStack stack = InventoryUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            for (int no : blackList) {
                if (no != i) continue;
                continue block0;
            }
            if (stack == null || stack.getItem() != item || blackList.contains(i)) continue;
            return i;
        }
        return -1;
    }

    public static int findItem(int startSlot, int endSlot, List<Integer> blackList, Class<?> ... item) {
        block0: for (int i = startSlot; i < endSlot; ++i) {
            ItemStack stack = InventoryUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            Iterator<Integer> classArray = blackList.iterator();
            while (classArray.hasNext()) {
                int no = classArray.next();
                if (no != i) continue;
                continue block0;
            }
            for (Class<?> clazz : item) {
                if (stack == null || !clazz.isAssignableFrom(stack.getItem().getClass())) continue;
                if (stack.stackSize == 0 || clazz == ItemBlock.class && BlockUtils.isUnwantedBlock(((ItemBlock)stack.getItem()).getBlock())) continue block0;
                return i;
            }
        }
        return -1;
    }

    public static boolean hasSpaceHotbar() {
        for (int i = 36; i < 45; ++i) {
            ItemStack itemStack = InventoryUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack != null) continue;
            return true;
        }
        return false;
    }

    public static int findAutoBlockBlock() {
        ItemBlock itemBlock;
        Block block;
        ItemStack itemStack;
        int i;
        for (i = 36; i < 45; ++i) {
            itemStack = InventoryUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock) || itemStack.stackSize <= 0 || !(block = (itemBlock = (ItemBlock)itemStack.getItem()).getBlock()).isFullCube() || BLOCK_BLACKLIST.contains(block)) continue;
            return i;
        }
        for (i = 36; i < 45; ++i) {
            itemStack = InventoryUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock) || itemStack.stackSize <= 0 || BLOCK_BLACKLIST.contains(block = (itemBlock = (ItemBlock)itemStack.getItem()).getBlock())) continue;
            return i;
        }
        return -1;
    }

    public static void windowClick(Minecraft mc, int slotId, int mouseButtonClicked, ClickType mode2) {
        PacketUtils.sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slotId, mouseButtonClicked, mode2.ordinal(), mc.thePlayer);
        PacketUtils.sendPacketNoEvent(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
    }

    public static int findAutoBlockBlockWith0Stack() {
        ItemBlock itemBlock;
        Block block;
        ItemStack itemStack;
        int i;
        for (i = 36; i < 45; ++i) {
            itemStack = InventoryUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock) || !(block = (itemBlock = (ItemBlock)itemStack.getItem()).getBlock()).isFullCube() || BLOCK_BLACKLIST.contains(block)) continue;
            return i;
        }
        for (i = 36; i < 45; ++i) {
            itemStack = InventoryUtils.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock) || BLOCK_BLACKLIST.contains(block = (itemBlock = (ItemBlock)itemStack.getItem()).getBlock())) continue;
            return i;
        }
        return -1;
    }

    public static double getSwordDamage(ItemStack sword) {
        return (double)((ItemSword)sword.getItem()).attackDamage + 1.25 * (double)ItemUtils.getEnchantment(sword, Enchantment.sharpness);
    }

    public static enum ClickType {
        CLICK,
        SHIFT_CLICK,
        SWAP_WITH_HOT_BAR_SLOT,
        PLACEHOLDER,
        DROP_ITEM;

    }
}

