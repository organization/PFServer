package lliiooll.PFServer.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import lliiooll.PFServer.MsgSent;

public class Tprefused extends BukkitCommand{

	protected Tprefused(String name) {
		super(name);
		this.usageMessage = "/tprefused";
		this.description = "一个基础传送指令";
	}

	@Override
	public boolean execute(CommandSender sender, String cmd, String[] args) {
		if(sender instanceof Player ) {
			if(cmd.equalsIgnoreCase("tprefused")) {
				Player p = (Player) sender;
				if(p.hasPermission("pfserver.tpa")) {
					if(Tpa.p1.equals(p)) {
						MsgSent.info("§2已拒绝传送", p);
						MsgSent.info("§2玩家已拒绝你的请求", Tpa.p);
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
