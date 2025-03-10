/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.entity;

import java.util.Collection;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SharedMonsterAttributes {
    private static final Logger logger = LogManager.getLogger();
    public static final IAttribute maxHealth = new RangedAttribute(null, "generic.maxHealth", 20.0, 0.0, 1024.0).setDescription("Max Health").setShouldWatch(true);
    public static final IAttribute followRange = new RangedAttribute(null, "generic.followRange", 32.0, 0.0, 2048.0).setDescription("Follow Range");
    public static final IAttribute knockbackResistance = new RangedAttribute(null, "generic.knockbackResistance", 0.0, 0.0, 1.0).setDescription("Knockback Resistance");
    public static final IAttribute movementSpeed = new RangedAttribute(null, "generic.movementSpeed", 0.7f, 0.0, 1024.0).setDescription("Movement Speed").setShouldWatch(true);
    public static final IAttribute attackDamage = new RangedAttribute(null, "generic.attackDamage", 2.0, 0.0, 2048.0);

    public static NBTTagList writeBaseAttributeMapToNBT(BaseAttributeMap map) {
        NBTTagList nbttaglist = new NBTTagList();
        for (IAttributeInstance iattributeinstance : map.getAllAttributes()) {
            nbttaglist.appendTag(SharedMonsterAttributes.writeAttributeInstanceToNBT(iattributeinstance));
        }
        return nbttaglist;
    }

    private static NBTTagCompound writeAttributeInstanceToNBT(IAttributeInstance instance) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        IAttribute iattribute = instance.getAttribute();
        nbttagcompound.setString("Name", iattribute.getAttributeUnlocalizedName());
        nbttagcompound.setDouble("Base", instance.getBaseValue());
        Collection<AttributeModifier> collection = instance.func_111122_c();
        if (collection != null && !collection.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            for (AttributeModifier attributemodifier : collection) {
                if (!attributemodifier.isSaved()) continue;
                nbttaglist.appendTag(SharedMonsterAttributes.writeAttributeModifierToNBT(attributemodifier));
            }
            nbttagcompound.setTag("Modifiers", nbttaglist);
        }
        return nbttagcompound;
    }

    private static NBTTagCompound writeAttributeModifierToNBT(AttributeModifier modifier) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("Name", modifier.getName());
        nbttagcompound.setDouble("Amount", modifier.getAmount());
        nbttagcompound.setInteger("Operation", modifier.getOperation());
        nbttagcompound.setLong("UUIDMost", modifier.getID().getMostSignificantBits());
        nbttagcompound.setLong("UUIDLeast", modifier.getID().getLeastSignificantBits());
        return nbttagcompound;
    }

    public static void setAttributeModifiers(BaseAttributeMap map, NBTTagList list) {
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
            IAttributeInstance iattributeinstance = map.getAttributeInstanceByName(nbttagcompound.getString("Name"));
            if (iattributeinstance != null) {
                SharedMonsterAttributes.applyModifiersToAttributeInstance(iattributeinstance, nbttagcompound);
                continue;
            }
            logger.warn("Ignoring unknown attribute '" + nbttagcompound.getString("Name") + "'");
        }
    }

    private static void applyModifiersToAttributeInstance(IAttributeInstance instance, NBTTagCompound compound) {
        instance.setBaseValue(compound.getDouble("Base"));
        if (compound.hasKey("Modifiers", 9)) {
            NBTTagList nbttaglist = compound.getTagList("Modifiers", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                AttributeModifier attributemodifier = SharedMonsterAttributes.readAttributeModifierFromNBT(nbttaglist.getCompoundTagAt(i));
                if (attributemodifier == null) continue;
                AttributeModifier attributemodifier1 = instance.getModifier(attributemodifier.getID());
                if (attributemodifier1 != null) {
                    instance.removeModifier(attributemodifier1);
                }
                instance.applyModifier(attributemodifier);
            }
        }
    }

    public static AttributeModifier readAttributeModifierFromNBT(NBTTagCompound compound) {
        UUID uuid = new UUID(compound.getLong("UUIDMost"), compound.getLong("UUIDLeast"));
        try {
            return new AttributeModifier(uuid, compound.getString("Name"), compound.getDouble("Amount"), compound.getInteger("Operation"));
        }
        catch (Exception exception) {
            logger.warn("Unable to create attribute: " + exception.getMessage());
            return null;
        }
    }
}

