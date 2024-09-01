/*
 * Decompiled with CFR 0.151.
 */
package client.ui.fastuni;

import client.ui.fastuni.FastUniFontRenderer;
import java.awt.Font;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class FontLoader {
    public static FastUniFontRenderer simpleFont12 = FontLoader.getSimpleFontUni(12);
    public static FastUniFontRenderer simpleFont14 = FontLoader.getSimpleFontUni(14);
    public static FastUniFontRenderer simpleFont16 = FontLoader.getSimpleFontUni(16);
    public static FastUniFontRenderer simpleFont18 = FontLoader.getSimpleFontUni(18);
    public static FastUniFontRenderer simpleFont20 = FontLoader.getSimpleFontUni(20);
    public static FastUniFontRenderer simpleFont22 = FontLoader.getSimpleFontUni(22);
    public static FastUniFontRenderer simpleFont24 = FontLoader.getSimpleFontUni(24);
    public static FastUniFontRenderer simpleFont26 = FontLoader.getSimpleFontUni(26);
    public static FastUniFontRenderer simpleFont28 = FontLoader.getSimpleFontUni(28);
    public static FastUniFontRenderer QuickSandMedium18 = FontLoader.getFont(18, "Quicksand-Medium.ttf");
    public static FastUniFontRenderer QuickSandMedium20 = FontLoader.getFont(20, "Quicksand-Medium.ttf");
    public static FastUniFontRenderer QuickSandMedium22 = FontLoader.getFont(22, "Quicksand-Medium.ttf");
    public static FastUniFontRenderer QuickSandMedium24 = FontLoader.getFont(24, "Quicksand-Medium.ttf");
    public static FastUniFontRenderer QuickSandMedium26 = FontLoader.getFont(26, "Quicksand-Medium.ttf");
    public static FastUniFontRenderer QuickSandMedium28 = FontLoader.getFont(28, "Quicksand-Medium.ttf");
    public static FastUniFontRenderer productM12 = FontLoader.getFont(12, "product_sans_medium.ttf");
    public static FastUniFontRenderer productM14 = FontLoader.getFont(14, "product_sans_medium.ttf");
    public static FastUniFontRenderer productM16 = FontLoader.getFont(16, "product_sans_medium.ttf");
    public static FastUniFontRenderer productM18 = FontLoader.getFont(18, "product_sans_medium.ttf");
    public static FastUniFontRenderer productM20 = FontLoader.getFont(20, "product_sans_medium.ttf");
    public static FastUniFontRenderer productM22 = FontLoader.getFont(22, "product_sans_medium.ttf");
    public static FastUniFontRenderer productM24 = FontLoader.getFont(24, "product_sans_medium.ttf");
    public static FastUniFontRenderer productM26 = FontLoader.getFont(26, "product_sans_medium.ttf");
    public static FastUniFontRenderer product28 = FontLoader.getFont(28, "product_sans_medium.ttf");
    public static FastUniFontRenderer miFont12 = FontLoader.getMiFontUni(12);
    public static FastUniFontRenderer miFont14 = FontLoader.getMiFontUni(16);
    public static FastUniFontRenderer miFont18 = FontLoader.getMiFontUni(18);
    public static FastUniFontRenderer miFont16 = FontLoader.getMiFontUni(14);
    public static FastUniFontRenderer miFont20 = FontLoader.getMiFontUni(20);
    public static FastUniFontRenderer miMiFont22 = FontLoader.getMiFontUni(22);
    public static FastUniFontRenderer miFont24 = FontLoader.getMiFontUni(24);
    public static FastUniFontRenderer miFont26 = FontLoader.getMiFontUni(26);
    public static FastUniFontRenderer miFont28 = FontLoader.getMiFontUni(28);
    public static FastUniFontRenderer icon20 = FontLoader.getIcon(20);
    public static FastUniFontRenderer icon18 = FontLoader.getIcon(18);
    public static FastUniFontRenderer icon16 = FontLoader.getIcon(16);
    public static FastUniFontRenderer icon14 = FontLoader.getIcon(14);
    public static FastUniFontRenderer icon12 = FontLoader.getIcon(12);
    public static FastUniFontRenderer icon22 = FontLoader.getIcon(22);
    public static FastUniFontRenderer icon24 = FontLoader.getIcon(24);
    public static FastUniFontRenderer icon36 = FontLoader.getIcon(36);
    public static FastUniFontRenderer icon46 = FontLoader.getIcon(46);
    public static FastUniFontRenderer icon42 = FontLoader.getIcon(42);
    public static FastUniFontRenderer icon48 = FontLoader.getIcon(48);
    public static FastUniFontRenderer mainMenu24 = FontLoader.getFont(24, "mainmenu.ttf");
    public static FastUniFontRenderer mainMenu32 = FontLoader.getFont(32, "mainmenu.ttf");
    public static FastUniFontRenderer mainMenu36 = FontLoader.getFont(36, "mainmenu.ttf");
    public static FastUniFontRenderer mainMenu28 = FontLoader.getFont(28, "mainmenu.ttf");
    public static FastUniFontRenderer mainMenu40 = FontLoader.getFont(40, "mainmenu.ttf");
    public static FastUniFontRenderer clientIcon32 = FontLoader.getFont(32, "mcplite_icon.ttf");
    public static FastUniFontRenderer clientIcon36 = FontLoader.getFont(36, "mcplite_icon.ttf");
    public static FastUniFontRenderer clientIcon40 = FontLoader.getFont(40, "mcplite_icon.ttf");
    public static FastUniFontRenderer clientIcon44 = FontLoader.getFont(44, "mcplite_icon.ttf");
    public static FastUniFontRenderer clientIcon48 = FontLoader.getFont(48, "mcplite_icon.ttf");

    public static void init() {
    }

    public static FastUniFontRenderer getIcon(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/icont.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return new FastUniFontRenderer(font, size, true);
    }

    public static FastUniFontRenderer getFont(int size, String fontname) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/" + fontname)).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return new FastUniFontRenderer(font, size, true);
    }

    public static FastUniFontRenderer getMiFontUni(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/MiSans-Bold.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return new FastUniFontRenderer(font, size, true);
    }

    public static FastUniFontRenderer getSimpleFontUni(int size) {
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
        return new FastUniFontRenderer(font, size, true);
    }

    public static Font getSimpleFont(int size) {
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
}

