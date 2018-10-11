package net.minecraft.client.gui.recipebook;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public interface IRecipeUpdateListener
{
    void recipesShown(List<IRecipe> recipes);
}