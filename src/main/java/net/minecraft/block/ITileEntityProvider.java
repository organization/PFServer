package net.minecraft.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface ITileEntityProvider
{
    @Nullable
    TileEntity createNewTileEntity(World worldIn, int meta);
}