/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.entity.boss;

import net.minecraft.entity.boss.IBossDisplayData;

public final class BossStatus {
    public static float healthScale;
    public static int statusBarTime;
    public static String bossName;
    public static boolean hasColorModifier;

    public static void setBossStatus(IBossDisplayData displayData, boolean hasColorModifierIn) {
        healthScale = displayData.getHealth() / displayData.getMaxHealth();
        statusBarTime = 100;
        bossName = displayData.getDisplayName().getFormattedText();
        hasColorModifier = hasColorModifierIn;
    }
}

