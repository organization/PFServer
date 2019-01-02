package net.minecraft.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Util
{
    @SideOnly(Side.CLIENT)
    public static Util.EnumOS getOSType()
    {
        String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        if (s.contains("win"))
        {
            return EnumOS.WINDOWS;
        }
        else if (s.contains("mac"))
        {
            return EnumOS.OSX;
        }
        else if (s.contains("solaris"))
        {
            return EnumOS.SOLARIS;
        }
        else if (s.contains("sunos"))
        {
            return EnumOS.SOLARIS;
        }
        else if (s.contains("linux"))
        {
            return EnumOS.LINUX;
        }
        else
        {
            return s.contains("unix") ? EnumOS.LINUX : EnumOS.UNKNOWN;
        }
    }

    @Nullable
    public static <V> V runTask(FutureTask<V> task, Logger logger)
    {
        try
        {
            task.run();
            return task.get();
        }
        catch (ExecutionException | InterruptedException executionexception)
        {
            logger.fatal("Error executing task", (Throwable)executionexception);
        }

        return (V)null;
    }

    public static <T> T getLastElement(List<T> list)
    {
        return list.get(list.size() - 1);
    }

    @SideOnly(Side.CLIENT)
    public static enum EnumOS
    {
        LINUX,
        SOLARIS,
        WINDOWS,
        OSX,
        UNKNOWN
    }
}