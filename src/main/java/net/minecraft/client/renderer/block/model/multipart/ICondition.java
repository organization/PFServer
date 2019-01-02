package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.Predicate;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public interface ICondition
{
    ICondition TRUE = blockState -> p_apply_1_ -> true;
    ICondition FALSE = blockState -> p_apply_1_ -> false;

    Predicate<IBlockState> getPredicate(BlockStateContainer blockState);
}