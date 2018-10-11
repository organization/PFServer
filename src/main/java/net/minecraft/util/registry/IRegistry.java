package net.minecraft.util.registry;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Set;

public interface IRegistry<K, V> extends Iterable<V>
{
    @Nullable
    @SideOnly(Side.CLIENT)
    V getObject(K name);

    void putObject(K key, V value);

    @SideOnly(Side.CLIENT)
    Set<K> getKeys();
}