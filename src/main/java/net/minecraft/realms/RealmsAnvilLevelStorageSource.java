/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatComparator;

public class RealmsAnvilLevelStorageSource {
    private ISaveFormat levelStorageSource;

    public RealmsAnvilLevelStorageSource(ISaveFormat levelStorageSourceIn) {
        this.levelStorageSource = levelStorageSourceIn;
    }

    public String getName() {
        return this.levelStorageSource.getName();
    }

    public boolean levelExists(String p_levelExists_1_) {
        return this.levelStorageSource.canLoadWorld(p_levelExists_1_);
    }

    public boolean convertLevel(String p_convertLevel_1_, IProgressUpdate p_convertLevel_2_) {
        return this.levelStorageSource.convertMapFormat(p_convertLevel_1_, p_convertLevel_2_);
    }

    public boolean requiresConversion(String p_requiresConversion_1_) {
        return this.levelStorageSource.isOldMapFormat(p_requiresConversion_1_);
    }

    public boolean isNewLevelIdAcceptable(String p_isNewLevelIdAcceptable_1_) {
        return this.levelStorageSource.isNewLevelIdAcceptable(p_isNewLevelIdAcceptable_1_);
    }

    public boolean deleteLevel(String p_deleteLevel_1_) {
        return this.levelStorageSource.deleteWorldDirectory(p_deleteLevel_1_);
    }

    public boolean isConvertible(String p_isConvertible_1_) {
        return this.levelStorageSource.isConvertible(p_isConvertible_1_);
    }

    public void renameLevel(String p_renameLevel_1_, String p_renameLevel_2_) {
        this.levelStorageSource.renameWorld(p_renameLevel_1_, p_renameLevel_2_);
    }

    public void clearAll() {
        this.levelStorageSource.flushCache();
    }

    public List<RealmsLevelSummary> getLevelList() throws AnvilConverterException {
        ArrayList<RealmsLevelSummary> list = Lists.newArrayList();
        for (SaveFormatComparator saveformatcomparator : this.levelStorageSource.getSaveList()) {
            list.add(new RealmsLevelSummary(saveformatcomparator));
        }
        return list;
    }
}

