package net.minecraft.world.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public interface IPlayerFileData
{
    void writePlayerData(EntityPlayer player);

    @Nullable
    NBTTagCompound readPlayerData(EntityPlayer player);

    String[] getAvailablePlayerDat();
}