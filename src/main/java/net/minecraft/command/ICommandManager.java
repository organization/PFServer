package net.minecraft.command;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface ICommandManager
{
    int executeCommand(ICommandSender sender, String rawCommand);

    List<String> getTabCompletions(ICommandSender sender, String input, @Nullable BlockPos pos);

    List<ICommand> getPossibleCommands(ICommandSender sender);

    Map<String, ICommand> getCommands();
}