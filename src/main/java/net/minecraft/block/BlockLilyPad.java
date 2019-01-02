package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockLilyPad extends BlockBush
{
    protected static final AxisAlignedBB LILY_PAD_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.09375D, 0.9375D);

    protected BlockLilyPad()
    {
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        if (!(entityIn instanceof EntityBoat))
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, LILY_PAD_AABB);
        }
    }

    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);

        if (entityIn instanceof EntityBoat && !org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(entityIn, pos, Blocks.AIR, 0).isCancelled())
        {
            worldIn.destroyBlock(new BlockPos(pos), true);
        }
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return LILY_PAD_AABB;
    }

    protected boolean canSustainBush(IBlockState state)
    {
        return state.getBlock() == Blocks.WATER || state.getMaterial() == Material.ICE;
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        if (pos.getY() >= 0 && pos.getY() < 256)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.down());
            Material material = iblockstate.getMaterial();
            return material == Material.WATER && (Integer) iblockstate.getValue(BlockLiquid.LEVEL) == 0 || material == Material.ICE;
        }
        else
        {
            return false;
        }
    }

    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }
}