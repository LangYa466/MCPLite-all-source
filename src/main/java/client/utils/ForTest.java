/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ForTest {
    public static List<Object> list = new ArrayList<Object>();
    public static int anInt1 = 0;
    public static int anInt = 0;
    public static boolean bool3 = false;
    public static boolean bool2 = false;
    public static boolean bool1 = false;

    public static void test(Object ... object) {
        try {
            String methodName = (String)object[0];
            if (methodName.equalsIgnoreCase("drawrect")) {
                float x = ((Float)object[1]).floatValue();
                float y = ((Float)object[2]).floatValue();
                float x2 = ((Float)object[3]).floatValue();
                float y2 = ((Float)object[4]).floatValue();
                Color color = (Color)object[5];
                GL11.glEnable(3042);
                GL11.glDisable(3553);
                GL11.glBlendFunc(770, 771);
                GL11.glEnable(2848);
                int hex = color.getRGB();
                float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
                float red = (float)(hex >> 16 & 0xFF) / 255.0f;
                float green = (float)(hex >> 8 & 0xFF) / 255.0f;
                float blue = (float)(hex & 0xFF) / 255.0f;
                GlStateManager.color(red, green, blue, alpha);
                GL11.glBegin(7);
                GL11.glVertex2d(x2, y);
                GL11.glVertex2d(x, y);
                GL11.glVertex2d(x, y2);
                GL11.glVertex2d(x2, y2);
                GL11.glEnd();
                GL11.glEnable(3553);
                GL11.glDisable(3042);
                GL11.glDisable(2848);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

