package lliiooll.PFServer.cmd;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import lliiooll.PFServer.MsgSent;

public class SetSpawn extends BukkitCommand{

	protected SetSpawn(String name) {
		super(name);
		this.usageMessage = "/spawn";
		this.description = "一个基础指令";
	}

	@Override
	public boolean execute(CommandSender sender, String cmd, String[] args) {
		if(sender instanceof Player ) {
			if(cmd.equalsIgnoreCase("setspawn")) {
				Player p = (Player) sender;
				if(p.hasPermission("pfserver.setspawn")) {
					File f = new File("PFServer.yml");
					if(!(f.exists())) {
						try {
							f.createNewFile();
						} catch (IOException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
					}else {
						YamlConfiguration file = YamlConfiguration.loadConfiguration(f);
						file.set("spawn.x", p.getLocation().getX());
						file.set("spawn.y", p.getLocation().getY());
						file.set("spawn.z", p.getLocation().getZ());
						file.set("spawn.world", p.getLocation().getWorld().getName());
						file.set("spawn.pitch", p.getLocation().getPitch());
						file.set("spawn.yaw", p.getLocation().getYaw());
						
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
