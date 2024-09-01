/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import com.viaversion.viaversion.util.MathUtil;
import java.awt.Color;
import java.awt.image.BufferedImage;
import net.minecraft.util.MathHelper;

public class ColorUtil {
    public static Color tripleColor(int rgbValue) {
        return ColorUtil.tripleColor(rgbValue, 1.0f);
    }

    public static Color tripleColor(int rgbValue, float alpha) {
        alpha = Math.min(1.0f, Math.max(0.0f, alpha));
        return new Color(rgbValue, rgbValue, rgbValue, (int)(255.0f * alpha));
    }

    public static Color[] getAnalogousColor(Color color) {
        Color[] colors = new Color[2];
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float degree = 0.083333336f;
        float newHueAdded = hsb[0] + degree;
        colors[0] = new Color(Color.HSBtoRGB(newHueAdded, hsb[1], hsb[2]));
        float newHueSubtracted = hsb[0] - degree;
        colors[1] = new Color(Color.HSBtoRGB(newHueSubtracted, hsb[1], hsb[2]));
        return colors;
    }

    static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathUtil.clamp(0, 255, alpha));
    }

    public static Color getRandomColor() {
        return new Color(Color.HSBtoRGB((float)Math.random(), (float)(0.5 + Math.random() / 2.0), (float)(0.5 + Math.random() / 2.0)));
    }

    public static Color hslToRGB(float[] hsl) {
        float red;
        float green;
        float blue;
        if (hsl[1] == 0.0f) {
            blue = 1.0f;
            green = 1.0f;
            red = 1.0f;
        } else {
            float q = (double)hsl[2] < 0.5 ? hsl[2] * (1.0f + hsl[1]) : hsl[2] + hsl[1] - hsl[2] * hsl[1];
            float p = 2.0f * hsl[2] - q;
            red = ColorUtil.hueToRGB(p, q, hsl[0] + 0.33333334f);
            green = ColorUtil.hueToRGB(p, q, hsl[0]);
            blue = ColorUtil.hueToRGB(p, q, hsl[0] - 0.33333334f);
        }
        return new Color((int)(red *= 255.0f), (int)(green *= 255.0f), (int)(blue *= 255.0f));
    }

    public static float hueToRGB(float p, float q, float t) {
        float newT = t;
        if (newT < 0.0f) {
            newT += 1.0f;
        }
        if (newT > 1.0f) {
            newT -= 1.0f;
        }
        if (newT < 0.16666667f) {
            return p + (q - p) * 6.0f * newT;
        }
        if (newT < 0.5f) {
            return q;
        }
        if (newT < 0.6666667f) {
            return p + (q - p) * (0.6666667f - newT) * 6.0f;
        }
        return p;
    }

    public static float[] rgbToHSL(Color rgb) {
        float red = (float)rgb.getRed() / 255.0f;
        float green = (float)rgb.getGreen() / 255.0f;
        float blue = (float)rgb.getBlue() / 255.0f;
        float max = Math.max(Math.max(red, green), blue);
        float min = Math.min(Math.min(red, green), blue);
        float c = (max + min) / 2.0f;
        float[] hsl = new float[]{c, c, c};
        if (max == min) {
            hsl[1] = 0.0f;
            hsl[0] = 0.0f;
        } else {
            float d = max - min;
            float f = hsl[1] = (double)hsl[2] > 0.5 ? d / (2.0f - max - min) : d / (max + min);
            if (max == red) {
                hsl[0] = (green - blue) / d + (float)(green < blue ? 6 : 0);
            } else if (max == blue) {
                hsl[0] = (blue - red) / d + 2.0f;
            } else if (max == green) {
                hsl[0] = (red - green) / d + 4.0f;
            }
            hsl[0] = hsl[0] / 6.0f;
        }
        return hsl;
    }

    public static Color imitateTransparency(Color backgroundColor, Color accentColor, float percentage) {
        return new Color(ColorUtil.interpolateColor(backgroundColor, accentColor, 255.0f * percentage / 255.0f));
    }

    public static int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return ColorUtil.applyOpacity(old, opacity).getRGB();
    }

    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1.0f, Math.max(0.0f, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)((float)color.getAlpha() * opacity));
    }

    public static Color darker(Color color, float FACTOR) {
        return new Color(Math.max((int)((float)color.getRed() * FACTOR), 0), Math.max((int)((float)color.getGreen() * FACTOR), 0), Math.max((int)((float)color.getBlue() * FACTOR), 0), color.getAlpha());
    }

    public static Color brighter(Color color, float FACTOR) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();
        int i = (int)(1.0 / (1.0 - (double)FACTOR));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if (r > 0 && r < i) {
            r = i;
        }
        if (g > 0 && g < i) {
            g = i;
        }
        if (b > 0 && b < i) {
            b = i;
        }
        return new Color(Math.min((int)((float)r / FACTOR), 255), Math.min((int)((float)g / FACTOR), 255), Math.min((int)((float)b / FACTOR), 255), alpha);
    }

    public static Color averageColor(BufferedImage bi, int width, int height, int pixelStep) {
        int[] color = new int[3];
        for (int x = 0; x < width; x += pixelStep) {
            for (int y = 0; y < height; y += pixelStep) {
                Color pixel = new Color(bi.getRGB(x, y));
                color[0] = color[0] + pixel.getRed();
                color[1] = color[1] + pixel.getGreen();
                color[2] = color[2] + pixel.getBlue();
            }
        }
        int num = width * height / (pixelStep * pixelStep);
        return new Color(color[0] / num, color[1] / num, color[2] / num);
    }

    public static Color rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        float hue = (float)angle / 360.0f;
        Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int)(opacity * 255.0f))));
    }

    public static Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? ColorUtil.interpolateColorHue(start, end, (float)angle / 360.0f) : ColorUtil.interpolateColorC(start, end, (float)angle / 360.0f);
    }

    public static int interpolateColor(Color color1, Color color2, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        return ColorUtil.interpolateColorC(color1, color2, amount).getRGB();
    }

    public static int interpolateColor(int color1, int color2, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        Color cColor1 = new Color(color1);
        Color cColor2 = new Color(color2);
        return ColorUtil.interpolateColorC(cColor1, cColor2, amount).getRGB();
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        return new Color(MathHelper.interpolateInt(color1.getRed(), color2.getRed(), amount), MathHelper.interpolateInt(color1.getGreen(), color2.getGreen(), amount), MathHelper.interpolateInt(color1.getBlue(), color2.getBlue(), amount), MathHelper.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);
        Color resultColor = Color.getHSBColor(MathHelper.interpolateFloat(color1HSB[0], color2HSB[0], amount), MathHelper.interpolateFloat(color1HSB[1], color2HSB[1], amount), MathHelper.interpolateFloat(color1HSB[2], color2HSB[2], amount));
        return ColorUtil.applyOpacity(resultColor, (float)MathHelper.interpolateInt(color1.getAlpha(), color2.getAlpha(), amount) / 255.0f);
    }

    public static Color fade(int speed, int index, Color color, float alpha) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        Color colorHSB = new Color(Color.HSBtoRGB(hsb[0], hsb[1], (float)angle / 360.0f));
        return new Color(colorHSB.getRed(), colorHSB.getGreen(), colorHSB.getBlue(), Math.max(0, Math.min(255, (int)(alpha * 255.0f))));
    }

    private static float getAnimationEquation(int index, int speed) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        return (float)((angle > 180 ? 360 - angle : angle) + 180) / 360.0f;
    }

    public static int[] createColorArray(int color) {
        return new int[]{ColorUtil.bitChangeColor(color, 16), ColorUtil.bitChangeColor(color, 8), ColorUtil.bitChangeColor(color, 0), ColorUtil.bitChangeColor(color, 24)};
    }

    public static int getOppositeColor(int color) {
        int R = ColorUtil.bitChangeColor(color, 0);
        int G = ColorUtil.bitChangeColor(color, 8);
        int B = ColorUtil.bitChangeColor(color, 16);
        int A = ColorUtil.bitChangeColor(color, 24);
        R = 255 - R;
        G = 255 - G;
        B = 255 - B;
        return R + (G << 8) + (B << 16) + (A << 24);
    }

    public static Color getOppositeColor(Color color) {
        return new Color(ColorUtil.getOppositeColor(color.getRGB()));
    }

    private static int bitChangeColor(int color, int bitChange) {
        return color >> bitChange & 0xFF;
    }
}

