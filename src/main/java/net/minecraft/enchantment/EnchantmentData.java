/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.WeightedRandom;

public class EnchantmentData
extends WeightedRandom.Item {
    public final Enchantment enchantmentobj;
    public final int enchantmentLevel;

    public EnchantmentData(Enchantment enchantmentObj, int enchLevel) {
        super(enchantmentObj.getWeight());
        this.enchantmentobj = enchantmentObj;
        this.enchantmentLevel = enchLevel;
    }
}

