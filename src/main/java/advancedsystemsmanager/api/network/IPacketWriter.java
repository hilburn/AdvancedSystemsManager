package advancedsystemsmanager.api.network;

import advancedsystemsmanager.network.ASMPacket;

public interface IPacketWriter
{
    boolean writeData(ASMPacket packet);
}
