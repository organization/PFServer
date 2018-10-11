package net.minecraft.client.renderer.entity;

import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderAreaEffectCloud extends Render<EntityAreaEffectCloud>
{
    public RenderAreaEffectCloud(RenderManager manager)
    {
        super(manager);
    }

    @Nullable
    protected ResourceLocation getEntityTexture(EntityAreaEffectCloud entity)
    {
        return null;
    }
}