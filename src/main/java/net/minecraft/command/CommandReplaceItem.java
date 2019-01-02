package net.minecraft.command;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommandReplaceItem extends CommandBase
{
    private static final Map<String, Integer> SHORTCUTS = Maps.<String, Integer>newHashMap();

    public String getName()
    {
        return "replaceitem";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getUsage(ICommandSender sender)
    {
        return "commands.replaceitem.usage";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.replaceitem.usage");
        }
        else
        {
            boolean flag;

            if ("entity".equals(args[0]))
            {
                flag = false;
            }
            else
            {
                if (!"block".equals(args[0]))
                {
                    throw new WrongUsageException("commands.replaceitem.usage");
                }

                flag = true;
            }

            int i;

            if (flag)
            {
                if (args.length < 6)
                {
                    throw new WrongUsageException("commands.replaceitem.block.usage");
                }

                i = 4;
            }
            else
            {
                if (args.length < 4)
                {
                    throw new WrongUsageException("commands.replaceitem.entity.usage");
                }

                i = 2;
            }

            String s = args[i];
            int j = this.getSlotForShortcut(args[i++]);
            Item item;

            try
            {
                item = getItemByText(sender, args[i]);
            }
            catch (NumberInvalidException numberinvalidexception)
            {
                if (Block.getBlockFromName(args[i]) != Blocks.AIR)
                {
                    throw numberinvalidexception;
                }

                item = null;
            }

            ++i;
            int k = args.length > i ? parseInt(args[i++], 1, item.getItemStackLimit()) : 1;
            int l = args.length > i ? parseInt(args[i++]) : 0;
            ItemStack itemstack = new ItemStack(item, k, l);

            if (args.length > i)
            {
                String s1 = buildString(args, i);

                try
                {
                    itemstack.setTagCompound(JsonToNBT.getTagFromJson(s1));
                }
                catch (NBTException nbtexception)
                {
                    throw new CommandException("commands.replaceitem.tagError", nbtexception.getMessage());
                }
            }

            if (flag)
            {
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
                BlockPos blockpos = parseBlockPos(sender, args, 1, false);
                World world = sender.getEntityWorld();
                TileEntity tileentity = world.getTileEntity(blockpos);

                if (tileentity == null || !(tileentity instanceof IInventory))
                {
                    throw new CommandException("commands.replaceitem.noContainer", blockpos.getX(), blockpos.getY(), blockpos.getZ());
                }

                IInventory iinventory = (IInventory)tileentity;

                if (j >= 0 && j < iinventory.getSizeInventory())
                {
                    iinventory.setInventorySlotContents(j, itemstack);
                }
            }
            else
            {
                Entity entity = getEntity(server, sender, args[1]);
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);

                if (entity instanceof EntityPlayer)
                {
                    ((EntityPlayer)entity).inventoryContainer.detectAndSendChanges();
                }

                if (!entity.replaceItemInInventory(j, itemstack))
                {
                    throw new CommandException("commands.replaceitem.failed", s, k, itemstack.isEmpty() ? "Air" : itemstack.getTextComponent());
                }

                if (entity instanceof EntityPlayer)
                {
                    ((EntityPlayer)entity).inventoryContainer.detectAndSendChanges();
                }
            }

            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, k);
            notifyCommandListener(sender, this, "commands.replaceitem.success", s, k, itemstack.isEmpty() ? "Air" : itemstack.getTextComponent());
        }
    }

    private int getSlotForShortcut(String shortcut) throws CommandException
    {
        if (!SHORTCUTS.containsKey(shortcut))
        {
            throw new CommandException("commands.generic.parameter.invalid", shortcut);
        }
        else
        {
            return (Integer) SHORTCUTS.get(shortcut);
        }
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "entity", "block");
        }
        else if (args.length == 2 && "entity".equals(args[0]))
        {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        else if (args.length >= 2 && args.length <= 4 && "block".equals(args[0]))
        {
            return getTabCompletionCoordinate(args, 1, targetPos);
        }
        else if ((args.length != 3 || !"entity".equals(args[0])) && (args.length != 5 || !"block".equals(args[0])))
        {
            return (args.length != 4 || !"entity".equals(args[0])) && (args.length != 6 || !"block".equals(args[0])) ? Collections.emptyList() : getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys());
        }
        else
        {
            return getListOfStringsMatchingLastWord(args, SHORTCUTS.keySet());
        }
    }

    public boolean isUsernameIndex(String[] args, int index)
    {
        return args.length > 0 && "entity".equals(args[0]) && index == 1;
    }

    static
    {
        for (int i = 0; i < 54; ++i)
        {
            SHORTCUTS.put("slot.container." + i, i);
        }

        for (int j = 0; j < 9; ++j)
        {
            SHORTCUTS.put("slot.hotbar." + j, j);
        }

        for (int k = 0; k < 27; ++k)
        {
            SHORTCUTS.put("slot.inventory." + k, 9 + k);
        }

        for (int l = 0; l < 27; ++l)
        {
            SHORTCUTS.put("slot.enderchest." + l, 200 + l);
        }

        for (int i1 = 0; i1 < 8; ++i1)
        {
            SHORTCUTS.put("slot.villager." + i1, 300 + i1);
        }

        for (int j1 = 0; j1 < 15; ++j1)
        {
            SHORTCUTS.put("slot.horse." + j1, 500 + j1);
        }

        SHORTCUTS.put("slot.weapon", 98);
        SHORTCUTS.put("slot.weapon.mainhand", 98);
        SHORTCUTS.put("slot.weapon.offhand", 99);
        SHORTCUTS.put("slot.armor.head", 100 + EntityEquipmentSlot.HEAD.getIndex());
        SHORTCUTS.put("slot.armor.chest", 100 + EntityEquipmentSlot.CHEST.getIndex());
        SHORTCUTS.put("slot.armor.legs", 100 + EntityEquipmentSlot.LEGS.getIndex());
        SHORTCUTS.put("slot.armor.feet", 100 + EntityEquipmentSlot.FEET.getIndex());
        SHORTCUTS.put("slot.horse.saddle", 400);
        SHORTCUTS.put("slot.horse.armor", 401);
        SHORTCUTS.put("slot.horse.chest", 499);
    }
}