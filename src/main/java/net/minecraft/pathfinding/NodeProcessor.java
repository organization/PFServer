package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public abstract class NodeProcessor
{
    protected IBlockAccess blockaccess;
    protected EntityLiving entity;
    protected final IntHashMap<PathPoint> pointMap = new IntHashMap<>();
    protected int entitySizeX;
    protected int entitySizeY;
    protected int entitySizeZ;
    protected boolean canEnterDoors;
    protected boolean canOpenDoors;
    protected boolean canSwim;

    public void init(IBlockAccess sourceIn, EntityLiving mob)
    {
        this.blockaccess = sourceIn;
        this.entity = mob;
        this.pointMap.clearMap();
        this.entitySizeX = MathHelper.floor(mob.width + 1.0F);
        this.entitySizeY = MathHelper.floor(mob.height + 1.0F);
        this.entitySizeZ = MathHelper.floor(mob.width + 1.0F);
    }

    public void postProcess()
    {
        this.blockaccess = null;
        this.entity = null;
    }

    protected PathPoint openPoint(int x, int y, int z)
    {
        int i = PathPoint.makeHash(x, y, z);
        PathPoint pathpoint = this.pointMap.lookup(i);

        if (pathpoint == null)
        {
            pathpoint = new PathPoint(x, y, z);
            this.pointMap.addKey(i, pathpoint);
        }

        return pathpoint;
    }

    public abstract PathPoint getStart();

    public abstract PathPoint getPathPointToCoords(double x, double y, double z);

    public abstract int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance);

    public abstract PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z, EntityLiving entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn);

    public abstract PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z);

    public void setCanEnterDoors(boolean canEnterDoorsIn)
    {
        this.canEnterDoors = canEnterDoorsIn;
    }

    public void setCanOpenDoors(boolean canOpenDoorsIn)
    {
        this.canOpenDoors = canOpenDoorsIn;
    }

    public void setCanSwim(boolean canSwimIn)
    {
        this.canSwim = canSwimIn;
    }

    public boolean getCanEnterDoors()
    {
        return this.canEnterDoors;
    }

    public boolean getCanOpenDoors()
    {
        return this.canOpenDoors;
    }

    public boolean getCanSwim()
    {
        return this.canSwim;
    }
}