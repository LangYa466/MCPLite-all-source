/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAITargetNonTamed<T extends EntityLivingBase>
extends EntityAINearestAttackableTarget {
    private EntityTameable theTameable;

    public EntityAITargetNonTamed(EntityTameable entityIn, Class<T> classTarget, boolean checkSight, Predicate<? super T> targetSelector) {
        super(entityIn, classTarget, 10, checkSight, false, targetSelector);
        this.theTameable = entityIn;
    }

    @Override
    public boolean shouldExecute() {
        return !this.theTameable.isTamed() && super.shouldExecute();
    }
}

