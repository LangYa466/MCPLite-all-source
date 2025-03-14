/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.enchantment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public enum EnumEnchantmentType {
    ALL,
    ARMOR,
    ARMOR_FEET,
    ARMOR_LEGS,
    ARMOR_TORSO,
    ARMOR_HEAD,
    WEAPON,
    DIGGER,
    FISHING_ROD,
    BREAKABLE,
    BOW;


    public boolean canEnchantItem(Item p_77557_1_) {
        if (this == ALL) {
            return true;
        }
        if (this == BREAKABLE && p_77557_1_.isDamageable()) {
            return true;
        }
        if (p_77557_1_ instanceof ItemArmor) {
            if (this == ARMOR) {
                return true;
            }
            ItemArmor itemarmor = (ItemArmor)p_77557_1_;
            return itemarmor.armorType == 0 ? this == ARMOR_HEAD : (itemarmor.armorType == 2 ? this == ARMOR_LEGS : (itemarmor.armorType == 1 ? this == ARMOR_TORSO : (itemarmor.armorType == 3 ? this == ARMOR_FEET : false)));
        }
        return p_77557_1_ instanceof ItemSword ? this == WEAPON : (p_77557_1_ instanceof ItemTool ? this == DIGGER : (p_77557_1_ instanceof ItemBow ? this == BOW : (p_77557_1_ instanceof ItemFishingRod ? this == FISHING_ROD : false)));
    }
}

