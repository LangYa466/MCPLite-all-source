/*
 * Decompiled with CFR 0.151.
 */
package client.module.modules.misc;

import client.Client;
import client.module.Module;
import client.module.ModuleType;
import client.module.Settings;
import client.utils.PlayerUtils;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class Teams
extends Module {
    @Settings
    public boolean armor = false;
    @Settings
    public boolean color = true;
    @Settings
    public boolean scoreBoard = false;

    public Teams() {
        super("Teams", 0, ModuleType.MISC);
    }

    public static boolean isSameTeam(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)entity;
            Teams teams = (Teams)Client.moduleManager.moduleMap.get(Teams.class);
            return Objects.requireNonNull(teams).getState() && (teams.armor && PlayerUtils.armorTeam(entityPlayer) || teams.color && PlayerUtils.colorTeam(entityPlayer) || teams.scoreBoard && PlayerUtils.scoreTeam(entityPlayer));
        }
        return false;
    }
}

