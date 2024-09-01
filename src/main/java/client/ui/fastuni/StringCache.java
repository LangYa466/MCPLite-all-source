/*
 * Decompiled with CFR 0.151.
 */
package client.ui.fastuni;

import client.ui.fastuni.GlyphCache;
import java.awt.Font;
import java.awt.Point;
import java.awt.font.GlyphVector;
import java.lang.ref.WeakReference;
import java.text.Bidi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class StringCache {
    private static final int BASELINE_OFFSET = 7;
    private static final int UNDERLINE_OFFSET = 1;
    private static final int UNDERLINE_THICKNESS = 2;
    private static final int STRIKETHROUGH_OFFSET = -6;
    private static final int STRIKETHROUGH_THICKNESS = 2;
    private GlyphCache glyphCache;
    private int[] colorTable;
    private WeakHashMap<Key, Entry> stringCache = new WeakHashMap();
    private WeakHashMap<String, Key> weakRefCache = new WeakHashMap();
    private Key lookupKey = new Key();
    private Glyph[][] digitGlyphs = new Glyph[4][];
    private boolean digitGlyphsReady = false;
    private boolean antiAliasEnabled = false;
    private Thread mainThread = Thread.currentThread();

    public StringCache(int[] colors) {
        this.glyphCache = new GlyphCache();
        this.colorTable = colors;
        this.cacheDightGlyphs();
    }

    public void setDefaultFont(String fontName, int fontSize, boolean antiAlias) {
        this.glyphCache.setDefaultFont(fontName, fontSize, antiAlias);
        this.antiAliasEnabled = antiAlias;
        this.weakRefCache.clear();
        this.stringCache.clear();
        this.cacheDightGlyphs();
    }

    public void setDefaultFont(Font font, int fontSize, boolean antiAlias) {
        this.glyphCache.setDefaultFont(font, fontSize, true);
        this.antiAliasEnabled = true;
        this.weakRefCache.clear();
        this.stringCache.clear();
        this.cacheDightGlyphs();
    }

    private void cacheDightGlyphs() {
        this.digitGlyphsReady = false;
        this.digitGlyphs[0] = this.cacheString((String)"0123456789").glyphs;
        this.digitGlyphs[1] = this.cacheString((String)"\u00a7l0123456789").glyphs;
        this.digitGlyphs[2] = this.cacheString((String)"\u00a7o0123456789").glyphs;
        this.digitGlyphs[3] = this.cacheString((String)"\u00a7l\u00a7o0123456789").glyphs;
        this.digitGlyphsReady = true;
    }

    public int renderString(String str, float startX, float startY, int initialColor, boolean shadowFlag) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        GL11.glTexEnvi(8960, 8704, 8448);
        Entry entry = this.cacheString(str);
        startY += 7.0f;
        int color = initialColor;
        GlStateManager.color((float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f);
        if (this.antiAliasEnabled) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        GlStateManager.color((float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f);
        byte fontStyle = 0;
        int colorIndex = 0;
        for (int glyphIndex = 0; glyphIndex < entry.glyphs.length; ++glyphIndex) {
            while (colorIndex < entry.colors.length && entry.glyphs[glyphIndex].stringIndex >= entry.colors[colorIndex].stringIndex) {
                color = this.applyColorCode(entry.colors[colorIndex].colorCode, initialColor, shadowFlag);
                fontStyle = entry.colors[colorIndex].fontStyle;
                ++colorIndex;
            }
            Glyph glyph = entry.glyphs[glyphIndex];
            GlyphCache.Entry texture = glyph.texture;
            int glyphX = glyph.x;
            char c = str.charAt(glyph.stringIndex);
            if (c >= '0' && c <= '9') {
                int oldWidth = texture.width;
                texture = this.digitGlyphs[fontStyle][c - 48].texture;
                int newWidth = texture.width;
                glyphX += oldWidth - newWidth >> 1;
            }
            GlStateManager.enableTexture2D();
            tessellator.draw();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            GlStateManager.bindTexture(texture.textureName);
            float x1 = startX + (float)glyphX / 2.0f;
            float x2 = startX + (float)(glyphX + texture.width) / 2.0f;
            float y1 = startY + (float)glyph.y / 2.0f;
            float y2 = startY + (float)(glyph.y + texture.height) / 2.0f;
            int a = color >> 24 & 0xFF;
            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            worldRenderer.pos(x1, y1, 0.0).tex(texture.u1, texture.v1).color(r, g, b, a).endVertex();
            worldRenderer.pos(x1, y2, 0.0).tex(texture.u1, texture.v2).color(r, g, b, a).endVertex();
            worldRenderer.pos(x2, y2, 0.0).tex(texture.u2, texture.v2).color(r, g, b, a).endVertex();
            worldRenderer.pos(x2, y1, 0.0).tex(texture.u2, texture.v1).color(r, g, b, a).endVertex();
        }
        tessellator.draw();
        if (entry.specialRender) {
            int renderStyle = 0;
            color = initialColor;
            GlStateManager.disableTexture2D();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION);
            GlStateManager.color((float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f);
            int colorIndex2 = 0;
            for (int glyphIndex = 0; glyphIndex < entry.glyphs.length; ++glyphIndex) {
                float y2;
                float y1;
                float x2;
                float x1;
                while (colorIndex2 < entry.colors.length && entry.glyphs[glyphIndex].stringIndex >= entry.colors[colorIndex2].stringIndex) {
                    color = this.applyColorCode(entry.colors[colorIndex2].colorCode, initialColor, shadowFlag);
                    renderStyle = entry.colors[colorIndex2].renderStyle;
                    ++colorIndex2;
                }
                Glyph glyph = entry.glyphs[glyphIndex];
                int glyphSpace = glyph.advance - glyph.texture.width;
                if (true) {
                    x1 = startX + (float)(glyph.x - glyphSpace) / 2.0f;
                    x2 = startX + (float)(glyph.x + glyph.advance) / 2.0f;
                    y1 = startY + 0.5f;
                    y2 = startY + 1.5f;
                    worldRenderer.pos(x1, y1, 0.0).endVertex();
                    worldRenderer.pos(x1, y2, 0.0).endVertex();
                    worldRenderer.pos(x2, y2, 0.0).endVertex();
                    worldRenderer.pos(x2, y1, 0.0).endVertex();
                }
                if ((renderStyle & 2) == 0) continue;
                x1 = startX + (float)(glyph.x - glyphSpace) / 2.0f;
                x2 = startX + (float)(glyph.x + glyph.advance) / 2.0f;
                y1 = startY + -3.0f;
                y2 = startY + -2.0f;
                worldRenderer.pos(x1, y1, 0.0).endVertex();
                worldRenderer.pos(x1, y2, 0.0).endVertex();
                worldRenderer.pos(x2, y2, 0.0).endVertex();
                worldRenderer.pos(x2, y1, 0.0).endVertex();
            }
            tessellator.draw();
            GlStateManager.enableTexture2D();
        }
        return entry.advance / 2;
    }

    public int getStringWidth(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        Entry entry = this.cacheString(str);
        return entry.advance / 2;
    }

    private int sizeString(String str, int width, boolean breakAtSpaces) {
        int index;
        if (str == null || str.isEmpty()) {
            return 0;
        }
        width += width;
        Glyph[] glyphs = this.cacheString((String)str).glyphs;
        int wsIndex = -1;
        int advance = 0;
        for (index = 0; index < glyphs.length && advance <= width; advance += glyphs[index].advance, ++index) {
            if (!breakAtSpaces) continue;
            char c = str.charAt(glyphs[index].stringIndex);
            if (c == ' ') {
                wsIndex = index;
                continue;
            }
            if (c != '\n') continue;
            wsIndex = index;
            break;
        }
        if (index < glyphs.length && wsIndex != -1 && wsIndex < index) {
            index = wsIndex;
        }
        return index < glyphs.length ? glyphs[index].stringIndex : str.length();
    }

    public int sizeStringToWidth(String str, int width) {
        return this.sizeString(str, width, true);
    }

    public String trimStringToWidth(String str, int width, boolean reverse) {
        int length = this.sizeString(str, width, false);
        str = str.substring(0, length);
        if (reverse) {
            str = new StringBuilder(str).reverse().toString();
        }
        return str;
    }

    private int applyColorCode(int colorCode, int color, boolean shadowFlag) {
        if (colorCode != -1) {
            colorCode = shadowFlag ? colorCode + 16 : colorCode;
            color = this.colorTable[colorCode] & 0xFFFFFF | color & 0xFF000000;
        }
        Tessellator.getInstance().getWorldRenderer().color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, shadowFlag ? 80 : color >> 24 & 0xFF);
        return color;
    }

    private Entry cacheString(String str) {
        Entry entry = null;
        if (this.mainThread == Thread.currentThread()) {
            this.lookupKey.str = str;
            entry = this.stringCache.get(this.lookupKey);
        }
        if (entry == null) {
            char[] text = str.toCharArray();
            entry = new Entry();
            int length = this.stripColorCodes(entry, str, text);
            ArrayList<Glyph> glyphList = new ArrayList<Glyph>();
            entry.advance = this.layoutBidiString(glyphList, text, 0, length, entry.colors);
            entry.glyphs = new Glyph[glyphList.size()];
            entry.glyphs = glyphList.toArray(entry.glyphs);
            Arrays.sort(entry.glyphs);
            int colorIndex = 0;
            int shift = 0;
            for (int glyphIndex = 0; glyphIndex < entry.glyphs.length; ++glyphIndex) {
                Glyph glyph = entry.glyphs[glyphIndex];
                while (colorIndex < entry.colors.length && glyph.stringIndex + shift >= entry.colors[colorIndex].stringIndex) {
                    shift += 2;
                    ++colorIndex;
                }
                glyph.stringIndex += shift;
            }
            if (this.mainThread == Thread.currentThread()) {
                Key key = new Key();
                key.str = new String(str);
                entry.keyRef = new WeakReference<Key>(key);
                this.stringCache.put(key, entry);
            }
        }
        if (this.mainThread == Thread.currentThread()) {
            Key oldKey = (Key)entry.keyRef.get();
            if (oldKey != null) {
                this.weakRefCache.put(str, oldKey);
            }
            this.lookupKey.str = null;
        }
        return entry;
    }

    private int stripColorCodes(Entry cacheEntry, String str, char[] text) {
        int next;
        ArrayList<ColorCode> colorList = new ArrayList<ColorCode>();
        int start = 0;
        int shift = 0;
        int fontStyle = 0;
        int renderStyle = 0;
        int colorCode = -1;
        while ((next = str.indexOf(167, start)) != -1 && next + 1 < str.length()) {
            System.arraycopy(text, next - shift + 2, text, next - shift, text.length - next - 2);
            int code = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(str.charAt(next + 1)));
            switch (code) {
                case 16: {
                    break;
                }
                case 17: {
                    fontStyle = (byte)(fontStyle | 1);
                    break;
                }
                case 18: {
                    renderStyle = (byte)(renderStyle | 2);
                    cacheEntry.specialRender = true;
                    break;
                }
                case 19: {
                    renderStyle = (byte)(renderStyle | 1);
                    cacheEntry.specialRender = true;
                    break;
                }
                case 20: {
                    fontStyle = (byte)(fontStyle | 2);
                    break;
                }
                case 21: {
                    fontStyle = 0;
                    renderStyle = 0;
                    colorCode = -1;
                    break;
                }
                default: {
                    if (code < 0 || code > 15) break;
                    colorCode = (byte)code;
                    fontStyle = 0;
                    renderStyle = 0;
                }
            }
            ColorCode entry = new ColorCode();
            entry.stringIndex = next;
            entry.stripIndex = next - shift;
            entry.colorCode = (byte)colorCode;
            entry.fontStyle = (byte)fontStyle;
            entry.renderStyle = (byte)renderStyle;
            colorList.add(entry);
            start = next + 2;
            shift += 2;
        }
        cacheEntry.colors = new ColorCode[colorList.size()];
        cacheEntry.colors = colorList.toArray(cacheEntry.colors);
        return text.length - shift;
    }

    private int layoutBidiString(List<Glyph> glyphList, char[] text, int start, int limit, ColorCode[] colors) {
        int advance = 0;
        if (Bidi.requiresBidi(text, start, limit)) {
            Bidi bidi = new Bidi(text, start, null, 0, limit - start, -2);
            if (bidi.isRightToLeft()) {
                return this.layoutStyle(glyphList, text, start, limit, 1, advance, colors);
            }
            int runCount = bidi.getRunCount();
            byte[] levels = new byte[runCount];
            Object[] ranges = new Integer[runCount];
            for (int index = 0; index < runCount; ++index) {
                levels[index] = (byte)bidi.getRunLevel(index);
                ranges[index] = index;
            }
            Bidi.reorderVisually(levels, 0, ranges, 0, runCount);
            for (int visualIndex = 0; visualIndex < runCount; ++visualIndex) {
                int logicalIndex = (Integer)ranges[visualIndex];
                int layoutFlag = (bidi.getRunLevel(logicalIndex) & 1) == 1 ? 1 : 0;
                advance = this.layoutStyle(glyphList, text, start + bidi.getRunStart(logicalIndex), start + bidi.getRunLimit(logicalIndex), layoutFlag, advance, colors);
            }
            return advance;
        }
        return this.layoutStyle(glyphList, text, start, limit, 0, advance, colors);
    }

    private int layoutStyle(List<Glyph> glyphList, char[] text, int start, int limit, int layoutFlags, int advance, ColorCode[] colors) {
        byte currentFontStyle = 0;
        int colorIndex = Arrays.binarySearch(colors, (Object)start);
        if (colorIndex < 0) {
            colorIndex = -colorIndex - 2;
        }
        while (start < limit) {
            int next = limit;
            while (colorIndex >= 0 && colorIndex < colors.length - 1 && colors[colorIndex].stripIndex == colors[colorIndex + 1].stripIndex) {
                ++colorIndex;
            }
            if (colorIndex >= 0 && colorIndex < colors.length) {
                currentFontStyle = colors[colorIndex].fontStyle;
            }
            while (++colorIndex < colors.length) {
                if (colors[colorIndex].fontStyle == currentFontStyle) continue;
                next = colors[colorIndex].stripIndex;
                break;
            }
            advance = this.layoutString(glyphList, text, start, next, layoutFlags, advance, currentFontStyle);
            start = next;
        }
        return advance;
    }

    private int layoutString(List<Glyph> glyphList, char[] text, int start, int limit, int layoutFlags, int advance, int style) {
        if (this.digitGlyphsReady) {
            for (int index = start; index < limit; ++index) {
                if (text[index] < '0' || text[index] > '9') continue;
                text[index] = 48;
            }
        }
        while (start < limit) {
            Font font = this.glyphCache.lookupFont(text, start, limit, style);
            int next = font.canDisplayUpTo(text, start, limit);
            if (next == -1) {
                next = limit;
            }
            if (next == start) {
                ++next;
            }
            advance = this.layoutFont(glyphList, text, start, next, layoutFlags, advance, font);
            start = next;
        }
        return advance;
    }

    private int layoutFont(List<Glyph> glyphList, char[] text, int start, int limit, int layoutFlags, int advance, Font font) {
        if (this.mainThread == Thread.currentThread()) {
            this.glyphCache.cacheGlyphs(font, text, start, limit, layoutFlags);
        }
        GlyphVector vector = this.glyphCache.layoutGlyphVector(font, text, start, limit, layoutFlags);
        Glyph glyph = null;
        int numGlyphs = vector.getNumGlyphs();
        for (int index = 0; index < numGlyphs; ++index) {
            Point position = vector.getGlyphPixelBounds(index, null, advance, 0.0f).getLocation();
            if (glyph != null) {
                glyph.advance = position.x - glyph.x;
            }
            glyph = new Glyph();
            glyph.stringIndex = start + vector.getGlyphCharIndex(index);
            glyph.texture = this.glyphCache.lookupGlyph(font, vector.getGlyphCode(index));
            glyph.x = position.x;
            glyph.y = position.y;
            glyphList.add(glyph);
        }
        advance += (int)vector.getGlyphPosition(numGlyphs).getX();
        if (glyph != null) {
            glyph.advance = advance - glyph.x;
        }
        return advance;
    }

    private static class Glyph
    implements Comparable<Glyph> {
        public int stringIndex;
        public GlyphCache.Entry texture;
        public int x;
        public int y;
        public int advance;

        private Glyph() {
        }

        @Override
        public int compareTo(Glyph o) {
            return this.stringIndex == o.stringIndex ? 0 : (this.stringIndex < o.stringIndex ? -1 : 1);
        }
    }

    private static class ColorCode
    implements Comparable<Integer> {
        public static final byte UNDERLINE = 1;
        public static final byte STRIKETHROUGH = 2;
        public int stringIndex;
        public int stripIndex;
        public byte colorCode;
        public byte fontStyle;
        public byte renderStyle;

        private ColorCode() {
        }

        @Override
        public int compareTo(Integer i) {
            return this.stringIndex == i ? 0 : (this.stringIndex < i ? -1 : 1);
        }
    }

    private static class Entry {
        public WeakReference<Key> keyRef;
        public int advance;
        public Glyph[] glyphs;
        public ColorCode[] colors;
        public boolean specialRender;

        private Entry() {
        }
    }

    private static class Key {
        public String str;

        private Key() {
        }

        public int hashCode() {
            int code = 0;
            int length = this.str.length();
            boolean colorCode = false;
            for (int index = 0; index < length; ++index) {
                int c = this.str.charAt(index);
                if (c >= 48 && c <= 57 && !colorCode) {
                    c = 48;
                }
                code = code * 31 + c;
                colorCode = c == 167;
            }
            return code;
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            String other = o.toString();
            int length = this.str.length();
            if (length != other.length()) {
                return false;
            }
            boolean colorCode = false;
            for (int index = 0; index < length; ++index) {
                char c2;
                char c1 = this.str.charAt(index);
                if (c1 != (c2 = other.charAt(index)) && (c1 < '0' || c1 > '9' || c2 < '0' || c2 > '9' || colorCode)) {
                    return false;
                }
                colorCode = c1 == '\u00a7';
            }
            return true;
        }

        public String toString() {
            return this.str;
        }
    }
}

