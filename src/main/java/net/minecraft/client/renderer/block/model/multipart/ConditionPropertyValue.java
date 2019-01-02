package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class ConditionPropertyValue implements ICondition
{
    private static final Splitter SPLITTER = Splitter.on('|').omitEmptyStrings();
    private final String key;
    private final String value;

    public ConditionPropertyValue(String keyIn, String valueIn)
    {
        this.key = keyIn;
        this.value = valueIn;
    }

    public Predicate<IBlockState> getPredicate(BlockStateContainer blockState)
    {
        final IProperty<?> iproperty = blockState.getProperty(this.key);

        if (iproperty == null)
        {
            throw new RuntimeException(this.toString() + ": Definition: " + blockState + " has no property: " + this.key);
        }
        else
        {
            String s = this.value;
            boolean flag = !s.isEmpty() && s.charAt(0) == '!';

            if (flag)
            {
                s = s.substring(1);
            }

            List<String> list = SPLITTER.splitToList(s);

            if (list.isEmpty())
            {
                throw new RuntimeException(this.toString() + ": has an empty value: " + this.value);
            }
            else
            {
                Predicate<IBlockState> predicate;

                if (list.size() == 1)
                {
                    predicate = this.makePredicate(iproperty, s);
                }
                else
                {
                    predicate = Predicates.or(list.stream().map(p_apply_1_ -> ConditionPropertyValue.this.makePredicate(iproperty, p_apply_1_)).collect(Collectors.toList()));
                }

                return flag ? Predicates.not(predicate) : predicate;
            }
        }
    }

    private Predicate<IBlockState> makePredicate(final IProperty<?> property, String valueIn)
    {
        final Optional<?> optional = property.parseValue(valueIn);

        if (!optional.isPresent())
        {
            throw new RuntimeException(this.toString() + ": has an unknown value: " + this.value);
        }
        else
        {
            return p_apply_1_ -> p_apply_1_ != null && p_apply_1_.getValue(property).equals(optional.get());
        }
    }

    public String toString()
    {
        return MoreObjects.toStringHelper(this).add("key", this.key).add("value", this.value).toString();
    }
}