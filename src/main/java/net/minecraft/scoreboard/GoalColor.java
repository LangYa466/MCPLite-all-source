/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.EnumChatFormatting;

public class GoalColor
implements IScoreObjectiveCriteria {
    private final String goalName;

    public GoalColor(String p_i45549_1_, EnumChatFormatting p_i45549_2_) {
        this.goalName = p_i45549_1_ + p_i45549_2_.getFriendlyName();
        IScoreObjectiveCriteria.INSTANCES.put(this.goalName, this);
    }

    @Override
    public String getName() {
        return this.goalName;
    }

    @Override
    public int setScore(List<EntityPlayer> p_96635_1_) {
        return 0;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
        return IScoreObjectiveCriteria.EnumRenderType.INTEGER;
    }
}

