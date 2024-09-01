/*
 * Decompiled with CFR 0.151.
 */
package client.ui.gui.impl;

import client.module.Module;
import client.ui.font.FontLoaders;
import client.ui.gui.impl.SettingComponents;
import client.utils.ColorUtil;
import client.utils.HoveringUtil;
import client.utils.MSTimer;
import client.utils.RenderUtils;
import client.utils.Screen;
import client.utils.TooltipObject;
import client.utils.anim.Direction;
import client.utils.anim.impl.DecelerateAnimation;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

public class ModuleRect
implements Screen {
    private int searchScore;
    public final Module module;
    private final DecelerateAnimation enableAnimation = new DecelerateAnimation(150, 1.0);
    private final SettingComponents settingComponents;
    public float x;
    public float y;
    public float width;
    public float height;
    public float rectHeight;
    public float rectWidth;
    public Module binding;
    public boolean typing;
    public final TooltipObject tooltipObject = new TooltipObject();
    private final MSTimer timerUtil = new MSTimer();

    public ModuleRect(Module module) {
        this.module = module;
        this.settingComponents = new SettingComponents(module);
    }

    @Override
    public void initGui() {
        this.settingComponents.initGui();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.binding != null) {
            System.out.println(1);
            if (keyCode == 57 || keyCode == 1 || keyCode == 211) {
                this.binding.setKey(0);
            } else {
                this.binding.setKey(keyCode);
            }
            this.binding = null;
        } else {
            this.settingComponents.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        this.typing = false;
        RenderUtils.drawRect2(this.x, this.y, this.rectWidth, 20.0, new Color(39, 39, 39).getRGB());
        Color actualColor = new Color(19, 19, 19);
        if (this.binding != null && !this.typing) {
            this.typing = true;
        }
        FontLoaders.GoogleSans20.drawString(this.module.name, this.x + 5.0f, this.y + FontLoaders.GoogleSans20.getMiddleOfBox(20.0f), -1);
        float bindWidth = FontLoaders.GoogleSans14.getStringWidth(Keyboard.getKeyName(this.module.key)) + 4;
        boolean hovered = HoveringUtil.isHovering(this.x + (float)FontLoaders.GoogleSans20.getStringWidth(this.module.name) + 13.0f, this.y + 6.0f, bindWidth, 8.0f, mouseX, mouseY);
        boolean hoveringModule = HoveringUtil.isHovering(this.x, this.y, this.width, 20.0f, mouseX, mouseY);
        if (!hoveringModule) {
            this.timerUtil.reset();
        }
        this.tooltipObject.setTip("module.getDescription()");
        this.tooltipObject.setRound(false);
        this.tooltipObject.setHovering(this.timerUtil.hasPassed(900L));
        Color bindRect = new Color(64, 68, 75);
        RenderUtils.drawRect2(this.x + (float)FontLoaders.GoogleSans20.getStringWidth(this.module.name) + 13.0f, this.y + 6.0f, FontLoaders.GoogleSans14.getStringWidth(Keyboard.getKeyName(this.module.key)) + 4, 8.0, hovered ? bindRect.brighter().getRGB() : bindRect.getRGB());
        FontLoaders.GoogleSans14.drawCenteredString(Keyboard.getKeyName(this.module.key), this.x + (float)FontLoaders.GoogleSans20.getStringWidth(this.module.name) + 13.0f + bindWidth / 2.0f, this.y + 8.0f, -1);
        RenderUtils.drawRect2(this.x, this.y + 20.0f, this.rectWidth, this.rectHeight, new Color(35, 35, 35).getRGB());
        this.enableAnimation.setDirection(this.module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
        float o = this.enableAnimation.getOutput().floatValue();
        RenderUtils.drawGoodCircle(this.x + this.rectWidth - 10.0f, this.y + 10.0f, 4.0f, ColorUtil.interpolateColor(new Color(64, 68, 75), actualColor, o));
        GlStateManager.pushMatrix();
        GlStateManager.translate(3.5f, 2.0f, 0.0f);
        GlStateManager.scale(o, o, o);
        GlStateManager.translate(-3.5f, -2.0f, 0.0f);
        FontLoaders.GoogleSans16.drawString("O", (this.x + this.rectWidth - 13.5f) / o, (this.y + 8.0f) / o, ColorUtil.interpolateColor(new Color(45, 45, 45), Color.WHITE, o));
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
        this.settingComponents.x = this.x;
        this.settingComponents.y = this.y + 20.0f;
        this.settingComponents.actualColor = actualColor;
        this.settingComponents.rectWidth = this.rectWidth;
        this.settingComponents.drawScreen(mouseX, mouseY);
        if (!this.typing) {
            this.typing = this.settingComponents.typing;
        }
        this.rectHeight = this.settingComponents.size > 0.0f ? this.settingComponents.size : 0.0f;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float bindWidth = FontLoaders.GoogleSans14.getStringWidth(Keyboard.getKeyName(this.module.key)) + 4;
        boolean hovered = HoveringUtil.isHovering(this.x + (float)FontLoaders.GoogleSans20.getStringWidth(this.module.name) + 13.0f, this.y + 6.0f, bindWidth, 8.0f, mouseX, mouseY);
        if (!hovered && HoveringUtil.isHovering(this.x, this.y, this.rectWidth, 20.0f, mouseX, mouseY)) {
            if (button == 0) {
                this.module.toggle();
            }
        } else if (hovered) {
            this.binding = this.module;
            return;
        }
        this.settingComponents.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.settingComponents.mouseReleased(mouseX, mouseY, state);
    }

    public int getSearchScore() {
        return this.searchScore;
    }

    public void setSearchScore(int searchScore) {
        this.searchScore = searchScore;
    }
}

