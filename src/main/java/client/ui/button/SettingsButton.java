/*
 * Decompiled with CFR 0.151.
 */
package client.ui.button;

import client.Client;
import client.module.Module;
import client.module.Settings;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class SettingsButton
extends GuiButton {
    private boolean dragged = false;
    private List<Float> floatValues = new ArrayList<Float>();
    private List<Double> doubleValues = new ArrayList<Double>();
    private List<Integer> integerValues = new ArrayList<Integer>();
    private List<String> stringList = new ArrayList<String>();
    private float sliderValue;
    private Module module;
    private Field field;
    private String name;
    private boolean isSlider = false;
    private boolean isList = false;
    private boolean isFloat = false;
    private boolean isInt = false;
    private boolean isDouble = false;
    private boolean isBoolean = false;

    public SettingsButton(int buttonId, int x, int y, String buttonText, Field field, Module module) {
        super(buttonId, x, y, 150, 20, buttonText);
        this.field = field;
        this.module = module;
        this.name = buttonText;
        if (field.getType() == Boolean.class || field.getType() == Boolean.TYPE) {
            this.isBoolean = true;
        }
        if (field.getType() == Float.class || field.getType() == Float.TYPE) {
            float i = (float)((Settings)field.getAnnotations()[0]).minValue();
            while (i <= (float)((Settings)field.getAnnotations()[0]).maxValue()) {
                this.floatValues.add(Float.valueOf(i));
                i = (float)((double)i + 0.01);
            }
            this.displayString = this.name + ":" + this.getFloatValue();
            this.isFloat = true;
        }
        if (field.getType() == Double.class || field.getType() == Double.TYPE) {
            for (double i = ((Settings)field.getAnnotations()[0]).minValue(); i <= ((Settings)field.getAnnotations()[0]).maxValue(); i += 0.01) {
                this.doubleValues.add(i);
            }
            this.displayString = this.name + ":" + this.getDoubleValue();
            this.isDouble = true;
        }
        if (field.getType() == Integer.class || field.getType() == Integer.TYPE) {
            int i = (int)((Settings)field.getAnnotations()[0]).minValue();
            while ((double)i <= ((Settings)field.getAnnotations()[0]).maxValue()) {
                this.integerValues.add(i);
                ++i;
            }
            this.displayString = this.name + ":" + this.getIntValue();
            this.isInt = true;
        }
        if (field.getAnnotation(Settings.class).list().length != 0 && field.getType() == String.class) {
            this.stringList = Arrays.asList(field.getAnnotation(Settings.class).list());
            for (int i = 0; i < ((Settings)field.getAnnotations()[0]).list().length; ++i) {
                this.integerValues.add(i);
            }
            this.displayString = this.name + ":" + this.getStringValue();
            this.isList = true;
        }
        if (this.isBoolean) {
            this.displayString = this.name + ":" + this.getBooleanState();
        }
        if (this.isFloat || this.isDouble || this.isInt || this.isList) {
            this.isSlider = true;
        }
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible && this.isSlider) {
            if (this.dragged) {
                this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0f, 1.0f);
                if (this.isFloat) {
                    this.setNumberValue(this.floatValues.get((int)(this.sliderValue * (float)(this.floatValues.size() - 1))));
                    this.displayString = this.name + ":" + this.getFloatValue();
                }
                if (this.isDouble) {
                    this.setNumberValue(this.doubleValues.get((int)(this.sliderValue * (float)(this.floatValues.size() - 1))));
                    this.displayString = this.name + ":" + this.getDoubleValue();
                }
                if (this.isInt) {
                    this.setNumberValue(this.integerValues.get((int)(this.sliderValue * (float)(this.integerValues.size() - 1))));
                    this.displayString = this.name + ":" + this.getIntValue();
                }
                if (this.isList) {
                    try {
                        this.field.set(this.module, this.stringList.get(this.integerValues.get((int)(this.sliderValue * (float)(this.integerValues.size() - 1)))));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.displayString = this.name + ":" + this.getStringValue();
                }
            }
            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(255.0f, 255.0f, 255.0f, 255.0f);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        if (this.dragged) {
            Client.configManager.saveConfig();
            this.dragged = false;
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.dragged = true;
            if (this.isBoolean) {
                this.toggleState();
                this.displayString = this.name + ":" + this.getBooleanState();
            }
            return true;
        }
        return false;
    }

    private void toggleState() {
        try {
            this.field.setAccessible(true);
            if (this.field.getName().equalsIgnoreCase("state")) {
                this.module.setState(!this.module.getState());
                return;
            }
            this.field.setBoolean(this.module, !this.getBooleanState());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean getBooleanState() {
        try {
            this.field.setAccessible(true);
            return (Boolean)this.field.get(this.module);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private float getFloatValue() {
        try {
            this.field.setAccessible(true);
            return ((Float)this.field.get(this.module)).floatValue();
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private double getDoubleValue() {
        try {
            this.field.setAccessible(true);
            return (Double)this.field.get(this.module);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private int getIntValue() {
        try {
            this.field.setAccessible(true);
            return (Integer)this.field.get(this.module);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String getStringValue() {
        try {
            this.field.setAccessible(true);
            return (String)this.field.get(this.module);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getNumberValuesSize() {
        if (this.isFloat) {
            return this.floatValues.size();
        }
        if (this.isDouble) {
            return this.doubleValues.size();
        }
        return 0;
    }

    private void setNumberValue(Number number) {
        try {
            Number value;
            if (this.isFloat) {
                value = (Float)number;
                this.field.setFloat(this.module, ((Float)value).floatValue());
            }
            if (this.isDouble) {
                value = (Double)number;
                this.field.setDouble(this.module, (Double)value);
            }
            if (this.isInt) {
                value = (Integer)number;
                this.field.setInt(this.module, (Integer)value);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

