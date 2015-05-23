package advancedsystemsmanager.flow.elements;

import advancedsystemsmanager.api.network.IPacketSync;
import advancedsystemsmanager.api.network.IPacketProvider;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;

public abstract class UpdateElement implements IPacketSync
{
    private IPacketProvider packetProvider;
    private int id;

    protected UpdateElement(IPacketProvider packetProvider)
    {
        this.packetProvider = packetProvider;
        packetProvider.registerSyncable(this);
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void onUpdate()
    {
        ASMPacket packet = packetProvider.getSyncPacket();
        packet.writeByte(id);
        writeData(packet);
        PacketHandler.sendDataToServer(packet);
    }

    public abstract void writeData(ASMPacket packet);

    public abstract void readData(ASMPacket packet);
}
