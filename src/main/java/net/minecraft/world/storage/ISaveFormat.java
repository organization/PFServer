package net.minecraft.world.storage;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.IProgressUpdate;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

public interface ISaveFormat
{
    @SideOnly(Side.CLIENT)
    String getName();

    ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata);

    @SideOnly(Side.CLIENT)
    List<WorldSummary> getSaveList() throws AnvilConverterException;

    boolean isOldMapFormat(String saveName);

    @SideOnly(Side.CLIENT)
    void flushCache();

    @Nullable
    @SideOnly(Side.CLIENT)
    WorldInfo getWorldInfo(String saveName);

    @SideOnly(Side.CLIENT)
    boolean isNewLevelIdAcceptable(String saveName);

    @SideOnly(Side.CLIENT)
    boolean deleteWorldDirectory(String saveName);

    @SideOnly(Side.CLIENT)
    void renameWorld(String dirName, String newName);

    @SideOnly(Side.CLIENT)
    boolean isConvertible(String saveName);

    boolean convertMapFormat(String filename, IProgressUpdate progressCallback);

    File getFile(String p_186352_1_, String p_186352_2_);

    @SideOnly(Side.CLIENT)
    boolean canLoadWorld(String saveName);
}