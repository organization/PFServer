package org.bukkit.command.defaults;

import net.minecraftforge.common.ForgeVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class VersionCommand extends BukkitCommand {
    public VersionCommand(String name) {
        super(name);

        this.description = "Gets the version of this server";
        this.usageMessage = "/version";
        this.setPermission("bukkit.command.version");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;
<<<<<<< HEAD
        sender.sendMessage("This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ", Forge version " + ForgeVersion.getVersion() + ")");
=======

        if (args.length == 0) {
            sender.sendMessage("This server is running PFServer version " + PFServer.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ", Forge version "+ForgeVersion.getVersion() + ")");
        } else {
            sender.sendMessage("This server is running PFServer version " + PFServer.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ", Forge version "+ForgeVersion.getVersion() + ")");
        }
>>>>>>> parent of effb38f6... 使用log4j.Logger作为输出
        return true;
    }
}
