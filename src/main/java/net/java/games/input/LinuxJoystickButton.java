/*
 * Decompiled with CFR 0.151.
 */
package net.java.games.input;

import java.io.IOException;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component;

final class LinuxJoystickButton
extends AbstractComponent {
    private float value;

    public LinuxJoystickButton(Component.Identifier button_id) {
        super(button_id.getName(), button_id);
    }

    public final boolean isRelative() {
        return false;
    }

    final void setValue(float value) {
        this.value = value;
    }

    protected final float poll() throws IOException {
        return this.value;
    }
}

