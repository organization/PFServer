package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public interface ICommand extends Comparable<ICommand>
{
    String getName();

    String getUsage(ICommandSender sender);

    List<String> getAliases();

    void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;

    boolean checkPermission(MinecraftServer server, ICommandSender sender);

    List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos);

    boolean isUsernameIndex(String[] args, int index);
}