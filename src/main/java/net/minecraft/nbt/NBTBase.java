/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public abstract class NBTBase {
    public static final String[] NBT_TYPES = new String[]{"END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]"};

    abstract void write(DataOutput var1) throws IOException;

    abstract void read(DataInput var1, int var2, NBTSizeTracker var3) throws IOException;

    public abstract String toString();

    public abstract byte getId();

    protected static NBTBase createNewByType(byte id) {
        switch (id) {
            case 0: {
                return new NBTTagEnd();
            }
            case 1: {
                return new NBTTagByte();
            }
            case 2: {
                return new NBTTagShort();
            }
            case 3: {
                return new NBTTagInt();
            }
            case 4: {
                return new NBTTagLong();
            }
            case 5: {
                return new NBTTagFloat();
            }
            case 6: {
                return new NBTTagDouble();
            }
            case 7: {
                return new NBTTagByteArray();
            }
            case 8: {
                return new NBTTagString();
            }
            case 9: {
                return new NBTTagList();
            }
            case 10: {
                return new NBTTagCompound();
            }
            case 11: {
                return new NBTTagIntArray();
            }
        }
        return null;
    }

    public abstract NBTBase copy();

    public boolean hasNoTags() {
        return false;
    }

    public boolean equals(Object p_equals_1_) {
        if (!(p_equals_1_ instanceof NBTBase)) {
            return false;
        }
        NBTBase nbtbase = (NBTBase)p_equals_1_;
        return this.getId() == nbtbase.getId();
    }

    public int hashCode() {
        return this.getId();
    }

    protected String getString() {
        return this.toString();
    }

    public static abstract class NBTPrimitive
    extends NBTBase {
        public abstract long getLong();

        public abstract int getInt();

        public abstract short getShort();

        public abstract byte getByte();

        public abstract double getDouble();

        public abstract float getFloat();
    }
}

