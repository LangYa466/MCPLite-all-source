/*
 * Decompiled with CFR 0.151.
 */
package client.ui.fastuni;

import client.ui.fastuni.IBFFontRenderer;
import client.ui.fastuni.StringCache;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

public class FontRendererCallback {
    public static boolean betterFontsEnabled = true;

    public static void constructor(IBFFontRenderer font, ResourceLocation location) {
        if (((FontRenderer)((Object)font)).getClass() != FontRenderer.class) {
            return;
        }
        if (location.getResourcePath().equalsIgnoreCase("textures/font/ascii.png") && font.getStringCache() == null) {
            font.setDropShadowEnabled(true);
            int[] colorCode = ((FontRenderer)font).colorCode;
            font.setStringCache(new StringCache(colorCode));
            font.getStringCache().setDefaultFont("Lucida Sans Regular", 18, true);
        }
    }

    public static String bidiReorder(IBFFontRenderer font, String text) {
        if (betterFontsEnabled && font.getStringCache() != null) {
            return text;
        }
        try {
            Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        }
        catch (ArabicShapingException var3) {
            return text;
        }
    }
}

