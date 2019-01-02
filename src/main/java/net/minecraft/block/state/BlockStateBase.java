package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public abstract class BlockStateBase implements IBlockState
{
    private static final Joiner COMMA_JOINER = Joiner.on(',');
    private static final Function < Entry < IProperty<?>, Comparable<? >> , String > MAP_ENTRY_TO_STRING = new Function < Entry < IProperty<?>, Comparable<? >> , String > ()
    {
        @Nullable
        public String apply(@Nullable Entry < IProperty<?>, Comparable<? >> p_apply_1_)
        {
            if (p_apply_1_ == null)
            {
                return "<NULL>";
            }
            else
            {
                IProperty<?> iproperty = (IProperty)p_apply_1_.getKey();
                return iproperty.getName() + "=" + this.getPropertyName(iproperty, p_apply_1_.getValue());
            }
        }
        private <T extends Comparable<T>> String getPropertyName(IProperty<T> property, Comparable<?> entry)
        {
            return property.getName((T)entry);
        }
    };

    public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property)
    {
        return this.withProperty(property, cyclePropertyValue(property.getAllowedValues(), this.getValue(property)));
    }

    protected static <T> T cyclePropertyValue(Collection<T> values, T currentValue)
    {
        Iterator<T> iterator = values.iterator();

        while (iterator.hasNext())
        {
            if (iterator.next().equals(currentValue))
            {
                if (iterator.hasNext())
                {
                    return iterator.next();
                }

                return values.iterator().next();
            }
        }

        return iterator.next();
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(Block.REGISTRY.getNameForObject(this.getBlock()));

        if (!this.getProperties().isEmpty())
        {
            stringbuilder.append("[");
            COMMA_JOINER.appendTo(stringbuilder, this.getProperties().entrySet().stream().map(MAP_ENTRY_TO_STRING).collect(Collectors.toList()));
            stringbuilder.append("]");
        }

        return stringbuilder.toString();
    }

    @Nullable
    public com.google.common.collect.ImmutableTable<IProperty<?>, Comparable<?>, IBlockState> getPropertyValueTable()
    {
        return null;
    }
}