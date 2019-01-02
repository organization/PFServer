package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SideOnly(Side.CLIENT)
public class ConditionOr implements ICondition
{
    final Iterable<ICondition> conditions;

    public ConditionOr(Iterable<ICondition> conditionsIn)
    {
        this.conditions = conditionsIn;
    }

    public Predicate<IBlockState> getPredicate(final BlockStateContainer blockState)
    {
        return Predicates.or(StreamSupport.stream(this.conditions.spliterator(), false).map(p_apply_1_ -> p_apply_1_ == null ? null : p_apply_1_.getPredicate(blockState)).collect(Collectors.toList()));
    }
}