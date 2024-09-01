/*
 * Decompiled with CFR 0.151.
 */
package client.ui.gui;

import client.Client;
import client.module.Module;
import client.module.ModuleManager;
import client.module.ModuleType;
import client.module.modules.visual.ClickGUI;
import client.ui.fastuni.FontLoader;
import client.ui.font.FontLoaders;
import client.ui.gui.ModulePanel;
import client.ui.gui.impl.ModuleRect;
import client.utils.ColorUtil;
import client.utils.Drag;
import client.utils.HoveringUtil;
import client.utils.RenderUtils;
import client.utils.StencilUtil;
import client.utils.anim.Animation;
import client.utils.anim.Direction;
import client.utils.anim.impl.DecelerateAnimation;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CompactClickgui
extends GuiScreen {
    private final Animation openingAnimation = new DecelerateAnimation(250, 1.0);
    private final Drag drag = new Drag(40.0f, 40.0f);
    private final ModulePanel modulePanel = new ModulePanel();
    private float rectWidth = 400.0f;
    private float rectHeight = 300.0f;
    public boolean typing;
    private HashMap<ModuleType, ArrayList<ModuleRect>> moduleRects;
    private Color firstColor = Color.BLACK;
    private Color secondColor = Color.BLACK;
    private final List<ModuleRect> searchResults = new ArrayList<ModuleRect>();
    private final List<String> searchTerms = new ArrayList<String>();
    private String searchText;

    @Override
    public void onDrag(int mouseX, int mouseY) {
        boolean focusedConfigGui = false;
        int fakeMouseX = focusedConfigGui ? 0 : mouseX;
        int fakeMouseY = focusedConfigGui ? 0 : mouseY;
        this.drag.onDraw(fakeMouseX, fakeMouseY);
    }

    @Override
    public void initGui() {
        this.openingAnimation.setDirection(Direction.FORWARDS);
        this.rectWidth = 500.0f;
        this.rectHeight = 350.0f;
        if (this.moduleRects != null) {
            this.moduleRects.forEach((cat, list) -> list.forEach(ModuleRect::initGui));
        }
        this.modulePanel.initGui();
    }

    public void bloom() {
        float x = this.drag.getX();
        float y = this.drag.getY();
        if (!this.openingAnimation.isDone()) {
            x -= (float)this.width + this.rectWidth / 2.0f;
            x += ((float)this.width + this.rectWidth / 2.0f) * this.openingAnimation.getOutput().floatValue();
        }
        RenderUtils.drawRect2(x, y, this.rectWidth, this.rectHeight, new Color(20, 20, 20).getRGB());
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            this.openingAnimation.setDirection(Direction.BACKWARDS);
        }
        this.modulePanel.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.moduleRects == null) {
            this.moduleRects = new HashMap();
            for (ModuleType category : ModuleType.values()) {
                ArrayList<ModuleRect> modules = new ArrayList<ModuleRect>();
                for (Module module : ModuleManager.getModulesInType(category)) {
                    modules.add(new ModuleRect(module));
                }
                this.moduleRects.put(category, modules);
            }
            this.moduleRects.forEach((cat, list) -> list.forEach(ModuleRect::initGui));
            return;
        }
        this.typing = this.modulePanel.typing;
        boolean focusedConfigGui = false;
        int fakeMouseX = focusedConfigGui ? 0 : mouseX;
        int fakeMouseY = focusedConfigGui ? 0 : mouseY;
        float x = this.drag.getX();
        float y = this.drag.getY();
        if (!this.openingAnimation.isDone()) {
            x -= (float)this.width + this.rectWidth / 2.0f;
            x += ((float)this.width + this.rectWidth / 2.0f) * this.openingAnimation.getOutput().floatValue();
        } else if (this.openingAnimation.getDirection().equals((Object)Direction.BACKWARDS)) {
            this.mc.displayGuiScreen(null);
            return;
        }
        this.rectWidth = 475.0f;
        this.rectHeight = 300.0f;
        ClickGUI clickGUIMod = (ClickGUI)Client.moduleManager.moduleMap.get(ClickGUI.class);
        RenderUtils.drawRect2(x, y, this.rectWidth, this.rectHeight, new Color(27, 27, 27).getRGB());
        RenderUtils.drawRect2(x, y, 90.0, this.rectHeight, new Color(39, 39, 39).getRGB());
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3042);
        switch (clickGUIMod.iconMode) {
            case "SCP": {
                this.mc.getTextureManager().bindTexture(new ResourceLocation("client/SCP.png"));
                break;
            }
            case "Alpha": {
                this.mc.getTextureManager().bindTexture(new ResourceLocation("client/Alpha.png"));
                break;
            }
            case "DearDragon": {
                this.mc.getTextureManager().bindTexture(new ResourceLocation("client/Dragon.png"));
                break;
            }
            case "DearDragon1": {
                this.mc.getTextureManager().bindTexture(new ResourceLocation("client/Dragon1.png"));
                break;
            }
            case "GrimAC": {
                this.mc.getTextureManager().bindTexture(new ResourceLocation("client/Grim.jpg"));
            }
        }
        Gui.drawModalRectWithCustomSizedTexture(x + 5.0f, y + 5.0f, 0.0f, 0.0f, 20.5f, 20.5f, 20.5f, 20.5f);
        String text = "MCP LITE";
        switch (clickGUIMod.clientNameShow) {
            case "ClientName": {
                break;
            }
            case "SCP": {
                text = "SCP";
                break;
            }
            case "Crazy": {
                text = "\u6740\u6740\u6740\u6740\u6740\u6740\u6740\u6740\u6740\u6740\u6740\u5403\u4eba\u5403\u4eba\u5403\u4eba\u5403\u4eba\u5403\u4eba\u5403\u4eba\u5403\u4eba\u5403\u4eba\u6740\u6740\u6740\u6740\u6740cbcbcbcbcbcbcbcbcbcbcbcbcb";
            }
        }
        if (clickGUIMod.clientNameShow.equalsIgnoreCase("crazy")) {
            FontLoader.miMiFont22.drawString(text, x + 33.0f, y + 7.0f, Color.red.getRGB());
        } else {
            FontLoaders.GoogleSans22.drawString(text, x + 33.0f, y + 7.0f, -1);
        }
        FontLoaders.GoogleSans16.drawCenteredString("2.0", x + 31.0f + (float)FontLoaders.GoogleSans22.getStringWidth(text) / 2.0f, y + 19.0f, -1);
        boolean searching = false;
        float bannerHeight = 37.5f;
        RenderUtils.drawRect2(x + 5.0f, y + 31.0f, 80.0, 0.5, new Color(110, 110, 110).getRGB());
        RenderUtils.drawRect2(x + 5.0f, y + this.rectHeight - (bannerHeight + 3.0f), 80.0, 0.5, new Color(110, 110, 110).getRGB());
        float minus = bannerHeight + 3.0f + 33.0f;
        float catHeight = (this.rectHeight - minus) / (float)ModuleType.values().length;
        float seperation = 0.0f;
        for (ModuleType category : ModuleType.values()) {
            Color selectColor;
            float catY = y + 33.0f + seperation;
            boolean hovering = HoveringUtil.isHovering(x, catY + 8.0f, 90.0f, catHeight - 16.0f, fakeMouseX, fakeMouseY);
            Color categoryColor = hovering ? ColorUtil.tripleColor(110).brighter() : ColorUtil.tripleColor(110);
            Color color = selectColor = clickGUIMod.getActiveCategory() == category ? Color.WHITE : categoryColor;
            if (!searching && clickGUIMod.getActiveCategory() == category) {
                RenderUtils.drawRect2(x, catY, 90.0, catHeight, new Color(27, 27, 27).getRGB());
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            FontLoaders.GoogleSans22.drawString(category.name(), x + 8.0f, catY + FontLoaders.GoogleSans22.getMiddleOfBox(catHeight), selectColor.getRGB());
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            seperation += catHeight;
        }
        this.modulePanel.currentCat = searching ? null : clickGUIMod.getActiveCategory();
        this.modulePanel.moduleRects = this.getModuleRects(clickGUIMod.getActiveCategory());
        this.modulePanel.x = x;
        this.modulePanel.y = y;
        this.modulePanel.rectHeight = this.rectHeight;
        this.modulePanel.rectWidth = this.rectWidth;
        StencilUtil.initStencilToWrite();
        RenderUtils.drawRect2(x, y, this.rectWidth, this.rectHeight, -1);
        StencilUtil.readStencilBuffer(1);
        this.modulePanel.drawScreen(fakeMouseX, fakeMouseY);
        StencilUtil.uninitStencilBuffer();
        this.modulePanel.drawTooltips(fakeMouseX, fakeMouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.drag.onClick(mouseX, mouseY, mouseButton, HoveringUtil.isHovering(this.drag.getX(), this.drag.getY(), this.rectWidth, 10.0f, mouseX, mouseY));
        float bannerWidth = 90.0f;
        float bannerHeight = 37.5f;
        ClickGUI clickGUIMod = (ClickGUI)Client.moduleManager.moduleMap.get(ClickGUI.class);
        if (HoveringUtil.isHovering(this.drag.getX(), this.drag.getY() + this.rectHeight - bannerHeight, bannerWidth, bannerHeight, mouseX, mouseY)) {
            // empty if block
        }
        int separation = 0;
        float minus = bannerHeight + 3.0f + 33.0f;
        float catHeight = (this.rectHeight - minus) / (float)ModuleType.values().length;
        for (ModuleType category : ModuleType.values()) {
            float catY = this.drag.getY() + 33.0f + (float)separation;
            boolean hovering = HoveringUtil.isHovering(this.drag.getX(), catY + 8.0f, 90.0f, catHeight - 16.0f, mouseX, mouseY);
            if (hovering) {
                clickGUIMod.setActiveCategory(category);
            }
            separation = (int)((float)separation + catHeight);
        }
        this.modulePanel.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.drag.onRelease(state);
        this.modulePanel.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public List<ModuleRect> getModuleRects(ModuleType category) {
        return this.moduleRects.get((Object)category);
    }
}

