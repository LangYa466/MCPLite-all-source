/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.entity.ai.attributes;

import net.minecraft.entity.ai.attributes.IAttribute;

public abstract class BaseAttribute
implements IAttribute {
    private final IAttribute field_180373_a;
    private final String unlocalizedName;
    private final double defaultValue;
    private boolean shouldWatch;

    protected BaseAttribute(IAttribute p_i45892_1_, String unlocalizedNameIn, double defaultValueIn) {
        this.field_180373_a = p_i45892_1_;
        this.unlocalizedName = unlocalizedNameIn;
        this.defaultValue = defaultValueIn;
        if (unlocalizedNameIn == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        }
    }

    @Override
    public String getAttributeUnlocalizedName() {
        return this.unlocalizedName;
    }

    @Override
    public double getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public boolean getShouldWatch() {
        return this.shouldWatch;
    }

    public BaseAttribute setShouldWatch(boolean shouldWatchIn) {
        this.shouldWatch = shouldWatchIn;
        return this;
    }

    @Override
    public IAttribute func_180372_d() {
        return this.field_180373_a;
    }

    public int hashCode() {
        return this.unlocalizedName.hashCode();
    }

    public boolean equals(Object p_equals_1_) {
        return p_equals_1_ instanceof IAttribute && this.unlocalizedName.equals(((IAttribute)p_equals_1_).getAttributeUnlocalizedName());
    }
}

