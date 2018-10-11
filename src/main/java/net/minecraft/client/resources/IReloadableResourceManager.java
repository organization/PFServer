package net.minecraft.client.resources;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public interface IReloadableResourceManager extends IResourceManager
{
    void reloadResources(List<IResourcePack> resourcesPacksList);

    void registerReloadListener(IResourceManagerReloadListener reloadListener);
}