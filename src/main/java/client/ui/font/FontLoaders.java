/*
 * Decompiled with CFR 0.151.
 */
package client.ui.font;

import client.ui.font.CFontRenderer;
import java.awt.Font;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public abstract class FontLoaders {
    public static CFontRenderer ICON10 = new CFontRenderer(FontLoaders.getfonts(10, "ICONF", true), true, true);
    public static CFontRenderer ICON20 = new CFontRenderer(FontLoaders.getfonts(20, "ICONF", true), true, true);
    public static CFontRenderer GoogleSans10 = new CFontRenderer(FontLoaders.getGoogleSans(10), true, true);
    public static CFontRenderer GoogleSans12 = new CFontRenderer(FontLoaders.getGoogleSans(12), true, true);
    public static CFontRenderer GoogleSans14 = new CFontRenderer(FontLoaders.getGoogleSans(14), true, true);
    public static CFontRenderer GoogleSans15 = new CFontRenderer(FontLoaders.getGoogleSans(15), true, true);
    public static CFontRenderer GoogleSans16 = new CFontRenderer(FontLoaders.getGoogleSans(16), true, true);
    public static CFontRenderer GoogleSans18 = new CFontRenderer(FontLoaders.getGoogleSans(18), true, true);
    public static CFontRenderer GoogleSans20 = new CFontRenderer(FontLoaders.getGoogleSans(20), true, true);
    public static CFontRenderer GoogleSans22 = new CFontRenderer(FontLoaders.getGoogleSans(22), true, true);
    public static CFontRenderer GoogleSans24 = new CFontRenderer(FontLoaders.getGoogleSans(24), true, true);
    public static CFontRenderer GoogleSans28 = new CFontRenderer(FontLoaders.getGoogleSans(28), true, true);
    public static CFontRenderer GoogleSans35 = new CFontRenderer(FontLoaders.getGoogleSans(35), true, true);
    public static CFontRenderer GoogleSans40 = new CFontRenderer(FontLoaders.getGoogleSans(40), true, true);
    public static CFontRenderer SimpleFont40 = new CFontRenderer(FontLoaders.getSimpleFont(40), true, true);
    public static CFontRenderer SimpleFont20 = new CFontRenderer(FontLoaders.getSimpleFont(20), true, true);
    public static CFontRenderer SimpleFont22 = new CFontRenderer(FontLoaders.getSimpleFont(22), true, true);
    public static CFontRenderer SimpleFont24 = new CFontRenderer(FontLoaders.getSimpleFont(24), true, true);
    public static CFontRenderer SimpleFont26 = new CFontRenderer(FontLoaders.getSimpleFont(26), true, true);
    public static CFontRenderer SimpleFont28 = new CFontRenderer(FontLoaders.getSimpleFont(28), true, true);
    public static CFontRenderer SF18 = new CFontRenderer(FontLoaders.getSF(18), true, true);
    public static CFontRenderer SF20 = new CFontRenderer(FontLoaders.getSF(20), true, true);
    public static CFontRenderer SF24 = new CFontRenderer(FontLoaders.getSF(24), true, true);
    public static CFontRenderer SF26 = new CFontRenderer(FontLoaders.getSF(24), true, true);
    public static CFontRenderer Product_regular12 = new CFontRenderer(FontLoaders.getProductRegular(12), true, true);
    public static CFontRenderer Product_regular14 = new CFontRenderer(FontLoaders.getProductRegular(14), true, true);
    public static CFontRenderer Product_regular16 = new CFontRenderer(FontLoaders.getProductRegular(16), true, true);
    public static CFontRenderer Product_regular18 = new CFontRenderer(FontLoaders.getProductRegular(18), true, true);
    public static CFontRenderer Product_regular20 = new CFontRenderer(FontLoaders.getProductRegular(20), true, true);
    public static CFontRenderer Product_regular22 = new CFontRenderer(FontLoaders.getProductRegular(22), true, true);
    public static CFontRenderer Product_regular24 = new CFontRenderer(FontLoaders.getProductRegular(24), true, true);
    public static CFontRenderer Product_regular26 = new CFontRenderer(FontLoaders.getProductRegular(26), true, true);
    public static CFontRenderer Product_regular28 = new CFontRenderer(FontLoaders.getProductRegular(28), true, true);
    public static CFontRenderer Avergent18 = new CFontRenderer(FontLoaders.getAvergent(18), true, true);
    public static CFontRenderer Avergent20 = new CFontRenderer(FontLoaders.getAvergent(20), true, true);
    public static CFontRenderer Avergent22 = new CFontRenderer(FontLoaders.getAvergent(22), true, true);
    public static CFontRenderer Avergent24 = new CFontRenderer(FontLoaders.getAvergent(24), true, true);
    public static CFontRenderer Avergent26 = new CFontRenderer(FontLoaders.getAvergent(26), true, true);
    public static CFontRenderer QuicksandLight18 = new CFontRenderer(FontLoaders.getQuidsandLight(18), true, true);
    public static CFontRenderer QuicksandLight20 = new CFontRenderer(FontLoaders.getQuidsandLight(20), true, true);
    public static CFontRenderer QuicksandLight22 = new CFontRenderer(FontLoaders.getQuidsandLight(22), true, true);
    public static CFontRenderer QuicksandLight24 = new CFontRenderer(FontLoaders.getQuidsandLight(24), true, true);
    public static CFontRenderer QuicksandLight26 = new CFontRenderer(FontLoaders.getQuidsandLight(26), true, true);
    public static CFontRenderer NovICON18 = new CFontRenderer(FontLoaders.getNovICON(18), true, true);
    public static CFontRenderer NovICON20 = new CFontRenderer(FontLoaders.getNovICON(20), true, true);
    public static CFontRenderer NovICON24 = new CFontRenderer(FontLoaders.getNovICON(24), true, true);
    public static CFontRenderer NovICON28 = new CFontRenderer(FontLoaders.getNovICON(28), true, true);
    public static CFontRenderer NovICON64 = new CFontRenderer(FontLoaders.getNovICON(64), true, true);
    public static CFontRenderer XylitolICON = new CFontRenderer(FontLoaders.getXylitolICON(22), true, true);
    public static CFontRenderer Baloo18 = new CFontRenderer(FontLoaders.getfonts(18, "Baloo", true), true, true);
    public static CFontRenderer Baloo20 = new CFontRenderer(FontLoaders.getfonts(20, "Baloo", true), true, true);
    public static CFontRenderer Baloo24 = new CFontRenderer(FontLoaders.getfonts(22, "Baloo", true), true, true);
    public static CFontRenderer Baloo28 = new CFontRenderer(FontLoaders.getfonts(24, "Baloo", true), true, true);
    public static CFontRenderer Bold18 = new CFontRenderer(FontLoaders.getfonts(18, "Bold", true), true, true);
    public static CFontRenderer Tenacity12 = new CFontRenderer(FontLoaders.getfonts(12, "tenacity", true), true, true);
    public static CFontRenderer Tenacity14 = new CFontRenderer(FontLoaders.getfonts(14, "tenacity", true), true, true);
    public static CFontRenderer Tenacity16 = new CFontRenderer(FontLoaders.getfonts(16, "tenacity", true), true, true);
    public static CFontRenderer Tenacity18 = new CFontRenderer(FontLoaders.getfonts(18, "tenacity", true), true, true);
    public static CFontRenderer Tenacity20 = new CFontRenderer(FontLoaders.getfonts(20, "tenacity", true), true, true);
    public static CFontRenderer Tenacity22 = new CFontRenderer(FontLoaders.getfonts(22, "tenacity", true), true, true);
    public static CFontRenderer Tenacity24 = new CFontRenderer(FontLoaders.getfonts(24, "tenacity", true), true, true);
    public static CFontRenderer Tenacity26 = new CFontRenderer(FontLoaders.getfonts(26, "tenacity", true), true, true);
    public static CFontRenderer Tenacity28 = new CFontRenderer(FontLoaders.getfonts(28, "tenacity", true), true, true);
    public static CFontRenderer Tenacity30 = new CFontRenderer(FontLoaders.getfonts(30, "tenacity", true), true, true);
    public static CFontRenderer Tenacityb12 = new CFontRenderer(FontLoaders.getfonts(12, "tenacity-bold", true), true, true);
    public static CFontRenderer Tenacityb14 = new CFontRenderer(FontLoaders.getfonts(14, "tenacity-bold", true), true, true);
    public static CFontRenderer Tenacityb16 = new CFontRenderer(FontLoaders.getfonts(16, "tenacity-bold", true), true, true);
    public static CFontRenderer Tenacityb18 = new CFontRenderer(FontLoaders.getfonts(18, "tenacity-bold", true), true, true);
    public static CFontRenderer Tenacityb20 = new CFontRenderer(FontLoaders.getfonts(20, "tenacity-bold", true), true, true);
    public static CFontRenderer Tenacityb22 = new CFontRenderer(FontLoaders.getfonts(22, "tenacity-bold", true), true, true);
    public static CFontRenderer Tenacityb24 = new CFontRenderer(FontLoaders.getfonts(24, "tenacity-bold", true), true, true);
    public static CFontRenderer Tenacityb26 = new CFontRenderer(FontLoaders.getfonts(26, "tenacity-bold", true), true, true);
    public static CFontRenderer Tenacityb28 = new CFontRenderer(FontLoaders.getfonts(28, "tenacity-bold", true), true, true);
    public static CFontRenderer Tenacityb30 = new CFontRenderer(FontLoaders.getfonts(30, "tenacity-bold", true), true, true);

    private static Font getComfortaa(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("Client/fonts/Comfortaa.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getQuidsandLight(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/Quicksand-Light.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getAvergent(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/Avergent-Regular.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getProductRegular(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/product_sans_regular.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getNovICON(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/NovICON.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getXylitolICON(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/iconfont.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getSF(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/SF.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getGoogleSans(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/GoogleSans.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getSimpleFont(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/font.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static Font getfonts(int size, String fontname, boolean ttf) {
        Font font;
        try {
            InputStream is = ttf ? Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/" + fontname + ".ttf")).getInputStream() : Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/" + fontname + ".otf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
}

