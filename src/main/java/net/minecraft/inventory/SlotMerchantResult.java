/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.village.MerchantRecipe;

public class SlotMerchantResult
extends Slot {
    private final InventoryMerchant theMerchantInventory;
    private EntityPlayer thePlayer;
    private int field_75231_g;
    private final IMerchant theMerchant;

    public SlotMerchantResult(EntityPlayer player, IMerchant merchant, InventoryMerchant merchantInventory, int slotIndex, int xPosition, int yPosition) {
        super(merchantInventory, slotIndex, xPosition, yPosition);
        this.thePlayer = player;
        this.theMerchant = merchant;
        this.theMerchantInventory = merchantInventory;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        if (this.getHasStack()) {
            this.field_75231_g += Math.min(amount, this.getStack().stackSize);
        }
        return super.decrStackSize(amount);
    }

    @Override
    protected void onCrafting(ItemStack stack, int amount) {
        this.field_75231_g += amount;
        this.onCrafting(stack);
    }

    @Override
    protected void onCrafting(ItemStack stack) {
        stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.field_75231_g);
        this.field_75231_g = 0;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
        ItemStack itemstack1;
        ItemStack itemstack;
        this.onCrafting(stack);
        MerchantRecipe merchantrecipe = this.theMerchantInventory.getCurrentRecipe();
        if (merchantrecipe != null && (this.doTrade(merchantrecipe, itemstack = this.theMerchantInventory.getStackInSlot(0), itemstack1 = this.theMerchantInventory.getStackInSlot(1)) || this.doTrade(merchantrecipe, itemstack1, itemstack))) {
            this.theMerchant.useRecipe(merchantrecipe);
            playerIn.triggerAchievement(StatList.timesTradedWithVillagerStat);
            if (itemstack != null && itemstack.stackSize <= 0) {
                itemstack = null;
            }
            if (itemstack1 != null && itemstack1.stackSize <= 0) {
                itemstack1 = null;
            }
            this.theMerchantInventory.setInventorySlotContents(0, itemstack);
            this.theMerchantInventory.setInventorySlotContents(1, itemstack1);
        }
    }

    private boolean doTrade(MerchantRecipe trade, ItemStack firstItem, ItemStack secondItem) {
        ItemStack itemstack = trade.getItemToBuy();
        ItemStack itemstack1 = trade.getSecondItemToBuy();
        if (firstItem != null && firstItem.getItem() == itemstack.getItem()) {
            if (itemstack1 != null && secondItem != null && itemstack1.getItem() == secondItem.getItem()) {
                firstItem.stackSize -= itemstack.stackSize;
                secondItem.stackSize -= itemstack1.stackSize;
                return true;
            }
            if (itemstack1 == null && secondItem == null) {
                firstItem.stackSize -= itemstack.stackSize;
                return true;
            }
        }
        return false;
    }
}

