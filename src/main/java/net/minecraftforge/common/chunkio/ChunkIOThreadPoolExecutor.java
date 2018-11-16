/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.chunkio;

import cn.pfcraft.server.PFServer;
import net.minecraft.crash.CrashReportCategory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class ChunkIOThreadPoolExecutor extends ThreadPoolExecutor {

    public ChunkIOThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t)
    {
        if (t != null)
        {
            try
            {
                PFServer.LOGGER.error("Unhandled exception loading chunk:", t);
                QueuedChunk queuedChunk = ((ChunkIOProvider) r).getChunkInfo();
                PFServer.LOGGER.error(queuedChunk);
                PFServer.LOGGER.error(CrashReportCategory.getCoordinateInfo(queuedChunk.x << 4, 64, queuedChunk.z << 4));
            }
            catch (Throwable t2)
            {
                PFServer.LOGGER.error(t2);
            }
            finally
            {
                // Make absolutely sure that we do not leave any deadlock case
                r.notifyAll();
            }
        }
    }
}
