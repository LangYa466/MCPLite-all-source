/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.misc;

import client.module.Module;
import client.module.ModuleType;

public class NoRotSet
extends Module {
    public static NoRotSet INSTANCE = new NoRotSet("NoRotSet", 0, false, ModuleType.MISC);

    protected NoRotSet(String name, int key, boolean defaultState, ModuleType moduleType) {
        super(name, key, defaultState, moduleType);
    }
}

