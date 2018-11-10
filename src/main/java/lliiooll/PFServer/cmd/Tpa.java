package lliiooll.PFServer.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import lliiooll.PFServer.MsgSent;

public class Tpa extends BukkitCommand{
	
	public static Player p;//请求传送着
	public static Player p1;//传送目标

	protected Tpa(String name) {
		super(name);
		this.usageMessage = "/tpa";
		this.description = "一个基础传送指令";
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender sender, String cmd, String[] args) {
		if(sender instanceof Player ) {
			if(cmd.equalsIgnoreCase("tpa")) {
				Player p = (Player) sender;
				if(p.hasPermission("pfserver.tpa")) {
					if(args.length > 0) {
						MsgSent.info("§2请输入玩家名称", p);
					}else {
						Player p1 = Bukkit.getPlayer(args[0]);
						if(p1.equals(null)) {
							MsgSent.error("§2玩家不在线或不存在", p);
						}else {
							Tpa.p = p;
							Tpa.p1 = p1;
							MsgSent.info("§2玩家§e" + p.getName() + "§2请求传送到你这里来,使用§b/tpaccept§2接受或§b/tprefused§2拒绝", p1);
							MsgSent.info("§2已发送请求", p);
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
