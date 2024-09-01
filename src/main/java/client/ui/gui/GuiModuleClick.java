/*
 * Decompiled with CFR 0.151.
 */
package client.ui.gui;

import client.Client;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.module.modules.visual.ClickGUI;
import client.ui.button.SettingsButton;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;

public class GuiModuleClick
extends GuiScreen
implements GuiYesNoCallback {
    private Module currentModule;
    private ModuleType currentType;
    private int level = 0;
    private final GuiOptionButton backButton = new GuiOptionButton(114514, 0, 0, "BACK");
    private final Map<SettingsButton, Module> settings = new HashMap<SettingsButton, Module>();
    private final Map<GuiOptionButton, Module> modules = new HashMap<GuiOptionButton, Module>();
    private final Map<GuiOptionButton, ModuleType> types = new HashMap<GuiOptionButton, ModuleType>();
    private final int BACK_BUTTON = 114514;
    private final int MODULE = 2;
    private final int MODULE_LIST = 1;
    private final int TYPE = 0;
    private GuiScreen screen;

    public GuiModuleClick(GuiScreen screen) {
        this.screen = screen;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 114514 && button.enabled) {
            if (this.level == 2) {
                this.backToModuleList();
            } else if (this.level == 1) {
                this.backToTypeList();
            } else {
                this.mc.displayGuiScreen(this.screen);
            }
        } else if (button.id == 2 && button.enabled) {
            this.openSetting(this.modules.get(button));
        } else if (button.id == 0 && button.enabled) {
            this.currentType = this.types.get(button);
            this.backToModuleList();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
            ClickGUI clickGUI = (ClickGUI)Client.moduleManager.moduleMap.get(ClickGUI.class);
            clickGUI.setState(false);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        this.init();
        super.initGui();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void drawBackground(int tint) {
        super.drawBackground(tint);
    }

    private void backToModuleList() {
        this.level = 1;
        this.modules.forEach((optionButton, module) -> {
            if (module.moduleType == this.currentType) {
                this.setButtonState((GuiButton)optionButton, true);
            } else {
                this.setButtonState((GuiButton)optionButton, false);
            }
        });
        this.settings.forEach((settings, module) -> this.setButtonState((GuiButton)settings, false));
        this.types.forEach((button, type) -> this.setButtonState((GuiButton)button, false));
        this.backButton.displayString = "BACK";
        this.backButton.enabled = true;
    }

    private void backToTypeList() {
        this.level = 0;
        this.modules.forEach((optionButton, module) -> this.setButtonState((GuiButton)optionButton, false));
        this.settings.forEach((settings, module) -> this.setButtonState((GuiButton)settings, false));
        this.types.forEach((button, type) -> this.setButtonState((GuiButton)button, true));
        this.backButton.displayString = "BACK";
    }

    private void openSetting(Module target) {
        this.level = 2;
        this.modules.forEach((keyButton, mapModule) -> {
            if (mapModule.getClass() == target.getClass()) {
                this.currentModule = mapModule;
                this.backButton.displayString = "BACK(" + this.currentModule.name + ")";
                this.modules.forEach((optionButton, module) -> this.setButtonState((GuiButton)optionButton, false));
                this.settings.forEach((settings, module) -> {
                    if (Objects.equals(this.modules.get((Object)keyButton).name, module.name)) {
                        this.setButtonState((GuiButton)settings, true);
                    }
                });
                this.backButton.enabled = true;
            }
        });
        this.types.forEach((button, type) -> this.setButtonState((GuiButton)button, false));
    }

    private void init() {
        this.buttonList.clear();
        this.buttonList.add(this.backButton);
        AtomicInteger id = new AtomicInteger(1);
        Arrays.stream(ModuleType.values()).forEach(moduleType -> {
            GuiOptionButton button = new GuiOptionButton(0, id.get() * 20 / this.height * 150, id.get() * 20 % this.height, moduleType.name());
            this.buttonList.add(button);
            this.types.put(button, (ModuleType)((Object)moduleType));
            this.setButtonState(button, false);
            id.getAndIncrement();
        });
        for (ModuleType type : ModuleType.values()) {
            id.set(1);
            Client.moduleManager.modules.forEach(module -> {
                if (type == module.moduleType) {
                    GuiOptionButton moduleButton = new GuiOptionButton(2, id.get() * 20 / this.height * 150, id.get() * 20 % this.height, module.name);
                    this.buttonList.add(moduleButton);
                    this.modules.put(moduleButton, (Module)module);
                    for (int i = 0; i < module.getSettings().length; ++i) {
                        Field field = module.getSettings()[i];
                        field.setAccessible(true);
                        SettingsButton settings = new SettingsButton(-1, (i + 1) * 20 / this.height * 150, (i + 1) * 20 % this.height, field.getAnnotation(Settings.class).name().isEmpty() ? field.getName() : field.getAnnotation(Settings.class).name(), field, (Module)module);
                        this.buttonList.add(settings);
                        this.settings.put(settings, (Module)module);
                        if (this.currentModule != null && this.currentModule.name.equalsIgnoreCase(module.name)) continue;
                        this.setButtonState(settings, false);
                    }
                    if (this.currentModule != null) {
                        this.level = 2;
                        this.setButtonState(moduleButton, false);
                        this.backButton.enabled = true;
                        this.backButton.displayString = "BACK(" + this.currentModule.name + ")";
                    }
                    id.getAndIncrement();
                }
            });
        }
        if (this.level == 0) {
            this.backToTypeList();
        } else if (this.level == 1) {
            this.backToModuleList();
        }
    }

    private void setButtonState(GuiButton guiButton, boolean state) {
        if (state) {
            guiButton.visible = true;
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(100L);
                    guiButton.enabled = true;
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
        } else {
            guiButton.visible = false;
            guiButton.enabled = false;
        }
    }
}

