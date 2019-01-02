package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandListBans extends CommandBase
{
    public String getName()
    {
        return "banlist";
    }

    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return (server.getPlayerList().getBannedIPs().isLanServer() || server.getPlayerList().getBannedPlayers().isLanServer()) && super.checkPermission(server, sender);
    }

    public String getUsage(ICommandSender sender)
    {
        return "commands.banlist.usage";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length >= 1 && "ips".equalsIgnoreCase(args[0]))
        {
            sender.sendMessage(new TextComponentTranslation("commands.banlist.ips", server.getPlayerList().getBannedIPs().getKeys().length));
            sender.sendMessage(new TextComponentString(joinNiceString(server.getPlayerList().getBannedIPs().getKeys())));
        }
        else
        {
            sender.sendMessage(new TextComponentTranslation("commands.banlist.players", server.getPlayerList().getBannedPlayers().getKeys().length));
            sender.sendMessage(new TextComponentString(joinNiceString(server.getPlayerList().getBannedPlayers().getKeys())));
        }
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "players", "ips") : Collections.emptyList();
    }
}