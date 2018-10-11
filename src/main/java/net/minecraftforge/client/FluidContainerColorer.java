package net.minecraftforge.client;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;

public class FluidContainerColorer implements IItemColor
{
    @Override
    public int colorMultiplier(@Nonnull ItemStack stack, int tintIndex)
    {
        if (tintIndex != 1) return 0xFFFFFFFF;
        FluidStack fluidStack = FluidUtil.getFluidContained(stack);
        if (fluidStack == null) return 0xFFFFFFFF;
        return fluidStack.getFluid().getColor(fluidStack);
    }
}