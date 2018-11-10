package lliiooll.PFServer.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import lliiooll.PFServer.MsgSent;

public class Tpaccept extends BukkitCommand{
	
	protected Tpaccept(String name) {
		super(name);
		this.usageMessage = "/tpaccept";
		this.description = "一个基础传送指令";
	}

	@Override
	public boolean execute(CommandSender sender, String cmd, String[] args) {
		if(sender instanceof Player ) {
			if(cmd.equalsIgnoreCase("tpaccept")) {
				Player p = (Player) sender;
				if(p.hasPermission("pfserver.tpa")) {
					if(Tpa.p1.equals(p)) {
						MsgSent.info("§2已接受传送", p);
						Tpa.p.teleport(p.getLocation());
						Tpa.p = null;
						Tpa.p1 = null;
					}else {
						MsgSent.warn("§2没有要处理的请求", p);
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
