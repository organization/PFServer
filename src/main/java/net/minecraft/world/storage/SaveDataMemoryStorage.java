package net.minecraft.world.storage;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class SaveDataMemoryStorage extends MapStorage
{
    public SaveDataMemoryStorage()
    {
        super((ISaveHandler)null);
    }

    @Nullable
    public WorldSavedData getOrLoadData(Class <? extends WorldSavedData > clazz, String dataIdentifier)
    {
        return this.loadedDataMap.get(dataIdentifier);
    }

    public void setData(String dataIdentifier, WorldSavedData data)
    {
        this.loadedDataMap.put(dataIdentifier, data);
    }

    public void saveAllData()
    {
    }

    public int getUniqueDataId(String key)
    {
        return 0;
    }
}