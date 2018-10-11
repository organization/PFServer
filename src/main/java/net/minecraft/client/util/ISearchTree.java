package net.minecraft.client.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public interface ISearchTree<T>
{
    List<T> search(String searchText);
}