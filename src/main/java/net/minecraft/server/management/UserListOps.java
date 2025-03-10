/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.server.management.UserList;
import net.minecraft.server.management.UserListEntry;
import net.minecraft.server.management.UserListOpsEntry;

public class UserListOps
extends UserList<GameProfile, UserListOpsEntry> {
    public UserListOps(File saveFile) {
        super(saveFile);
    }

    @Override
    protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
        return new UserListOpsEntry(entryData);
    }

    @Override
    public String[] getKeys() {
        String[] astring = new String[this.getValues().size()];
        int i = 0;
        for (UserListOpsEntry userlistopsentry : this.getValues().values()) {
            astring[i++] = ((GameProfile)userlistopsentry.getValue()).getName();
        }
        return astring;
    }

    public boolean bypassesPlayerLimit(GameProfile profile) {
        UserListOpsEntry userlistopsentry = (UserListOpsEntry)this.getEntry(profile);
        return userlistopsentry != null ? userlistopsentry.bypassesPlayerLimit() : false;
    }

    @Override
    protected String getObjectKey(GameProfile obj) {
        return obj.getId().toString();
    }

    public GameProfile getGameProfileFromName(String username) {
        for (UserListOpsEntry userlistopsentry : this.getValues().values()) {
            if (!username.equalsIgnoreCase(((GameProfile)userlistopsentry.getValue()).getName())) continue;
            return (GameProfile)userlistopsentry.getValue();
        }
        return null;
    }
}

