/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.misc;

import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;

public class DelayRemover
extends Module {
    @Settings
    public boolean jump = true;
    @Settings
    public boolean click = true;

    public DelayRemover() {
        super("DelayRemover", 0, true, ModuleType.MISC);
    }
}

