package advancedsystemsmanager.api.network;

import advancedsystemsmanager.network.ASMPacket;

public interface IPacketReader
{
    void readData(ASMPacket packet);
}
