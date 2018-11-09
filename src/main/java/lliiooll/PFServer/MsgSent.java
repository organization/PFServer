package lliiooll.PFServer;

import org.bukkit.entity.Player;

public class MsgSent {

	private static String info = "¡ì2[¡ìbPFServerR¡ì2] ";
	private static String error = "¡ì2[¡ìcPFServerR¡ì2] ";
	private static String warn = "¡ì2[¡ìePFServerR¡ì2] ";
	
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