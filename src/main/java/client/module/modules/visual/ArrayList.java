/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.visual;

import client.Client;
import client.event.events.Render2DEvent;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.visual.ClientColor;
import client.ui.element.ElementManager;
import client.ui.font.CFontRenderer;
import client.ui.font.FontLoaders;
import client.utils.RenderUtils;
import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

public class ArrayList
extends Module {
    @Settings(list={"Minecraft", "Product"})
    private String font = "Minecraft";
    @Settings(list={"NONE", "Correct", "Reverse"})
    private String order = "NONE";
    @Settings
    private boolean shadow = false;
    @Settings
    private boolean rounded = false;
    @Settings(maxValue=10.0)
    private int spaceX = 0;
    @Settings(maxValue=10.0)
    private int spaceY = 0;
    @Settings(maxValue=255.0)
    private int textAlpha = 255;
    @Settings(maxValue=255.0)
    private int text2Alpha = 255;
    @Settings(maxValue=255.0)
    private int text3Alpha = 255;
    @Settings(maxValue=255.0)
    private int backgroundR = 0;
    @Settings(maxValue=255.0)
    private int backgroundG = 0;
    @Settings(maxValue=255.0)
    private int backgroundB = 0;
    @Settings(maxValue=255.0)
    private int backgroundAlpha = 100;

    public ArrayList() {
        super("ArrayList", 0, true, ModuleType.VISUAL);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        CFontRenderer cFontRenderer = null;
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        FontRenderer fontRenderer = ArrayList.mc.fontRendererObj;
        switch (this.font) {
            case "Minecraft": {
                break;
            }
            case "Product": {
                cFontRenderer = FontLoaders.Tenacity20;
            }
        }
        CFontRenderer finalCFontRenderer = cFontRenderer;
        List<Module> list = Client.moduleManager.modules.stream().filter(module -> !module.hide).collect(Collectors.toList());
        switch (this.order) {
            case "Correct": {
                list.sort(Comparator.comparingInt(e -> this.getLong((Module)e, finalCFontRenderer)).reversed());
                break;
            }
            case "Reverse": {
                list.sort(Comparator.comparingInt(e -> this.getLong((Module)e, finalCFontRenderer)));
            }
        }
        ElementManager.arrayList.posX = (float)(scaledResolution.getScaledWidth() - ArrayList.mc.fontRendererObj.getStringWidth(((Module)list.get((int)0)).name)) + ElementManager.arrayList.moveX;
        ElementManager.arrayList.posY = ElementManager.arrayList.moveY;
        float i = 0.0f;
        for (Module module2 : list) {
            if (!module2.getState()) continue;
            String string = module2.getTag() != null ? module2.name + " " + module2.getTag() : module2.name;
            RenderUtils.drawModule(cFontRenderer, module2.name, module2.getTag(), (float)(scaledResolution.getScaledWidth() - this.getLong(module2, cFontRenderer)) + ElementManager.arrayList.moveX, i + ElementManager.arrayList.moveY, this.spaceX, this.spaceY, new Color(this.backgroundR, this.backgroundG, this.backgroundB, this.backgroundAlpha), RenderUtils.getGradientOffset(new Color(ClientColor.INSTANCE.mixR1, ClientColor.INSTANCE.mixG1, ClientColor.INSTANCE.mixB1, this.textAlpha), new Color(ClientColor.INSTANCE.mixR2, ClientColor.INSTANCE.mixG2, ClientColor.INSTANCE.mixB2, this.text2Alpha), ((double)(ArrayList.mc.thePlayer.ticksExisted * 7) + (double)i * 2.0) % 400.0 / 100.0, this.text3Alpha), this.shadow, this.rounded);
            i += cFontRenderer != null ? (float)cFontRenderer.getHeight() : (float)fontRenderer.FONT_HEIGHT;
            i += (float)this.spaceY;
        }
        ElementManager.arrayList.width = this.getLong((Module)list.get(0), cFontRenderer) + this.spaceY;
        ElementManager.arrayList.height = i + (float)this.spaceY;
    }

    private int getLong(Module module, CFontRenderer cFontRenderer) {
        String tag = module.getTag() != null && !module.getTag().isEmpty() ? module.getTag() + " " : "";
        return cFontRenderer != null ? cFontRenderer.getStringWidth(module.name + tag) : ArrayList.mc.fontRendererObj.getStringWidth(module.getTag() + " " + module.name);
    }
}

