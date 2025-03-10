/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntitySilverfish
extends EntityMob {
    private AISummonSilverfish summonSilverfish;

    public EntitySilverfish(World worldIn) {
        super(worldIn);
        this.setSize(0.4f, 0.3f);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.summonSilverfish = new AISummonSilverfish(this);
        this.tasks.addTask(3, this.summonSilverfish);
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
        this.tasks.addTask(5, new AIHideInStone(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>((EntityCreature)this, EntityPlayer.class, true));
    }

    @Override
    public double getYOffset() {
        return 0.2;
    }

    @Override
    public float getEyeHeight() {
        return 0.1f;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0);
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected String getLivingSound() {
        return "mob.silverfish.say";
    }

    @Override
    public String getHurtSound() {
        return "mob.silverfish.hit";
    }

    @Override
    protected String getDeathSound() {
        return "mob.silverfish.kill";
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        if (source instanceof EntityDamageSource || source == DamageSource.magic) {
            this.summonSilverfish.func_179462_f();
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound("mob.silverfish.step", 0.15f, 1.0f);
    }

    @Override
    protected Item getDropItem() {
        return null;
    }

    @Override
    public void onUpdate() {
        this.renderYawOffset = this.rotationYaw;
        super.onUpdate();
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.stone ? 10.0f : super.getBlockPathWeight(pos);
    }

    @Override
    protected boolean isValidLightLevel() {
        return true;
    }

    @Override
    public boolean getCanSpawnHere() {
        if (super.getCanSpawnHere()) {
            EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, 5.0);
            return entityplayer == null;
        }
        return false;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    static class AISummonSilverfish
    extends EntityAIBase {
        private EntitySilverfish silverfish;
        private int field_179463_b;

        public AISummonSilverfish(EntitySilverfish silverfishIn) {
            this.silverfish = silverfishIn;
        }

        public void func_179462_f() {
            if (this.field_179463_b == 0) {
                this.field_179463_b = 20;
            }
        }

        @Override
        public boolean shouldExecute() {
            return this.field_179463_b > 0;
        }

        @Override
        public void updateTask() {
            --this.field_179463_b;
            if (this.field_179463_b <= 0) {
                World world = this.silverfish.worldObj;
                Random random = this.silverfish.getRNG();
                BlockPos blockpos = new BlockPos(this.silverfish);
                int i = 0;
                while (i <= 5 && i >= -5) {
                    int j = 0;
                    while (j <= 10 && j >= -10) {
                        int k = 0;
                        while (k <= 10 && k >= -10) {
                            BlockPos blockpos1 = blockpos.add(j, i, k);
                            IBlockState iblockstate = world.getBlockState(blockpos1);
                            if (iblockstate.getBlock() == Blocks.monster_egg) {
                                if (world.getGameRules().getBoolean("mobGriefing")) {
                                    world.destroyBlock(blockpos1, true);
                                } else {
                                    world.setBlockState(blockpos1, iblockstate.getValue(BlockSilverfish.VARIANT).getModelBlock(), 3);
                                }
                                if (random.nextBoolean()) {
                                    return;
                                }
                            }
                            k = k <= 0 ? 1 - k : 0 - k;
                        }
                        j = j <= 0 ? 1 - j : 0 - j;
                    }
                    i = i <= 0 ? 1 - i : 0 - i;
                }
            }
        }
    }

    static class AIHideInStone
    extends EntityAIWander {
        private final EntitySilverfish silverfish;
        private EnumFacing facing;
        private boolean field_179484_c;

        public AIHideInStone(EntitySilverfish silverfishIn) {
            super(silverfishIn, 1.0, 10);
            this.silverfish = silverfishIn;
            this.setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            if (this.silverfish.getAttackTarget() != null) {
                return false;
            }
            if (!this.silverfish.getNavigator().noPath()) {
                return false;
            }
            Random random = this.silverfish.getRNG();
            if (random.nextInt(10) == 0) {
                this.facing = EnumFacing.random(random);
                BlockPos blockpos = new BlockPos(this.silverfish.posX, this.silverfish.posY + 0.5, this.silverfish.posZ).offset(this.facing);
                IBlockState iblockstate = this.silverfish.worldObj.getBlockState(blockpos);
                if (BlockSilverfish.canContainSilverfish(iblockstate)) {
                    this.field_179484_c = true;
                    return true;
                }
            }
            this.field_179484_c = false;
            return super.shouldExecute();
        }

        @Override
        public boolean continueExecuting() {
            return this.field_179484_c ? false : super.continueExecuting();
        }

        @Override
        public void startExecuting() {
            if (!this.field_179484_c) {
                super.startExecuting();
            } else {
                World world = this.silverfish.worldObj;
                BlockPos blockpos = new BlockPos(this.silverfish.posX, this.silverfish.posY + 0.5, this.silverfish.posZ).offset(this.facing);
                IBlockState iblockstate = world.getBlockState(blockpos);
                if (BlockSilverfish.canContainSilverfish(iblockstate)) {
                    world.setBlockState(blockpos, Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.forModelBlock(iblockstate)), 3);
                    this.silverfish.spawnExplosionParticle();
                    this.silverfish.setDead();
                }
            }
        }
    }
}

