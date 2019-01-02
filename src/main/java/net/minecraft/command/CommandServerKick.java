package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandServerKick extends CommandBase
{
    public String getName()
    {
        return "kick";
    }

    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public String getUsage(ICommandSender sender)
    {
        return "commands.kick.usage";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length > 0 && args[0].length() > 1)
        {
            EntityPlayerMP entityplayermp = server.getPlayerList().getPlayerByUsername(args[0]);

            if (entityplayermp == null)
            {
                throw new PlayerNotFoundException("commands.generic.player.notFound", args[0]);
            }
            else
            {
                if (args.length >= 2)
                {
                    ITextComponent itextcomponent = getChatComponentFromNthArg(sender, args, 1);
                    entityplayermp.connection.disconnect(itextcomponent);
                    notifyCommandListener(sender, this, "commands.kick.success.reason", entityplayermp.getName(), itextcomponent.getUnformattedText());
                }
                else
                {
                    entityplayermp.connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.kicked"));
                    notifyCommandListener(sender, this, "commands.kick.success", entityplayermp.getName());
                }
            }
        }
        else
        {
            throw new WrongUsageException("commands.kick.usage");
        }
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        return args.length >= 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.emptyList();
    }
}