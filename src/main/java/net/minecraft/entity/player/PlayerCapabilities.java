/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.entity.player;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerCapabilities {
    public boolean disableDamage;
    public boolean isFlying;
    public boolean allowFlying;
    public boolean isCreativeMode;
    public boolean allowEdit = true;
    private float flySpeed = 0.05f;
    private float walkSpeed = 0.1f;

    public void writeCapabilitiesToNBT(NBTTagCompound tagCompound) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setBoolean("invulnerable", this.disableDamage);
        nbttagcompound.setBoolean("flying", this.isFlying);
        nbttagcompound.setBoolean("mayfly", this.allowFlying);
        nbttagcompound.setBoolean("instabuild", this.isCreativeMode);
        nbttagcompound.setBoolean("mayBuild", this.allowEdit);
        nbttagcompound.setFloat("flySpeed", this.flySpeed);
        nbttagcompound.setFloat("walkSpeed", this.walkSpeed);
        tagCompound.setTag("abilities", nbttagcompound);
    }

    public void readCapabilitiesFromNBT(NBTTagCompound tagCompound) {
        if (tagCompound.hasKey("abilities", 10)) {
            NBTTagCompound nbttagcompound = tagCompound.getCompoundTag("abilities");
            this.disableDamage = nbttagcompound.getBoolean("invulnerable");
            this.isFlying = nbttagcompound.getBoolean("flying");
            this.allowFlying = nbttagcompound.getBoolean("mayfly");
            this.isCreativeMode = nbttagcompound.getBoolean("instabuild");
            if (nbttagcompound.hasKey("flySpeed", 99)) {
                this.flySpeed = nbttagcompound.getFloat("flySpeed");
                this.walkSpeed = nbttagcompound.getFloat("walkSpeed");
            }
            if (nbttagcompound.hasKey("mayBuild", 1)) {
                this.allowEdit = nbttagcompound.getBoolean("mayBuild");
            }
        }
    }

    public float getFlySpeed() {
        return this.flySpeed;
    }

    public void setFlySpeed(float speed) {
        this.flySpeed = speed;
    }

    public float getWalkSpeed() {
        return this.walkSpeed;
    }

    public void setPlayerWalkSpeed(float speed) {
        this.walkSpeed = speed;
    }
}

