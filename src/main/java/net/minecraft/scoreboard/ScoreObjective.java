/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.scoreboard;

import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Scoreboard;

public class ScoreObjective {
    private final Scoreboard theScoreboard;
    private final String name;
    private final IScoreObjectiveCriteria objectiveCriteria;
    private IScoreObjectiveCriteria.EnumRenderType renderType;
    private String displayName;

    public ScoreObjective(Scoreboard theScoreboardIn, String nameIn, IScoreObjectiveCriteria objectiveCriteriaIn) {
        this.theScoreboard = theScoreboardIn;
        this.name = nameIn;
        this.objectiveCriteria = objectiveCriteriaIn;
        this.displayName = nameIn;
        this.renderType = objectiveCriteriaIn.getRenderType();
    }

    public Scoreboard getScoreboard() {
        return this.theScoreboard;
    }

    public String getName() {
        return this.name;
    }

    public IScoreObjectiveCriteria getCriteria() {
        return this.objectiveCriteria;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String nameIn) {
        this.displayName = nameIn;
        this.theScoreboard.onObjectiveDisplayNameChanged(this);
    }

    public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
        return this.renderType;
    }

    public void setRenderType(IScoreObjectiveCriteria.EnumRenderType type) {
        this.renderType = type;
        this.theScoreboard.onObjectiveDisplayNameChanged(this);
    }
}

