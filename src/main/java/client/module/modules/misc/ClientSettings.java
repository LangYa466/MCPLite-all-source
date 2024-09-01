/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.misc;

import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;

public class ClientSettings
extends Module {
    public static final ClientSettings INSTANCE = new ClientSettings();
    @Settings
    public boolean moveFix = true;
    @Settings
    public boolean onHyt = false;
    @Settings(list={"Vanilla", "MCPhoto", "EatPeople", "Pools", "WhiteAndBlue"})
    public String guiMainMenuBackGround = "Vanilla";
    @Settings(list={"Vanilla", "MCPLITE"})
    public String guiMainMenuStyles = "Vanilla";
    @Settings
    public boolean guiSwitchButton = false;
    @Settings
    public boolean chatBorder = true;

    public ClientSettings() {
        super("ClientSettings", 0, false, ModuleType.MISC);
    }

    @Override
    public void onTick() {
        this.setState(false);
    }
}

