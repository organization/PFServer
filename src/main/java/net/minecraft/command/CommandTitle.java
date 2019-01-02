package net.minecraft.command;

import com.google.gson.JsonParseException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandTitle extends CommandBase
{
    private static final Logger LOGGER = LogManager.getLogger();

    public String getName()
    {
        return "title";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getUsage(ICommandSender sender)
    {
        return "commands.title.usage";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.title.usage");
        }
        else
        {
            if (args.length < 3)
            {
                if ("title".equals(args[1]) || "subtitle".equals(args[1]) || "actionbar".equals(args[1]))
                {
                    throw new WrongUsageException("commands.title.usage.title");
                }

                if ("times".equals(args[1]))
                {
                    throw new WrongUsageException("commands.title.usage.times");
                }
            }

            EntityPlayerMP entityplayermp = getPlayer(server, sender, args[0]);
            SPacketTitle.Type spackettitle$type = SPacketTitle.Type.byName(args[1]);

            if (spackettitle$type != SPacketTitle.Type.CLEAR && spackettitle$type != SPacketTitle.Type.RESET)
            {
                if (spackettitle$type == SPacketTitle.Type.TIMES)
                {
                    if (args.length != 5)
                    {
                        throw new WrongUsageException("commands.title.usage");
                    }
                    else
                    {
                        int i = parseInt(args[2]);
                        int j = parseInt(args[3]);
                        int k = parseInt(args[4]);
                        SPacketTitle spackettitle2 = new SPacketTitle(i, j, k);
                        entityplayermp.connection.sendPacket(spackettitle2);
                        notifyCommandListener(sender, this, "commands.title.success");
                    }
                }
                else if (args.length < 3)
                {
                    throw new WrongUsageException("commands.title.usage");
                }
                else
                {
                    String s = buildString(args, 2);
                    ITextComponent itextcomponent;

                    try
                    {
                        itextcomponent = ITextComponent.Serializer.jsonToComponent(s);
                    }
                    catch (JsonParseException jsonparseexception)
                    {
                        throw toSyntaxException(jsonparseexception);
                    }

                    SPacketTitle spackettitle1 = new SPacketTitle(spackettitle$type, TextComponentUtils.processComponent(sender, itextcomponent, entityplayermp));
                    entityplayermp.connection.sendPacket(spackettitle1);
                    notifyCommandListener(sender, this, "commands.title.success");
                }
            }
            else if (args.length != 2)
            {
                throw new WrongUsageException("commands.title.usage");
            }
            else
            {
                SPacketTitle spackettitle = new SPacketTitle(spackettitle$type, (ITextComponent)null);
                entityplayermp.connection.sendPacket(spackettitle);
                notifyCommandListener(sender, this, "commands.title.success");
            }
        }
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        else
        {
            return args.length == 2 ? getListOfStringsMatchingLastWord(args, SPacketTitle.Type.getNames()) : Collections.emptyList();
        }
    }

    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}