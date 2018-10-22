package mgazul.PFServer;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLLog;

public class CatServer {
	private static final String version = "0.0.1";
	private static final String native_verson = "v1_12_R1";

	public static String getVersion(){
		return version;
	}

    public static String getNativeVersion() {
        return native_verson;
    }

	public static boolean asyncCatch(String reason) {
		if (Thread.currentThread() != MinecraftServer.getServerInst().primaryThread) {
			FMLLog.bigWarning("Try to asynchronously " + reason + ", caught!");
			return true;
		} else {
			return false;
		}
	}
}
