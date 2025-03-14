/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerFoliage;

public class FoliageColorReloadListener
implements IResourceManagerReloadListener {
    private static final ResourceLocation LOC_FOLIAGE_PNG = new ResourceLocation("textures/colormap/foliage.png");

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        try {
            ColorizerFoliage.setFoliageBiomeColorizer(TextureUtil.readImageData(resourceManager, LOC_FOLIAGE_PNG));
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

