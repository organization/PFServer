package lliiooll.PFServer.cmd;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import lliiooll.PFServer.MsgSent;

public class Spawn extends BukkitCommand{

	protected Spawn(String name) {
		super(name);
		this.usageMessage = "/spawn";
		this.description = "一个基础指令";
	}

	@Override
	public boolean execute(CommandSender sender, String cmd, String[] args) {
		if(sender instanceof Player ) {
			if(cmd.equalsIgnoreCase("spawn")) {
				Player p = (Player) sender;
				if(p.hasPermission("pfserver.spawn")) {
					File f = new File("PFServer.yml");
					if(!(f.exists())) {
						p.teleport(p.getBedSpawnLocation());
						MsgSent.info("§2传送完毕", p);
					}else {
						YamlConfiguration file = YamlConfiguration.loadConfiguration(f);
						String spawn = file.getString("spawn");
						if(spawn.equals(null)) {
							p.teleport(p.getBedSpawnLocation());
							MsgSent.info("§2传送完毕", p);
						}else {
							int x = file.getInt("spawn.x");
							int y = file.getInt("spawn.y");
							int z = file.getInt("spawn.z");
							String world = file.getString("spawn.world");
							if(world.equals(null)) {
								p.teleport(p.getBedSpawnLocation());
								MsgSent.info("§2传送完毕", p);
							}else {
								World w = Bukkit.getWorld(world);
								Location l = new Location(w, x, y, z);
								p.teleport(l);
								MsgSent.info("§2传送完毕", p);
							}
						}
					}
				}else {
					MsgSent.error("§c你没有使用这个指令的权限", p);
				}
			}
		}else {
			System.out.print("§c控制台无法使用此命令");
		}
		return false;
	}
}
