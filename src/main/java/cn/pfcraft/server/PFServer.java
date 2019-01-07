package cn.pfcraft.server;

import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PFServer {

	public static final String name = "PFServer";
	private static final String version = "1.1.1";
	private static final String native_verson = "v1_12_R1";
	public static final Logger LOGGER = LogManager.getLogger("PFServer");
	public static final String bukkit_version = "1.12.2-R0.1-SNAPSHOT";

	public static String getName(){
		return name;
	}

	public static String getVersion(){
		return version;
	}

    public static String getNativeVersion() {
        return native_verson;
    }

	public static String getBukkitVersion() {
		return bukkit_version;
	}

	public static boolean asyncCatch(String reason) {
		if (Thread.currentThread() != MinecraftServer.getServerInst().primaryThread) {
			LOGGER.warn("Try to asynchronously " + reason + ", caught!");
			return true;
		} else {
			return false;
		}
	}

	public static void bigWarning(String format, Object... data){
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		LOGGER.warn("****************************************");
		LOGGER.warn("* "+format, data);
		for (int i = 2; i < 8 && i < trace.length; i++)
		{
			LOGGER.warn("*  at {}{}", trace[i].toString(), i == 7 ? "..." : "");
		}
		LOGGER.warn("****************************************");
	}
}
