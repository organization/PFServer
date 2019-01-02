package net.minecraft.entity.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityMinecartCommandBlock extends EntityMinecart
{
    public static final DataParameter<String> COMMAND = EntityDataManager.<String>createKey(EntityMinecartCommandBlock.class, DataSerializers.STRING);
    private static final DataParameter<ITextComponent> LAST_OUTPUT = EntityDataManager.<ITextComponent>createKey(EntityMinecartCommandBlock.class, DataSerializers.TEXT_COMPONENT);
    private final CommandBlockBaseLogic commandBlockLogic = new CommandBlockBaseLogic()
    {
        {
            this.sender = EntityMinecartCommandBlock.this.getBukkitEntity(); // CraftBukkit - Set the sender
        }
        public void updateCommand()
        {
            EntityMinecartCommandBlock.this.getDataManager().set(EntityMinecartCommandBlock.COMMAND, this.getCommand());
            EntityMinecartCommandBlock.this.getDataManager().set(EntityMinecartCommandBlock.LAST_OUTPUT, this.getLastOutput());
        }
        @SideOnly(Side.CLIENT)
        public int getCommandBlockType()
        {
            return 1;
        }
        @SideOnly(Side.CLIENT)
        public void fillInInfo(ByteBuf buf)
        {
            buf.writeInt(EntityMinecartCommandBlock.this.getEntityId());
        }
        public BlockPos getPosition()
        {
            return new BlockPos(EntityMinecartCommandBlock.this.posX, EntityMinecartCommandBlock.this.posY + 0.5D, EntityMinecartCommandBlock.this.posZ);
        }
        public Vec3d getPositionVector()
        {
            return new Vec3d(EntityMinecartCommandBlock.this.posX, EntityMinecartCommandBlock.this.posY, EntityMinecartCommandBlock.this.posZ);
        }
        public World getEntityWorld()
        {
            return EntityMinecartCommandBlock.this.world;
        }
        public Entity getCommandSenderEntity()
        {
            return EntityMinecartCommandBlock.this;
        }
        public MinecraftServer getServer()
        {
            return EntityMinecartCommandBlock.this.world.getMinecraftServer();
        }
    };
    private int activatorRailCooldown;

    public EntityMinecartCommandBlock(World worldIn)
    {
        super(worldIn);
    }

    public EntityMinecartCommandBlock(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    public static void registerFixesMinecartCommand(DataFixer fixer)
    {
        EntityMinecart.registerFixesMinecart(fixer, EntityMinecartCommandBlock.class);
        fixer.registerWalker(FixTypes.ENTITY, (fixer1, compound, versionIn) -> {
            if (TileEntity.getKey(TileEntityCommandBlock.class).equals(new ResourceLocation(compound.getString("id"))))
            {
                compound.setString("id", "Control");
                fixer1.process(FixTypes.BLOCK_ENTITY, compound, versionIn);
                compound.setString("id", "MinecartCommandBlock");
            }

            return compound;
        });
    }

    protected void entityInit()
    {
        super.entityInit();
        this.getDataManager().register(COMMAND, "");
        this.getDataManager().register(LAST_OUTPUT, new TextComponentString(""));
    }

    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.commandBlockLogic.readDataFromNBT(compound);
        this.getDataManager().set(COMMAND, this.getCommandBlockLogic().getCommand());
        this.getDataManager().set(LAST_OUTPUT, this.getCommandBlockLogic().getLastOutput());
    }

    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        this.commandBlockLogic.writeToNBT(compound);
    }

    public Type getType()
    {
        return Type.COMMAND_BLOCK;
    }

    public IBlockState getDefaultDisplayTile()
    {
        return Blocks.COMMAND_BLOCK.getDefaultState();
    }

    public CommandBlockBaseLogic getCommandBlockLogic()
    {
        return this.commandBlockLogic;
    }

    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower)
    {
        if (receivingPower && this.ticksExisted - this.activatorRailCooldown >= 4)
        {
            this.getCommandBlockLogic().trigger(this.world);
            this.activatorRailCooldown = this.ticksExisted;
        }
    }

    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if (super.processInitialInteract(player, hand)) return true;
        this.commandBlockLogic.tryOpenEditCommandBlock(player);
        return false;
    }

    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);

        if (LAST_OUTPUT.equals(key))
        {
            try
            {
                this.commandBlockLogic.setLastOutput((ITextComponent)this.getDataManager().get(LAST_OUTPUT));
            }
            catch (Throwable ignored)
            {
            }
        }
        else if (COMMAND.equals(key))
        {
            this.commandBlockLogic.setCommand((String)this.getDataManager().get(COMMAND));
        }
    }

    public boolean ignoreItemEntityData()
    {
        return true;
    }
}