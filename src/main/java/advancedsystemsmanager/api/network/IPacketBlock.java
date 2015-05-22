package advancedsystemsmanager.api.network;

import advancedsystemsmanager.network.ASMPacket;

public interface IPacketBlock
{
    void writeData(ASMPacket packet, int id);

    void readData(ASMPacket packet, int id);
}
