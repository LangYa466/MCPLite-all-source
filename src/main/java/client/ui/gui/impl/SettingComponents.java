/*
 * Decompiled with CFR 0.151.
 */
package client.ui.gui.impl;

import client.module.Module;
import client.module.Settings;
import client.ui.font.FontLoaders;
import client.utils.ColorUtil;
import client.utils.GuiEvents;
import client.utils.HoveringUtil;
import client.utils.RenderUtils;
import client.utils.Screen;
import client.utils.StringUtils;
import client.utils.TextField;
import client.utils.anim.Animation;
import client.utils.anim.Direction;
import client.utils.anim.impl.DecelerateAnimation;
import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class SettingComponents
implements Screen {
    private final Module module;
    private final HashMap<Field, Float> numberSettingMap = new HashMap();
    private final HashMap<Field, TextField> textFieldMap = new HashMap();
    private final HashMap<Field, Animation> colorSettingMap = new HashMap();
    private final HashMap<Field, Animation> modeSettingMap = new HashMap();
    private final HashMap<Field, Animation> multiBoolMap = new HashMap();
    public float size;
    public Color actualColor;
    public float x;
    public float y;
    public float rectWidth;
    public Field draggingNumber;
    private boolean hueFlag;
    private boolean saturationFlag;
    public boolean typing;

    public SettingComponents(Module module) {
        this.module = module;
        for (Field setting : module.getSettings()) {
            Animation animation;
            if (setting.getType() == Float.class || setting.getType() == Double.class || setting.getType() == Integer.class || setting.getType() == Float.TYPE || setting.getType() == Double.TYPE || setting.getType() == Integer.TYPE) {
                this.numberSettingMap.put(setting, Float.valueOf(0.0f));
            }
            if (setting.getType() == String.class) {
                animation = new DecelerateAnimation(250, 1.0).setDirection(Direction.BACKWARDS);
                this.modeSettingMap.put(setting, animation);
            }
            if (setting.getType() != Boolean.class && setting.getType() != Boolean.TYPE) continue;
            animation = new DecelerateAnimation(250, 1.0).setDirection(Direction.BACKWARDS);
            this.multiBoolMap.put(setting, animation);
        }
    }

    @Override
    public void initGui() {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    public void handle(int mouseX, int mouseY, int button, GuiEvents type) {
        this.typing = false;
        float settingHeight = 16.0f;
        float count = 0.0f;
        Color accentColor = this.actualColor;
        Color disabledColor = new Color(64, 68, 75);
        try {
            for (Field setting : this.module.getSettings()) {
                setting.setAccessible(true);
                float settingY = this.y + count * settingHeight;
                float middleSettingY = (float)MathHelper.roundToHalf(this.y + FontLoaders.GoogleSans16.getMiddleOfBox(settingHeight) + count * settingHeight);
                if (setting.getType() == Float.class || setting.getType() == Double.class || setting.getType() == Integer.class || setting.getType() == Float.TYPE || setting.getType() == Double.TYPE || setting.getType() == Integer.TYPE) {
                    FontLoaders.GoogleSans16.drawString(setting.getName(), this.x + 5.0f, middleSettingY, -1);
                    String value = String.valueOf(MathHelper.round(((Number)setting.get(this.module)).floatValue(), 2));
                    value = value.contains(".") ? value.replaceAll("0*$", "").replaceAll("\\.$", "") : value;
                    String maxValue = Double.toString(MathHelper.round(setting.getAnnotation(Settings.class).maxValue(), 2));
                    float valueWidth = FontLoaders.GoogleSans14.getStringWidth(maxValue);
                    RenderUtils.drawRect2(this.x + this.rectWidth - (valueWidth + 7.0f), settingY + 4.0f, valueWidth + 4.0f, 8.0, disabledColor.getRGB());
                    FontLoaders.GoogleSans14.drawCenteredString(value, this.x + this.rectWidth - (valueWidth + 5.0f) + valueWidth / 2.0f, settingY + 6.0f, -1);
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    float sliderWidth = 50.0f;
                    float sliderHeight = 2.0f;
                    float sliderX = this.x + this.rectWidth - (valueWidth + 4.0f + 10.0f + sliderWidth);
                    float sliderY = settingY + settingHeight / 2.0f - sliderHeight / 2.0f;
                    float sliderRadius = 1.0f;
                    boolean hoveringSlider = HoveringUtil.isHovering(sliderX, sliderY - 2.0f, sliderWidth, sliderHeight + 4.0f, mouseX, mouseY);
                    if (type == GuiEvents.RELEASE) {
                        this.draggingNumber = null;
                    }
                    if (type == GuiEvents.CLICK && hoveringSlider && button == 0) {
                        this.draggingNumber = setting;
                    }
                    double currentValue = ((Number)setting.get(this.module)).doubleValue();
                    if (this.draggingNumber != null && this.draggingNumber == setting) {
                        float percent = Math.min(1.0f, Math.max(0.0f, ((float)mouseX - sliderX) / sliderWidth));
                        double newValue = (double)percent * (setting.getAnnotation(Settings.class).maxValue() - setting.getAnnotation(Settings.class).minValue()) + setting.getAnnotation(Settings.class).minValue();
                        this.setNumberValue(setting, newValue);
                    }
                    float sliderMath = (float)((currentValue - setting.getAnnotation(Settings.class).minValue()) / (setting.getAnnotation(Settings.class).maxValue() - setting.getAnnotation(Settings.class).minValue()));
                    this.numberSettingMap.put(setting, Float.valueOf((float)RenderUtils.animate(sliderWidth * sliderMath, this.numberSettingMap.get(setting).floatValue(), 0.1)));
                    RenderUtils.drawRect2(sliderX, sliderY, sliderWidth, sliderHeight, disabledColor.getRGB());
                    RenderUtils.drawRect2(sliderX, sliderY, Math.max(3.0f, this.numberSettingMap.get(setting).floatValue()), sliderHeight, accentColor.getRGB());
                    float whiteRectWidth = 1.5f;
                    float whiteRectHeight = 6.0f;
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    RenderUtils.drawRect2(sliderX + Math.max(3.0f, this.numberSettingMap.get(setting).floatValue()), sliderY + sliderHeight / 2.0f - whiteRectHeight / 2.0f, whiteRectWidth, whiteRectHeight, -1);
                }
                if (setting.getType() == Boolean.class || setting.getType() == Boolean.TYPE) {
                    FontLoaders.GoogleSans16.drawString(setting.getName(), this.x + 5.0f, middleSettingY, -1);
                    boolean enabled = setting.getBoolean(this.module);
                    float boolWH = 10.0f;
                    float boolX = this.x + this.rectWidth - (boolWH + 6.0f);
                    float boolY = settingY + settingHeight / 2.0f - boolWH / 2.0f;
                    boolean hoveringBool = HoveringUtil.isHovering(boolX - 2.0f, boolY - 2.0f, boolWH + 4.0f, boolWH + 4.0f, mouseX, mouseY);
                    if (type == GuiEvents.CLICK && hoveringBool && button == 0) {
                        setting.setBoolean(this.module, !enabled);
                    }
                    Color rectColor = enabled ? accentColor : disabledColor.brighter();
                    RenderUtils.drawRect2(boolX, boolY, boolWH, boolWH, rectColor.getRGB());
                    RenderUtils.drawRect2(boolX + 0.5f, boolY + 0.5f, boolWH - 1.0f, boolWH - 1.0f, disabledColor.getRGB());
                    if (setting.getBoolean(this.module)) {
                        FontLoaders.GoogleSans16.drawCenteredString("o", boolX + boolWH / 2.0f, boolY + FontLoaders.GoogleSans16.getMiddleOfBox(boolWH) + 0.5f, Color.WHITE.getRGB());
                    }
                }
                if (setting.getType() == String.class) {
                    FontLoaders.GoogleSans16.drawString(setting.getName(), this.x + 5.0f, middleSettingY, -1);
                    float modeRectWidth = (float)FontLoaders.GoogleSans14.getStringWidth(StringUtils.getLongestModeName(Arrays.asList(setting.getAnnotation(Settings.class).list()))) + 10.0f;
                    float modeSize = 10.0f;
                    float realY = settingY + settingHeight / 2.0f - modeSize / 2.0f;
                    boolean hovered = HoveringUtil.isHovering(this.x + this.rectWidth - (modeRectWidth + 5.0f), realY, modeRectWidth, modeSize, mouseX, mouseY);
                    Animation openAnimation = this.modeSettingMap.get(setting);
                    if (!openAnimation.isDone() || openAnimation.getDirection().equals((Object)Direction.FORWARDS)) {
                        Color dropdownColor = ColorUtil.darker(disabledColor, 0.8f);
                        RenderUtils.drawRect2(this.x + this.rectWidth - (modeRectWidth + 5.0f), realY + modeSize, modeRectWidth, (float)(setting.getAnnotation(Settings.class).list().length - 1) * modeSize * openAnimation.getOutput().floatValue(), dropdownColor.getRGB());
                        float seperation = 0.0f;
                        for (String mode : setting.getAnnotation(Settings.class).list()) {
                            boolean hoveringMode;
                            if (mode.equals(setting.get(this.module))) continue;
                            float modeY = realY + 3.5f + 6.0f * openAnimation.getOutput().floatValue() + FontLoaders.GoogleSans14.getMiddleOfBox(modeSize) + seperation;
                            boolean bl = hoveringMode = HoveringUtil.isHovering(this.x + this.rectWidth - (modeRectWidth + 5.0f), modeY - FontLoaders.GoogleSans14.getMiddleOfBox(modeSize), modeRectWidth, modeSize, mouseX, mouseY) && openAnimation.isDone();
                            if (hoveringMode && button == 0 && type == GuiEvents.CLICK) {
                                setting.set(this.module, mode);
                                openAnimation.setDirection(Direction.BACKWARDS);
                                return;
                            }
                            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                            RenderUtils.drawRect2(this.x + this.rectWidth - (modeRectWidth + 5.0f), modeY - FontLoaders.GoogleSans14.getMiddleOfBox(modeSize), modeRectWidth, modeSize, ColorUtil.applyOpacity(hoveringMode ? accentColor : dropdownColor, openAnimation.getOutput().floatValue()).getRGB());
                            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                            FontLoaders.GoogleSans14.drawString(mode, this.x + this.rectWidth - (modeRectWidth + 3.0f), modeY, ColorUtil.applyOpacity(-1, openAnimation.getOutput().floatValue()));
                            seperation += modeSize * openAnimation.getOutput().floatValue();
                        }
                    }
                    RenderUtils.drawRect2(this.x + this.rectWidth - (modeRectWidth + 5.0f), realY, modeRectWidth, modeSize, disabledColor.getRGB());
                    FontLoaders.GoogleSans14.drawString((String)setting.get(this.module), this.x + this.rectWidth - (modeRectWidth + 5.0f) + 2.0f, realY + FontLoaders.GoogleSans14.getMiddleOfBox(modeSize), -1);
                    if (hovered && button == 1 && type == GuiEvents.CLICK) {
                        openAnimation.changeDirection();
                    }
                    RenderUtils.drawClickGuiArrow(this.x + this.rectWidth - 11.0f, realY + modeSize / 2.0f - 1.0f, 4.0f, openAnimation, -1);
                    count += (2.0f + (float)(setting.getAnnotation(Settings.class).list().length - 1) * modeSize) / settingHeight * openAnimation.getOutput().floatValue();
                }
                count += 1.0f;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.size = count * settingHeight;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        this.handle(mouseX, mouseY, -1, GuiEvents.DRAW);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        this.handle(mouseX, mouseY, button, GuiEvents.CLICK);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.handle(mouseX, mouseY, state, GuiEvents.RELEASE);
    }

    private void setNumberValue(Field field, Number number) {
        try {
            if (field.getType() == Float.class || field.getType() == Float.TYPE) {
                float value = number.floatValue();
                field.setFloat(this.module, value);
            }
            if (field.getType() == Double.class || field.getType() == Double.TYPE) {
                double value = number.doubleValue();
                field.setDouble(this.module, value);
            }
            if (field.getType() == Integer.class || field.getType() == Integer.TYPE) {
                int value = number.intValue();
                field.setInt(this.module, value);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

