package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMaterialMatcher;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Random;

public class BlockSkull extends BlockContainer
{
    public static final PropertyDirection FACING = BlockDirectional.FACING;
    public static final PropertyBool NODROP = PropertyBool.create("nodrop");
    private static final Predicate<BlockWorldState> IS_WITHER_SKELETON = p_apply_1_ -> p_apply_1_.getBlockState() != null && p_apply_1_.getBlockState().getBlock() == Blocks.SKULL && p_apply_1_.getTileEntity() instanceof TileEntitySkull && ((TileEntitySkull)p_apply_1_.getTileEntity()).getSkullType() == 1;
    protected static final AxisAlignedBB DEFAULT_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.5D, 0.75D);
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.25D, 0.25D, 0.5D, 0.75D, 0.75D, 1.0D);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 0.5D);
    protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.5D, 0.25D, 0.25D, 1.0D, 0.75D, 0.75D);
    protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.25D, 0.25D, 0.5D, 0.75D, 0.75D);
    private BlockPattern witherBasePattern;
    private BlockPattern witherPattern;

    protected BlockSkull()
    {
        super(Material.CIRCUITS);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(NODROP, Boolean.FALSE));
    }

    public String getLocalizedName()
    {
        return I18n.translateToLocal("tile.skull.skeleton.name");
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state)
    {
        return true;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        switch ((EnumFacing)state.getValue(FACING))
        {
            case UP:
            default:
                return DEFAULT_AABB;
            case NORTH:
                return NORTH_AABB;
            case SOUTH:
                return SOUTH_AABB;
            case WEST:
                return WEST_AABB;
            case EAST:
                return EAST_AABB;
        }
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(NODROP, Boolean.FALSE);
    }

    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntitySkull();
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        int i = 0;
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntitySkull)
        {
            i = ((TileEntitySkull)tileentity).getSkullType();
        }

        return new ItemStack(Items.SKULL, 1, i);
    }

    // CraftBukkit start - Special case dropping so we can get info from the tile entity
    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos blockposition, IBlockState iblockdata, float f, int i) {
        if (world.rand.nextFloat() < f) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntitySkull) {
                TileEntitySkull tileentityskull = (TileEntitySkull) tileentity;
                ItemStack itemstack = this.getItem(world, blockposition, iblockdata);

                if (tileentityskull.getSkullType() == 3 && tileentityskull.getPlayerProfile() != null) {
                    itemstack.setTagCompound(new NBTTagCompound());
                    NBTTagCompound nbttagcompound = new NBTTagCompound();

                    NBTUtil.writeGameProfile(nbttagcompound, tileentityskull.getPlayerProfile());
                    itemstack.getTagCompound().setTag("SkullOwner", nbttagcompound);
                }

                spawnAsEntity(world, blockposition, itemstack);
            }
        }
    }
    // CraftBukkit end

    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode)
        {
            state = state.withProperty(NODROP, Boolean.TRUE);
            worldIn.setBlockState(pos, state, 4);
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
    }
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, IBlockAccess worldIn, BlockPos pos, IBlockState state, int fortune)
    {
        {
            // CraftBukkit start - Drop item in code above, not here
            // if (!((Boolean)state.getValue(NODROP)).booleanValue())
            if (false)
            {
                TileEntity tileentity = worldIn.getTileEntity(pos);

                if (tileentity instanceof TileEntitySkull)
                {
                    TileEntitySkull tileentityskull = (TileEntitySkull)tileentity;
                    ItemStack itemstack = new ItemStack(Items.SKULL, 1, tileentityskull.getSkullType());

                    if (tileentityskull.getSkullType() == 3 && tileentityskull.getPlayerProfile() != null)
                    {
                        itemstack.setTagCompound(new NBTTagCompound());
                        NBTTagCompound nbttagcompound = new NBTTagCompound();
                        NBTUtil.writeGameProfile(nbttagcompound, tileentityskull.getPlayerProfile());
                        itemstack.getTagCompound().setTag("SkullOwner", nbttagcompound);
                    }

                    drops.add(itemstack);
                }
            }
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.SKULL;
    }

    public boolean canDispenserPlace(World worldIn, BlockPos pos, ItemStack stack)
    {
        if (stack.getMetadata() == 1 && pos.getY() >= 2 && worldIn.getDifficulty() != EnumDifficulty.PEACEFUL && !worldIn.isRemote)
        {
            return this.getWitherBasePattern().match(worldIn, pos) != null;
        }
        else
        {
            return false;
        }
    }

    public void checkWitherSpawn(World worldIn, BlockPos pos, TileEntitySkull te)
    {
        if (worldIn.captureBlockSnapshots) return;
        if (te.getSkullType() == 1 && pos.getY() >= 2 && worldIn.getDifficulty() != EnumDifficulty.PEACEFUL && !worldIn.isRemote)
        {
            BlockPattern blockpattern = this.getWitherPattern();
            BlockPattern.PatternHelper blockpattern$patternhelper = blockpattern.match(worldIn, pos);

            if (blockpattern$patternhelper != null)
            {
                BlockStateListPopulator blockList = new BlockStateListPopulator(worldIn.getWorld());
                for (int i = 0; i < 3; ++i)
                {
                    BlockWorldState blockworldstate = blockpattern$patternhelper.translateOffset(i, 0, 0);
                    BlockPos pos1 = blockworldstate.getPos();
                    IBlockState data = blockworldstate.getBlockState().withProperty(BlockSkull.NODROP, true);
                    blockList.setTypeAndData(pos1.getX(), pos1.getY(), pos1.getZ(), data.getBlock(), data.getBlock().getMetaFromState(data), 2);
//                    worldIn.setBlockState(blockworldstate.getPos(), blockworldstate.getBlockState().withProperty(NODROP, Boolean.valueOf(true)), 2);
                }

                for (int j = 0; j < blockpattern.getPalmLength(); ++j)
                {
                    for (int k = 0; k < blockpattern.getThumbLength(); ++k)
                    {
                        BlockWorldState blockworldstate1 = blockpattern$patternhelper.translateOffset(j, k, 0);
                        BlockPos pos1 = blockworldstate1.getPos();
                        blockList.setTypeAndData(pos1.getX(), pos1.getY(), pos1.getZ(), Blocks.AIR, 0, 2);
//                        worldIn.setBlockState(blockworldstate1.getPos(), Blocks.AIR.getDefaultState(), 2);

                    }
                }

                BlockPos blockpos = blockpattern$patternhelper.translateOffset(1, 0, 0).getPos();
                EntityWither entitywither = new EntityWither(worldIn);
                BlockPos blockpos1 = blockpattern$patternhelper.translateOffset(1, 2, 0).getPos();
                entitywither.setLocationAndAngles((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.55D, (double)blockpos1.getZ() + 0.5D, blockpattern$patternhelper.getForwards().getAxis() == EnumFacing.Axis.X ? 0.0F : 90.0F, 0.0F);
                entitywither.renderYawOffset = blockpattern$patternhelper.getForwards().getAxis() == EnumFacing.Axis.X ? 0.0F : 90.0F;
                entitywither.ignite();
                if (worldIn.addEntity(entitywither, CreatureSpawnEvent.SpawnReason.BUILD_WITHER)) {
                    blockList.updateList();

                    for (EntityPlayerMP entityplayermp : worldIn.getEntitiesWithinAABB(EntityPlayerMP.class, entitywither.getEntityBoundingBox().grow(50.0D))) {
                        CriteriaTriggers.SUMMONED_ENTITY.trigger(entityplayermp, entitywither);
                    }

//                worldIn.spawnEntity(entitywither);

                    for (int l = 0; l < 120; ++l) {
                        worldIn.spawnParticle(EnumParticleTypes.SNOWBALL, (double) blockpos.getX() + worldIn.rand.nextDouble(), (double) (blockpos.getY() - 2) + worldIn.rand.nextDouble() * 3.9D, (double) blockpos.getZ() + worldIn.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
                    }

                    for (int i1 = 0; i1 < blockpattern.getPalmLength(); ++i1) {
                        for (int j1 = 0; j1 < blockpattern.getThumbLength(); ++j1) {
                            BlockWorldState blockworldstate2 = blockpattern$patternhelper.translateOffset(i1, j1, 0);
                            worldIn.notifyNeighborsRespectDebug(blockworldstate2.getPos(), Blocks.AIR, false);
                        }
                    }
                }
            }
        }
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7)).withProperty(NODROP, (meta & 8) > 0);
    }

    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getIndex();

        if ((Boolean) state.getValue(NODROP))
        {
            i |= 8;
        }

        return i;
    }

    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, NODROP);
    }

    protected BlockPattern getWitherBasePattern()
    {
        if (this.witherBasePattern == null)
        {
            this.witherBasePattern = FactoryBlockPattern.start().aisle("   ", "###", "~#~").where('#', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.SOUL_SAND))).where('~', BlockWorldState.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
        }

        return this.witherBasePattern;
    }

    protected BlockPattern getWitherPattern()
    {
        if (this.witherPattern == null)
        {
            this.witherPattern = FactoryBlockPattern.start().aisle("^^^", "###", "~#~").where('#', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.SOUL_SAND))).where('^', IS_WITHER_SKELETON).where('~', BlockWorldState.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
        }

        return this.witherPattern;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}