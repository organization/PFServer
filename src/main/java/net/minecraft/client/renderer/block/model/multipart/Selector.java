package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.gson.*;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.VariantList;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SideOnly(Side.CLIENT)
public class Selector
{
    private final ICondition condition;
    private final VariantList variantList;

    public Selector(ICondition conditionIn, VariantList variantListIn)
    {
        if (conditionIn == null)
        {
            throw new IllegalArgumentException("Missing condition for selector");
        }
        else if (variantListIn == null)
        {
            throw new IllegalArgumentException("Missing variant for selector");
        }
        else
        {
            this.condition = conditionIn;
            this.variantList = variantListIn;
        }
    }

    public VariantList getVariantList()
    {
        return this.variantList;
    }

    public Predicate<IBlockState> getPredicate(BlockStateContainer state)
    {
        return this.condition.getPredicate(state);
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else
        {
            if (p_equals_1_ instanceof Selector)
            {
                Selector selector = (Selector)p_equals_1_;

                if (this.condition.equals(selector.condition))
                {
                    return this.variantList.equals(selector.variantList);
                }
            }

            return false;
        }
    }

    public int hashCode()
    {
        return 31 * this.condition.hashCode() + this.variantList.hashCode();
    }

    @SideOnly(Side.CLIENT)
    public static class Deserializer implements JsonDeserializer<Selector>
        {
            private static final Function<JsonElement, ICondition> FUNCTION_OR_AND = new Function<JsonElement, ICondition>()
            {
                @Nullable
                public ICondition apply(@Nullable JsonElement p_apply_1_)
                {
                    return p_apply_1_ == null ? null : Deserializer.getOrAndCondition(p_apply_1_.getAsJsonObject());
                }
            };
            private static final Function<Entry<String, JsonElement>, ICondition> FUNCTION_PROPERTY_VALUE = new Function<Entry<String, JsonElement>, ICondition>()
            {
                @Nullable
                public ICondition apply(@Nullable Entry<String, JsonElement> p_apply_1_)
                {
                    return p_apply_1_ == null ? null : Deserializer.makePropertyValue(p_apply_1_);
                }
            };

            public Selector deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
            {
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
                return new Selector(this.getWhenCondition(jsonobject), (VariantList)p_deserialize_3_.deserialize(jsonobject.get("apply"), VariantList.class));
            }

            private ICondition getWhenCondition(JsonObject json)
            {
                return json.has("when") ? getOrAndCondition(JsonUtils.getJsonObject(json, "when")) : ICondition.TRUE;
            }

            @VisibleForTesting
            static ICondition getOrAndCondition(JsonObject json)
            {
                Set<Entry<String, JsonElement>> set = json.entrySet();

                if (set.isEmpty())
                {
                    throw new JsonParseException("No elements found in selector");
                }
                else if (set.size() == 1)
                {
                    if (json.has("OR"))
                    {
                        return new ConditionOr(StreamSupport.stream(JsonUtils.getJsonArray(json, "OR").spliterator(), false).map(FUNCTION_OR_AND).collect(Collectors.toList()));
                    }
                    else
                    {
                        return (ICondition)(json.has("AND") ? new ConditionAnd(StreamSupport.stream(JsonUtils.getJsonArray(json, "AND").spliterator(), false).map(FUNCTION_OR_AND).collect(Collectors.toList())) : makePropertyValue(set.iterator().next()));
                    }
                }
                else
                {
                    return new ConditionAnd(set.stream().map(FUNCTION_PROPERTY_VALUE).collect(Collectors.toList()));
                }
            }

            private static ConditionPropertyValue makePropertyValue(Entry<String, JsonElement> entry)
            {
                return new ConditionPropertyValue(entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
            }
        }
}