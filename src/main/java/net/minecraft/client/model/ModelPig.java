/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ModelQuadruped;

public class ModelPig
extends ModelQuadruped {
    public ModelPig() {
        this(0.0f);
    }

    public ModelPig(float p_i1151_1_) {
        super(6, p_i1151_1_);
        this.head.setTextureOffset(16, 16).addBox(-2.0f, 0.0f, -9.0f, 4, 3, 1, p_i1151_1_);
        this.childYOffset = 4.0f;
    }
}

