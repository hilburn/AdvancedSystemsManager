package advancedsystemsmanager.api.network;

import advancedsystemsmanager.network.ASMPacket;

public interface IPacketWriter
{
    void writeData(ASMPacket packet);
}
