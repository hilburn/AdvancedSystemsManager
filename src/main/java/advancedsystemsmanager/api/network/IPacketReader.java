package advancedsystemsmanager.api.network;

import advancedsystemsmanager.network.ASMPacket;

public interface IPacketReader
{
    boolean readData(ASMPacket packet);
}
