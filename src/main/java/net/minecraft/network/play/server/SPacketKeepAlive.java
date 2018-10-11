package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

public class SPacketKeepAlive implements Packet<INetHandlerPlayClient>
{
    private long id;

    public SPacketKeepAlive()
    {
    }

    public SPacketKeepAlive(long idIn)
    {
        this.id = idIn;
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleKeepAlive(this);
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.id = buf.readLong();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeLong(this.id);
    }

    @SideOnly(Side.CLIENT)
    public long getId()
    {
        return this.id;
    }
}