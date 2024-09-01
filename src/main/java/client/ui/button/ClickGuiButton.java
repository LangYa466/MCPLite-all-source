/*
 * Decompiled with CFR 0.151.
 */
package client.ui.button;

import client.ui.gui.GuiModuleClick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ClickGuiButton
extends GuiButton {
    private GuiScreen screen;

    public ClickGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, GuiScreen guiScreen) {
        super(buttonId, x, y, widthIn, heightIn, "ClickGui");
        this.screen = guiScreen;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiModuleClick(this.screen));
    }
}

