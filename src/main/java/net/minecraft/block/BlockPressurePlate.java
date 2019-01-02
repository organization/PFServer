package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.bukkit.event.entity.EntityInteractEvent;

import java.util.List;

public class BlockPressurePlate extends BlockBasePressurePlate
{
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    private final Sensitivity sensitivity;

    protected BlockPressurePlate(Material materialIn, Sensitivity sensitivityIn)
    {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, Boolean.FALSE));
        this.sensitivity = sensitivityIn;
    }

    protected int getRedstoneStrength(IBlockState state)
    {
        return (Boolean) state.getValue(POWERED) ? 15 : 0;
    }

    protected IBlockState setRedstoneStrength(IBlockState state, int strength)
    {
        return state.withProperty(POWERED, strength > 0);
    }

    protected void playClickOnSound(World worldIn, BlockPos color)
    {
        if (this.blockMaterial == Material.WOOD)
        {
            worldIn.playSound((EntityPlayer)null, color, SoundEvents.BLOCK_WOOD_PRESSPLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
        }
        else
        {
            worldIn.playSound((EntityPlayer)null, color, SoundEvents.BLOCK_STONE_PRESSPLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
        }
    }

    protected void playClickOffSound(World worldIn, BlockPos pos)
    {
        if (this.blockMaterial == Material.WOOD)
        {
            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_WOOD_PRESSPLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
        }
        else
        {
            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_STONE_PRESSPLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
        }
    }

    protected int computeRedstoneStrength(World worldIn, BlockPos pos)
    {
        AxisAlignedBB axisalignedbb = PRESSURE_AABB.offset(pos);
        List <? extends Entity > list;

        switch (this.sensitivity)
        {
            case EVERYTHING:
                list = worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb);
                break;
            case MOBS:
                list = worldIn.<Entity>getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
                break;
            default:
                return 0;
        }

        if (!list.isEmpty())
        {
            for (Entity entity : list)
            {
                if (this.getRedstoneStrength(worldIn.getBlockState(pos)) == 0) {
                    org.bukkit.World bworld = worldIn.getWorld();
                    org.bukkit.plugin.PluginManager manager = worldIn.getServer().getPluginManager();
                    org.bukkit.event.Cancellable cancellable;

                    if (entity instanceof EntityPlayer) {
                        cancellable = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent((EntityPlayer) entity, org.bukkit.event.block.Action.PHYSICAL, pos, null, null, null);
                    } else {
                        cancellable = new EntityInteractEvent(entity.getBukkitEntity(), bworld.getBlockAt(pos.getX(), pos.getY(), pos.getZ()));
                        manager.callEvent((EntityInteractEvent) cancellable);
                    }

                    // We only want to block turning the plate on if all events are cancelled
                    if (cancellable.isCancelled()) {
                        continue;
                    }
                }

                if (!entity.doesEntityNotTriggerPressurePlate())
                {
                    return 15;
                }
            }
        }

        return 0;
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(POWERED, meta == 1);
    }

    public int getMetaFromState(IBlockState state)
    {
        return (Boolean) state.getValue(POWERED) ? 1 : 0;
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, POWERED);
    }

    public static enum Sensitivity
    {
        EVERYTHING,
        MOBS
    }
}