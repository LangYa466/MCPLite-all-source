/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.client.resources.data;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.resources.data.IMetadataSection;

public class TextureMetadataSection
implements IMetadataSection {
    private final boolean textureBlur;
    private final boolean textureClamp;
    private final List<Integer> listMipmaps;

    public TextureMetadataSection(boolean p_i45102_1_, boolean p_i45102_2_, List<Integer> p_i45102_3_) {
        this.textureBlur = p_i45102_1_;
        this.textureClamp = p_i45102_2_;
        this.listMipmaps = p_i45102_3_;
    }

    public boolean getTextureBlur() {
        return this.textureBlur;
    }

    public boolean getTextureClamp() {
        return this.textureClamp;
    }

    public List<Integer> getListMipmaps() {
        return Collections.unmodifiableList(this.listMipmaps);
    }
}

