package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAir extends Item
{
    private final Block block;

    public ItemAir(Block blockIn)
    {
        this.block = blockIn;
    }

    public String getUnlocalizedName(ItemStack stack)
    {
        return this.block.getUnlocalizedName();
    }

    public String getUnlocalizedName()
    {
        return this.block.getUnlocalizedName();
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        this.block.addInformation(stack, worldIn, tooltip, flagIn);
    }
}