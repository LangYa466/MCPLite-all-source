/*
 * Decompiled with CFR 0.151.
 */
package client.ui.gui;

import client.module.ModuleType;
import client.ui.gui.impl.ModuleRect;
import client.utils.HoveringUtil;
import client.utils.RenderUtils;
import client.utils.Screen;
import client.utils.Scroll;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import net.minecraft.util.MathHelper;

public class ModulePanel
implements Screen {
    public float x;
    public float y;
    public float rectWidth;
    public float rectHeight;
    public ModuleType currentCat;
    public List<ModuleRect> moduleRects;
    private HashMap<ModuleType, Scroll> scrollHashMap;
    private boolean draggingScrollBar;
    public boolean typing;

    @Override
    public void initGui() {
        this.scrollHashMap = new HashMap();
        for (ModuleType category : ModuleType.values()) {
            this.scrollHashMap.put(category, new Scroll());
        }
        this.scrollHashMap.put(null, new Scroll());
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.moduleRects != null) {
            this.moduleRects.forEach(moduleRect -> moduleRect.keyTyped(typedChar, keyCode));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        this.typing = false;
        int count = 0;
        float leftSideHeight = 0.0f;
        float rightSideHeight = 0.0f;
        float maxScrollbarHeight = this.rectHeight - 10.0f;
        Scroll scroll = this.scrollHashMap.get((Object)this.currentCat);
        scroll.onScroll(35);
        for (ModuleRect moduleRect : this.moduleRects) {
            boolean rightSide = count % 2 == 1;
            moduleRect.rectWidth = (this.rectWidth - 130.0f) / 2.0f;
            moduleRect.width = this.rectWidth;
            moduleRect.height = this.rectHeight;
            moduleRect.x = this.x + 100.0f + (rightSide ? moduleRect.rectWidth + 10.0f : 0.0f);
            moduleRect.y = (float)((double)(this.y + 10.0f + (rightSide ? rightSideHeight : leftSideHeight)) + MathHelper.roundToHalf(scroll.getScroll()));
            moduleRect.drawScreen(mouseX, mouseY);
            if (!this.typing) {
                this.typing = moduleRect.typing;
            }
            if (rightSide) {
                rightSideHeight += moduleRect.rectHeight + 30.0f;
            } else {
                leftSideHeight += moduleRect.rectHeight + 30.0f;
            }
            ++count;
        }
        scroll.setMaxScroll(Math.max(0.0f, Math.max(leftSideHeight, rightSideHeight) - 100.0f));
        float scrollBarHeight = maxScrollbarHeight * (this.rectHeight / scroll.getMaxScroll());
        scrollBarHeight = Math.min(this.rectHeight - 10.0f, scrollBarHeight);
        float scrollYMath = -scroll.getScroll() / scroll.getMaxScroll() * (maxScrollbarHeight - scrollBarHeight);
        RenderUtils.drawRect2(this.x + this.rectWidth - 9.0f, this.y + 5.0f + scrollYMath + scrollBarHeight / 2.0f - 2.0f, 3.0, 0.5, new Color(64, 68, 75).getRGB());
        RenderUtils.drawRect2(this.x + this.rectWidth - 9.0f, (double)(this.y + 5.0f + scrollYMath + scrollBarHeight / 2.0f) - 0.5, 3.0, 0.5, new Color(64, 68, 75).getRGB());
        RenderUtils.drawRect2(this.x + this.rectWidth - 9.0f, this.y + 5.0f + scrollYMath + scrollBarHeight / 2.0f + 1.0f, 3.0, 0.5, new Color(64, 68, 75).getRGB());
        if (this.draggingScrollBar) {
            float percentOfScrollableHeight = (this.y + 5.0f - (float)mouseY) / maxScrollbarHeight;
            scroll.setRawScroll(Math.max(Math.min(0.0f, scroll.getMaxScroll() * percentOfScrollableHeight), -scroll.getMaxScroll()));
        }
    }

    public void drawTooltips(int mouseX, int mouseY) {
        this.moduleRects.forEach(moduleRect -> moduleRect.tooltipObject.drawScreen(mouseX, mouseY));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float maxScrollbarHeight = this.rectHeight - 10.0f;
        Scroll scroll = this.scrollHashMap.get((Object)this.currentCat);
        float scrollBarHeight = maxScrollbarHeight * (this.rectHeight / scroll.getMaxScroll());
        float scrollYMath = -scroll.getScroll() / scroll.getMaxScroll() * (maxScrollbarHeight - scrollBarHeight);
        boolean hoveredScrollBar = HoveringUtil.isHovering(this.x + this.rectWidth - 10.0f, this.y + 5.0f + scrollYMath, 5.0f, scrollBarHeight, mouseX, mouseY);
        if (hoveredScrollBar && button == 0) {
            this.draggingScrollBar = true;
        }
        this.moduleRects.forEach(moduleRect -> moduleRect.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.draggingScrollBar) {
            this.draggingScrollBar = false;
        }
        this.moduleRects.forEach(moduleRect -> moduleRect.mouseReleased(mouseX, mouseY, state));
    }
}

