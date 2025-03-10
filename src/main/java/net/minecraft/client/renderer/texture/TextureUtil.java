/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.Mipmaps;
import net.optifine.reflect.Reflector;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class TextureUtil {
    private static final Logger logger = LogManager.getLogger();
    private static final IntBuffer dataBuffer = GLAllocation.createDirectIntBuffer(0x400000);
    public static final DynamicTexture missingTexture = new DynamicTexture(16, 16);
    public static final int[] missingTextureData = missingTexture.getTextureData();
    private static final int[] mipmapBuffer;
    private static int[] dataArray;

    public static int glGenTextures() {
        return GlStateManager.generateTexture();
    }

    public static void deleteTexture(int textureId) {
        GlStateManager.deleteTexture(textureId);
    }

    public static int uploadTextureImage(int p_110987_0_, BufferedImage p_110987_1_) {
        return TextureUtil.uploadTextureImageAllocate(p_110987_0_, p_110987_1_, false, false);
    }

    public static void uploadTexture(int textureId, int[] p_110988_1_, int p_110988_2_, int p_110988_3_) {
        TextureUtil.bindTexture(textureId);
        TextureUtil.uploadTextureSub(0, p_110988_1_, p_110988_2_, p_110988_3_, 0, 0, false, false, false);
    }

    public static int[][] generateMipmapData(int p_147949_0_, int p_147949_1_, int[][] p_147949_2_) {
        int[][] aint = new int[p_147949_0_ + 1][];
        aint[0] = p_147949_2_[0];
        if (p_147949_0_ > 0) {
            boolean flag = false;
            for (int i = 0; i < p_147949_2_[0].length; ++i) {
                if (p_147949_2_[0][i] >> 24 != 0) continue;
                flag = true;
                break;
            }
            for (int l1 = 1; l1 <= p_147949_0_; ++l1) {
                if (p_147949_2_[l1] != null) {
                    aint[l1] = p_147949_2_[l1];
                    continue;
                }
                int[] aint1 = aint[l1 - 1];
                int[] aint2 = new int[aint1.length >> 2];
                int j = p_147949_1_ >> l1;
                int k = aint2.length / j;
                int l = j << 1;
                for (int i1 = 0; i1 < j; ++i1) {
                    for (int j1 = 0; j1 < k; ++j1) {
                        int k1 = 2 * (i1 + j1 * l);
                        aint2[i1 + j1 * j] = TextureUtil.blendColors(aint1[k1 + 0], aint1[k1 + 1], aint1[k1 + 0 + l], aint1[k1 + 1 + l], flag);
                    }
                }
                aint[l1] = aint2;
            }
        }
        return aint;
    }

    private static int blendColors(int p_147943_0_, int p_147943_1_, int p_147943_2_, int p_147943_3_, boolean p_147943_4_) {
        return Mipmaps.alphaBlend(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_);
    }

    private static int blendColorComponent(int p_147944_0_, int p_147944_1_, int p_147944_2_, int p_147944_3_, int p_147944_4_) {
        float f = (float)Math.pow((float)(p_147944_0_ >> p_147944_4_ & 0xFF) / 255.0f, 2.2);
        float f1 = (float)Math.pow((float)(p_147944_1_ >> p_147944_4_ & 0xFF) / 255.0f, 2.2);
        float f2 = (float)Math.pow((float)(p_147944_2_ >> p_147944_4_ & 0xFF) / 255.0f, 2.2);
        float f3 = (float)Math.pow((float)(p_147944_3_ >> p_147944_4_ & 0xFF) / 255.0f, 2.2);
        float f4 = (float)Math.pow((double)(f + f1 + f2 + f3) * 0.25, 0.45454545454545453);
        return (int)((double)f4 * 255.0);
    }

    public static void uploadTextureMipmap(int[][] p_147955_0_, int p_147955_1_, int p_147955_2_, int p_147955_3_, int p_147955_4_, boolean p_147955_5_, boolean p_147955_6_) {
        for (int i = 0; i < p_147955_0_.length; ++i) {
            int[] aint = p_147955_0_[i];
            TextureUtil.uploadTextureSub(i, aint, p_147955_1_ >> i, p_147955_2_ >> i, p_147955_3_ >> i, p_147955_4_ >> i, p_147955_5_, p_147955_6_, p_147955_0_.length > 1);
        }
    }

    private static void uploadTextureSub(int p_147947_0_, int[] p_147947_1_, int p_147947_2_, int p_147947_3_, int p_147947_4_, int p_147947_5_, boolean p_147947_6_, boolean p_147947_7_, boolean p_147947_8_) {
        int j;
        int i = 0x400000 / p_147947_2_;
        TextureUtil.setTextureBlurMipmap(p_147947_6_, p_147947_8_);
        TextureUtil.setTextureClamped(p_147947_7_);
        for (int k = 0; k < p_147947_2_ * p_147947_3_; k += p_147947_2_ * j) {
            int l = k / p_147947_2_;
            j = Math.min(i, p_147947_3_ - l);
            int i1 = p_147947_2_ * j;
            TextureUtil.copyToBufferPos(p_147947_1_, k, i1);
            GL11.glTexSubImage2D(3553, p_147947_0_, p_147947_4_, p_147947_5_ + l, p_147947_2_, j, 32993, 33639, dataBuffer);
        }
    }

    public static int uploadTextureImageAllocate(int p_110989_0_, BufferedImage p_110989_1_, boolean p_110989_2_, boolean p_110989_3_) {
        TextureUtil.allocateTexture(p_110989_0_, p_110989_1_.getWidth(), p_110989_1_.getHeight());
        return TextureUtil.uploadTextureImageSub(p_110989_0_, p_110989_1_, 0, 0, p_110989_2_, p_110989_3_);
    }

    public static void allocateTexture(int p_110991_0_, int p_110991_1_, int p_110991_2_) {
        TextureUtil.allocateTextureImpl(p_110991_0_, 0, p_110991_1_, p_110991_2_);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void allocateTextureImpl(int p_180600_0_, int p_180600_1_, int p_180600_2_, int p_180600_3_) {
        Class object = TextureUtil.class;
        if (Reflector.SplashScreen.exists()) {
            object = Reflector.SplashScreen.getTargetClass();
        }
        Class clazz = object;
        synchronized (clazz) {
            TextureUtil.deleteTexture(p_180600_0_);
            TextureUtil.bindTexture(p_180600_0_);
        }
        if (p_180600_1_ >= 0) {
            GL11.glTexParameteri(3553, 33085, p_180600_1_);
            GL11.glTexParameterf(3553, 33082, 0.0f);
            GL11.glTexParameterf(3553, 33083, p_180600_1_);
            GL11.glTexParameterf(3553, 34049, 0.0f);
        }
        for (int i = 0; i <= p_180600_1_; ++i) {
            GL11.glTexImage2D(3553, i, 6408, p_180600_2_ >> i, p_180600_3_ >> i, 0, 32993, 33639, (IntBuffer)null);
        }
    }

    public static int uploadTextureImageSub(int textureId, BufferedImage p_110995_1_, int p_110995_2_, int p_110995_3_, boolean p_110995_4_, boolean p_110995_5_) {
        TextureUtil.bindTexture(textureId);
        TextureUtil.uploadTextureImageSubImpl(p_110995_1_, p_110995_2_, p_110995_3_, p_110995_4_, p_110995_5_);
        return textureId;
    }

    private static void uploadTextureImageSubImpl(BufferedImage p_110993_0_, int p_110993_1_, int p_110993_2_, boolean p_110993_3_, boolean p_110993_4_) {
        int i = p_110993_0_.getWidth();
        int j = p_110993_0_.getHeight();
        int k = 0x400000 / i;
        int[] aint = dataArray;
        TextureUtil.setTextureBlurred(p_110993_3_);
        TextureUtil.setTextureClamped(p_110993_4_);
        for (int l = 0; l < i * j; l += i * k) {
            int i1 = l / i;
            int j1 = Math.min(k, j - i1);
            int k1 = i * j1;
            p_110993_0_.getRGB(0, i1, i, j1, aint, 0, i);
            TextureUtil.copyToBuffer(aint, k1);
            GL11.glTexSubImage2D(3553, 0, p_110993_1_, p_110993_2_ + i1, i, j1, 32993, 33639, dataBuffer);
        }
    }

    public static void setTextureClamped(boolean p_110997_0_) {
        if (p_110997_0_) {
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
        } else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }
    }

    private static void setTextureBlurred(boolean p_147951_0_) {
        TextureUtil.setTextureBlurMipmap(p_147951_0_, false);
    }

    public static void setTextureBlurMipmap(boolean p_147954_0_, boolean p_147954_1_) {
        if (p_147954_0_) {
            GL11.glTexParameteri(3553, 10241, p_147954_1_ ? 9987 : 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
        } else {
            int i = Config.getMipmapType();
            GL11.glTexParameteri(3553, 10241, p_147954_1_ ? i : 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
        }
    }

    private static void copyToBuffer(int[] p_110990_0_, int p_110990_1_) {
        TextureUtil.copyToBufferPos(p_110990_0_, 0, p_110990_1_);
    }

    private static void copyToBufferPos(int[] p_110994_0_, int p_110994_1_, int p_110994_2_) {
        int[] aint = p_110994_0_;
        if (Minecraft.getMinecraft().gameSettings.anaglyph) {
            aint = TextureUtil.updateAnaglyph(p_110994_0_);
        }
        dataBuffer.clear();
        dataBuffer.put(aint, p_110994_1_, p_110994_2_);
        dataBuffer.position(0).limit(p_110994_2_);
    }

    static void bindTexture(int p_94277_0_) {
        GlStateManager.bindTexture(p_94277_0_);
    }

    public static int[] readImageData(IResourceManager resourceManager, ResourceLocation imageLocation) throws IOException {
        BufferedImage bufferedimage = TextureUtil.readBufferedImage(resourceManager.getResource(imageLocation).getInputStream());
        if (bufferedimage == null) {
            return null;
        }
        int i = bufferedimage.getWidth();
        int j = bufferedimage.getHeight();
        int[] aint = new int[i * j];
        bufferedimage.getRGB(0, 0, i, j, aint, 0, i);
        return aint;
    }

    public static BufferedImage readBufferedImage(InputStream imageStream) throws IOException {
        BufferedImage bufferedimage;
        if (imageStream == null) {
            return null;
        }
        try {
            bufferedimage = ImageIO.read(imageStream);
        }
        finally {
            IOUtils.closeQuietly(imageStream);
        }
        return bufferedimage;
    }

    public static int[] updateAnaglyph(int[] p_110985_0_) {
        int[] aint = new int[p_110985_0_.length];
        for (int i = 0; i < p_110985_0_.length; ++i) {
            aint[i] = TextureUtil.anaglyphColor(p_110985_0_[i]);
        }
        return aint;
    }

    public static int anaglyphColor(int p_177054_0_) {
        int i = p_177054_0_ >> 24 & 0xFF;
        int j = p_177054_0_ >> 16 & 0xFF;
        int k = p_177054_0_ >> 8 & 0xFF;
        int l = p_177054_0_ & 0xFF;
        int i1 = (j * 30 + k * 59 + l * 11) / 100;
        int j1 = (j * 30 + k * 70) / 100;
        int k1 = (j * 30 + l * 70) / 100;
        return i << 24 | i1 << 16 | j1 << 8 | k1;
    }

    public static void processPixelValues(int[] p_147953_0_, int p_147953_1_, int p_147953_2_) {
        int[] aint = new int[p_147953_1_];
        int i = p_147953_2_ / 2;
        for (int j = 0; j < i; ++j) {
            System.arraycopy(p_147953_0_, j * p_147953_1_, aint, 0, p_147953_1_);
            System.arraycopy(p_147953_0_, (p_147953_2_ - 1 - j) * p_147953_1_, p_147953_0_, j * p_147953_1_, p_147953_1_);
            System.arraycopy(aint, 0, p_147953_0_, (p_147953_2_ - 1 - j) * p_147953_1_, p_147953_1_);
        }
    }

    static {
        dataArray = new int[0x400000];
        int i = -16777216;
        int j = -524040;
        int[] aint = new int[]{-524040, -524040, -524040, -524040, -524040, -524040, -524040, -524040};
        int[] aint1 = new int[]{-16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
        int k = aint.length;
        for (int l = 0; l < 16; ++l) {
            System.arraycopy(l < k ? aint : aint1, 0, missingTextureData, 16 * l, k);
            System.arraycopy(l < k ? aint1 : aint, 0, missingTextureData, 16 * l + k, k);
        }
        missingTexture.updateDynamicTexture();
        mipmapBuffer = new int[4];
    }
}

