/*
 * Decompiled with CFR 0.151.
 */
package client.ui.fastuni;

import client.ui.fastuni.StringCache;

public interface IBFFontRenderer {
    public StringCache getStringCache();

    public void setStringCache(StringCache var1);

    public boolean isDropShadowEnabled();

    public void setDropShadowEnabled(boolean var1);

    public boolean isEnabled();

    public void setEnabled(boolean var1);
}

