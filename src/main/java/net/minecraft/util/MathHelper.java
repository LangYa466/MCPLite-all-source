/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import net.minecraft.util.Vec3i;
import net.optifine.util.MathUtils;

public class MathHelper {
    public static final float SQRT_2 = MathHelper.sqrt_float(2.0f);
    private static final int SIN_BITS = 12;
    private static final int SIN_MASK = 4095;
    private static final int SIN_COUNT = 4096;
    private static final int SIN_COUNT_D4 = 1024;
    public static final float PI = MathUtils.roundToFloat(Math.PI);
    public static final float PI2 = MathUtils.roundToFloat(Math.PI * 2);
    public static final float PId2 = MathUtils.roundToFloat(1.5707963267948966);
    private static final float radToIndex = MathUtils.roundToFloat(651.8986469044033);
    public static final float deg2Rad = MathUtils.roundToFloat(Math.PI / 180);
    private static final float[] SIN_TABLE_FAST = new float[4096];
    public static boolean fastMath = false;
    private static final float[] SIN_TABLE = new float[65536];
    private static final int[] multiplyDeBruijnBitPosition;
    private static final double field_181163_d;
    private static final double[] field_181164_e;
    private static final double[] field_181165_f;

    public static float sin(float p_76126_0_) {
        return fastMath ? SIN_TABLE_FAST[(int)(p_76126_0_ * radToIndex) & 0xFFF] : SIN_TABLE[(int)(p_76126_0_ * 10430.378f) & 0xFFFF];
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static float cos(float value) {
        return fastMath ? SIN_TABLE_FAST[(int)(value * radToIndex + 1024.0f) & 0xFFF] : SIN_TABLE[(int)(value * 10430.378f + 16384.0f) & 0xFFFF];
    }

    public static double roundToHalf(double d) {
        return (double)Math.round(d * 2.0) / 2.0;
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return oldValue + (newValue - oldValue) * interpolationValue;
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
        return MathHelper.interpolate(oldValue, newValue, (float)interpolationValue).floatValue();
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return MathHelper.interpolate(oldValue, newValue, (float)interpolationValue).intValue();
    }

    public static float sqrt_float(float value) {
        return (float)Math.sqrt(value);
    }

    public static float sqrt_double(double value) {
        return (float)Math.sqrt(value);
    }

    public static int floor_float(float value) {
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }

    public static int truncateDoubleToInt(double value) {
        return (int)(value + 1024.0) - 1024;
    }

    public static int floor_double(double value) {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }

    public static long floor_double_long(double value) {
        long i = (long)value;
        return value < (double)i ? i - 1L : i;
    }

    public static int func_154353_e(double value) {
        return (int)(value >= 0.0 ? value : -value + 1.0);
    }

    public static float abs(float value) {
        return value >= 0.0f ? value : -value;
    }

    public static int abs_int(int value) {
        return value >= 0 ? value : -value;
    }

    public static int ceiling_float_int(float value) {
        int i = (int)value;
        return value > (float)i ? i + 1 : i;
    }

    public static int ceiling_double_int(double value) {
        int i = (int)value;
        return value > (double)i ? i + 1 : i;
    }

    public static int clamp_int(int num, int min, int max) {
        return num < min ? min : (num > max ? max : num);
    }

    public static float clamp_float(float num, float min, float max) {
        return num < min ? min : (num > max ? max : num);
    }

    public static double clamp_double(double num, double min, double max) {
        return num < min ? min : (num > max ? max : num);
    }

    public static double denormalizeClamp(double lowerBnd, double upperBnd, double slide) {
        return slide < 0.0 ? lowerBnd : (slide > 1.0 ? upperBnd : lowerBnd + (upperBnd - lowerBnd) * slide);
    }

    public static double abs_max(double p_76132_0_, double p_76132_2_) {
        if (p_76132_0_ < 0.0) {
            p_76132_0_ = -p_76132_0_;
        }
        if (p_76132_2_ < 0.0) {
            p_76132_2_ = -p_76132_2_;
        }
        return p_76132_0_ > p_76132_2_ ? p_76132_0_ : p_76132_2_;
    }

    public static int bucketInt(int p_76137_0_, int p_76137_1_) {
        return p_76137_0_ < 0 ? -((-p_76137_0_ - 1) / p_76137_1_) - 1 : p_76137_0_ / p_76137_1_;
    }

    public static int getRandomInRange(int min, int max) {
        return (int)(Math.random() * (double)(max - min) + (double)min);
    }

    public static double getRandomInRange(double min, double max) {
        SecureRandom random = new SecureRandom();
        return min == max ? min : random.nextDouble() * (max - min) + min;
    }

    public static int getRandomIntegerInRange(Random p_76136_0_, int p_76136_1_, int p_76136_2_) {
        return p_76136_1_ >= p_76136_2_ ? p_76136_1_ : p_76136_0_.nextInt(p_76136_2_ - p_76136_1_ + 1) + p_76136_1_;
    }

    public static float randomFloatClamp(Random p_151240_0_, float p_151240_1_, float p_151240_2_) {
        return p_151240_1_ >= p_151240_2_ ? p_151240_1_ : p_151240_0_.nextFloat() * (p_151240_2_ - p_151240_1_) + p_151240_1_;
    }

    public static double getRandomDoubleInRange(Random p_82716_0_, double p_82716_1_, double p_82716_3_) {
        return p_82716_1_ >= p_82716_3_ ? p_82716_1_ : p_82716_0_.nextDouble() * (p_82716_3_ - p_82716_1_) + p_82716_1_;
    }

    public static double average(long[] values) {
        long i = 0L;
        for (long j : values) {
            i += j;
        }
        return (double)i / (double)values.length;
    }

    public static boolean epsilonEquals(float p_180185_0_, float p_180185_1_) {
        return MathHelper.abs(p_180185_1_ - p_180185_0_) < 1.0E-5f;
    }

    public static int normalizeAngle(int p_180184_0_, int p_180184_1_) {
        return (p_180184_0_ % p_180184_1_ + p_180184_1_) % p_180184_1_;
    }

    public static float wrapAngleTo180_float(float value) {
        if ((value %= 360.0f) >= 180.0f) {
            value -= 360.0f;
        }
        if (value < -180.0f) {
            value += 360.0f;
        }
        return value;
    }

    public static double wrapAngleTo180_double(double value) {
        if ((value %= 360.0) >= 180.0) {
            value -= 360.0;
        }
        if (value < -180.0) {
            value += 360.0;
        }
        return value;
    }

    public static int parseIntWithDefault(String p_82715_0_, int p_82715_1_) {
        try {
            return Integer.parseInt(p_82715_0_);
        }
        catch (Throwable var3) {
            return p_82715_1_;
        }
    }

    public static int parseIntWithDefaultAndMax(String p_82714_0_, int p_82714_1_, int p_82714_2_) {
        return Math.max(p_82714_2_, MathHelper.parseIntWithDefault(p_82714_0_, p_82714_1_));
    }

    public static double parseDoubleWithDefault(String p_82712_0_, double p_82712_1_) {
        try {
            return Double.parseDouble(p_82712_0_);
        }
        catch (Throwable var4) {
            return p_82712_1_;
        }
    }

    public static double parseDoubleWithDefaultAndMax(String p_82713_0_, double p_82713_1_, double p_82713_3_) {
        return Math.max(p_82713_3_, MathHelper.parseDoubleWithDefault(p_82713_0_, p_82713_1_));
    }

    public static int roundUpToPowerOfTwo(int value) {
        int i = value - 1;
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        return i + 1;
    }

    private static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    private static int calculateLogBaseTwoDeBruijn(int value) {
        value = MathHelper.isPowerOfTwo(value) ? value : MathHelper.roundUpToPowerOfTwo(value);
        return multiplyDeBruijnBitPosition[(int)((long)value * 125613361L >> 27) & 0x1F];
    }

    public static int calculateLogBaseTwo(int value) {
        return MathHelper.calculateLogBaseTwoDeBruijn(value) - (MathHelper.isPowerOfTwo(value) ? 0 : 1);
    }

    public static int roundUp(int p_154354_0_, int p_154354_1_) {
        int i;
        if (p_154354_1_ == 0) {
            return 0;
        }
        if (p_154354_0_ == 0) {
            return p_154354_1_;
        }
        if (p_154354_0_ < 0) {
            p_154354_1_ *= -1;
        }
        return (i = p_154354_0_ % p_154354_1_) == 0 ? p_154354_0_ : p_154354_0_ + p_154354_1_ - i;
    }

    public static int func_180183_b(float p_180183_0_, float p_180183_1_, float p_180183_2_) {
        return MathHelper.func_180181_b(MathHelper.floor_float(p_180183_0_ * 255.0f), MathHelper.floor_float(p_180183_1_ * 255.0f), MathHelper.floor_float(p_180183_2_ * 255.0f));
    }

    public static int func_180181_b(int p_180181_0_, int p_180181_1_, int p_180181_2_) {
        int i = (p_180181_0_ << 8) + p_180181_1_;
        i = (i << 8) + p_180181_2_;
        return i;
    }

    public static int func_180188_d(int p_180188_0_, int p_180188_1_) {
        int i = (p_180188_0_ & 0xFF0000) >> 16;
        int j = (p_180188_1_ & 0xFF0000) >> 16;
        int k = (p_180188_0_ & 0xFF00) >> 8;
        int l = (p_180188_1_ & 0xFF00) >> 8;
        int i1 = (p_180188_0_ & 0xFF) >> 0;
        int j1 = (p_180188_1_ & 0xFF) >> 0;
        int k1 = (int)((float)i * (float)j / 255.0f);
        int l1 = (int)((float)k * (float)l / 255.0f);
        int i2 = (int)((float)i1 * (float)j1 / 255.0f);
        return p_180188_0_ & 0xFF000000 | k1 << 16 | l1 << 8 | i2;
    }

    public static double func_181162_h(double p_181162_0_) {
        return p_181162_0_ - Math.floor(p_181162_0_);
    }

    public static long getPositionRandom(Vec3i pos) {
        return MathHelper.getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ());
    }

    public static long getCoordinateRandom(int x, int y, int z) {
        long i = (long)(x * 3129871) ^ (long)z * 116129781L ^ (long)y;
        i = i * i * 42317861L + i * 11L;
        return i;
    }

    public static UUID getRandomUuid(Random rand) {
        long i = rand.nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x4000L;
        long j = rand.nextLong() & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE;
        return new UUID(i, j);
    }

    public static double func_181160_c(double p_181160_0_, double p_181160_2_, double p_181160_4_) {
        return (p_181160_0_ - p_181160_2_) / (p_181160_4_ - p_181160_2_);
    }

    public static double atan2(double p_181159_0_, double p_181159_2_) {
        boolean flag2;
        boolean flag1;
        boolean flag;
        double d0 = p_181159_2_ * p_181159_2_ + p_181159_0_ * p_181159_0_;
        if (Double.isNaN(d0)) {
            return Double.NaN;
        }
        boolean bl = flag = p_181159_0_ < 0.0;
        if (flag) {
            p_181159_0_ = -p_181159_0_;
        }
        boolean bl2 = flag1 = p_181159_2_ < 0.0;
        if (flag1) {
            p_181159_2_ = -p_181159_2_;
        }
        boolean bl3 = flag2 = p_181159_0_ > p_181159_2_;
        if (flag2) {
            double d1 = p_181159_2_;
            p_181159_2_ = p_181159_0_;
            p_181159_0_ = d1;
        }
        double d9 = MathHelper.func_181161_i(d0);
        double d2 = field_181163_d + (p_181159_0_ *= d9);
        int i = (int)Double.doubleToRawLongBits(d2);
        double d3 = field_181164_e[i];
        double d4 = field_181165_f[i];
        double d5 = d2 - field_181163_d;
        double d6 = p_181159_0_ * d4 - (p_181159_2_ *= d9) * d5;
        double d7 = (6.0 + d6 * d6) * d6 * 0.16666666666666666;
        double d8 = d3 + d7;
        if (flag2) {
            d8 = 1.5707963267948966 - d8;
        }
        if (flag1) {
            d8 = Math.PI - d8;
        }
        if (flag) {
            d8 = -d8;
        }
        return d8;
    }

    public static double func_181161_i(double p_181161_0_) {
        double d0 = 0.5 * p_181161_0_;
        long i = Double.doubleToRawLongBits(p_181161_0_);
        i = 6910469410427058090L - (i >> 1);
        p_181161_0_ = Double.longBitsToDouble(i);
        p_181161_0_ *= 1.5 - d0 * p_181161_0_ * p_181161_0_;
        return p_181161_0_;
    }

    public static int hsvToRGB(float p_181758_0_, float p_181758_1_, float p_181758_2_) {
        float f6;
        float f5;
        float f4;
        int i = (int)(p_181758_0_ * 6.0f) % 6;
        float f = p_181758_0_ * 6.0f - (float)i;
        float f1 = p_181758_2_ * (1.0f - p_181758_1_);
        float f2 = p_181758_2_ * (1.0f - f * p_181758_1_);
        float f3 = p_181758_2_ * (1.0f - (1.0f - f) * p_181758_1_);
        switch (i) {
            case 0: {
                f4 = p_181758_2_;
                f5 = f3;
                f6 = f1;
                break;
            }
            case 1: {
                f4 = f2;
                f5 = p_181758_2_;
                f6 = f1;
                break;
            }
            case 2: {
                f4 = f1;
                f5 = p_181758_2_;
                f6 = f3;
                break;
            }
            case 3: {
                f4 = f1;
                f5 = f2;
                f6 = p_181758_2_;
                break;
            }
            case 4: {
                f4 = f3;
                f5 = f1;
                f6 = p_181758_2_;
                break;
            }
            case 5: {
                f4 = p_181758_2_;
                f5 = f1;
                f6 = f2;
                break;
            }
            default: {
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + p_181758_0_ + ", " + p_181758_1_ + ", " + p_181758_2_);
            }
        }
        int j = MathHelper.clamp_int((int)(f4 * 255.0f), 0, 255);
        int k = MathHelper.clamp_int((int)(f5 * 255.0f), 0, 255);
        int l = MathHelper.clamp_int((int)(f6 * 255.0f), 0, 255);
        return j << 16 | k << 8 | l;
    }

    static {
        for (int i = 0; i < 65536; ++i) {
            MathHelper.SIN_TABLE[i] = (float)Math.sin((double)i * Math.PI * 2.0 / 65536.0);
        }
        for (int j = 0; j < SIN_TABLE_FAST.length; ++j) {
            MathHelper.SIN_TABLE_FAST[j] = MathUtils.roundToFloat(Math.sin((double)j * Math.PI * 2.0 / 4096.0));
        }
        multiplyDeBruijnBitPosition = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
        field_181163_d = Double.longBitsToDouble(4805340802404319232L);
        field_181164_e = new double[257];
        field_181165_f = new double[257];
        for (int k = 0; k < 257; ++k) {
            double d0 = (double)k / 256.0;
            double d1 = Math.asin(d0);
            MathHelper.field_181165_f[k] = Math.cos(d1);
            MathHelper.field_181164_e[k] = d1;
        }
    }
}

