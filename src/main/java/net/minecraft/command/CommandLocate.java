package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandLocate extends CommandBase
{
    public String getName()
    {
        return "locate";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getUsage(ICommandSender sender)
    {
        return "commands.locate.usage";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 1)
        {
            throw new WrongUsageException("commands.locate.usage");
        }
        else
        {
            String s = args[0];
            BlockPos blockpos = sender.getEntityWorld().findNearestStructure(s, sender.getPosition(), false);

            if (blockpos != null)
            {
                sender.sendMessage(new TextComponentTranslation("commands.locate.success", s, blockpos.getX(), blockpos.getZ()));
            }
            else
            {
                throw new CommandException("commands.locate.failure", s);
            }
        }
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "Stronghold", "Monument", "Village", "Mansion", "EndCity", "Fortress", "Temple", "Mineshaft") : Collections.emptyList();
    }
}