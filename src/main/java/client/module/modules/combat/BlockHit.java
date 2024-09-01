/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.combat;

import client.module.Module;
import client.module.ModuleType;

public class BlockHit
extends Module {
    public static BlockHit INSTANCE = new BlockHit();

    public BlockHit() {
        super("BlockHit", 0, true, ModuleType.COMBAT);
    }
}

