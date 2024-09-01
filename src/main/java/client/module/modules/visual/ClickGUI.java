/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.visual;

import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.ui.gui.CompactClickgui;
import client.ui.gui.GuiModuleClick;

public class ClickGUI
extends Module {
    private int activeCategory = 0;
    private ModuleType activeCategory2 = ModuleType.COMBAT;
    public static int prevGuiScale;
    GuiModuleClick guiModuleClick;
    @Settings(list={"ClientName", "SCP", "Crazy"})
    public String clientNameShow = "ClientName";
    @Settings(list={"Alpha", "DearDragon", "DearDragon1", "SCP", "GrimAC"})
    public String iconMode = "Alpha";

    public ClickGUI() {
        super("ClickGui", 54, ModuleType.VISUAL);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new CompactClickgui());
    }

    @Override
    public void onTick() {
        this.setState(false);
    }

    @Override
    public void onDisable() {
    }

    public int getActiveCategoryy() {
        return this.activeCategory;
    }

    public ModuleType getActiveCategory() {
        return this.activeCategory2;
    }

    public void setActiveCategory(int activeCategory) {
        this.activeCategory = activeCategory;
    }

    public void setActiveCategory(ModuleType activeCategory) {
        this.activeCategory2 = activeCategory;
    }
}

