package net.minecraft.entity.monster;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class EntityGolem extends EntityCreature implements IAnimals
{
    public EntityGolem(World worldIn)
    {
        super(worldIn);
    }

    public void fall(float distance, float damageMultiplier)
    {
    }

    @Nullable
    protected SoundEvent getAmbientSound()
    {
        return null;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return null;
    }

    @Nullable
    protected SoundEvent getDeathSound()
    {
        return null;
    }

    public int getTalkInterval()
    {
        return 120;
    }

    public boolean canDespawn()
    {
        return false;
    }
}