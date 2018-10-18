package org.bukkit.command.defaults;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import mgazul.PFServer.CatServer;
import net.minecraftforge.common.ForgeVersion;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class VersionCommand extends BukkitCommand {
    public VersionCommand(String name) {
        super(name);

        this.description = "Gets the version of this server";
        this.usageMessage = "/version";
        this.setPermission("bukkit.command.version");
        this.setAliases(Arrays.asList("ver"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length == 0) {
            sender.sendMessage("This server is running PFServer version " + CatServer.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ", Forge version "+ForgeVersion.getVersion() + ")");
        } else {
            sender.sendMessage("This server is running PFServer version " + CatServer.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ", Forge version "+ForgeVersion.getVersion() + ")");
        }
        return true;
    }
}
