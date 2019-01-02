package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class CPacketResourcePackStatus implements Packet<INetHandlerPlayServer>
{
    public Action action;

    public CPacketResourcePackStatus()
    {
    }

    public CPacketResourcePackStatus(Action p_i47156_1_)
    {
        this.action = p_i47156_1_;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.action = (Action)buf.readEnumValue(Action.class);
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeEnumValue(this.action);
    }

    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.handleResourcePackStatus(this);
    }

    public static enum Action
    {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED
    }
}