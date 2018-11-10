package lliiooll.PFServer;

import org.bukkit.entity.Player;

public class MsgSent {

	private static String info = "§2[§bPFServerR§2] ";
	private static String error = "§2[§cPFServerR§2] ";
	private static String warn = "§2[§ePFServerR§2] ";
	
	public static void info(String msg,Player p) {
		p.sendMessage(info + msg);
	}
	
	public static void warn(String msg,Player p) {
		p.sendMessage(warn + msg);
	}
	
	public static void error(String msg,Player p) {
		p.sendMessage(error + msg);
	}
}