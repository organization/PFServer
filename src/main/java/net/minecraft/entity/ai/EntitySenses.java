package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

import java.util.List;

public class EntitySenses
{
    final EntityLiving entity;
    final List<Entity> seenEntities = Lists.<Entity>newArrayList();
    final List<Entity> unseenEntities = Lists.<Entity>newArrayList();

    public EntitySenses(EntityLiving entityIn)
    {
        this.entity = entityIn;
    }

    public void clearSensingCache()
    {
        this.seenEntities.clear();
        this.unseenEntities.clear();
    }

    public boolean canSee(Entity entityIn)
    {
        if (this.seenEntities.contains(entityIn))
        {
            return true;
        }
        else if (this.unseenEntities.contains(entityIn))
        {
            return false;
        }
        else
        {
            this.entity.world.profiler.startSection("canSee");
            boolean flag = this.entity.canEntityBeSeen(entityIn);
            this.entity.world.profiler.endSection();

            if (flag)
            {
                this.seenEntities.add(entityIn);
            }
            else
            {
                this.unseenEntities.add(entityIn);
            }

            return flag;
        }
    }
}