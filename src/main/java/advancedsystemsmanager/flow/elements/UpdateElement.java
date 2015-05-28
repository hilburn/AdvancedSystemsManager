package advancedsystemsmanager.flow.elements;

import advancedsystemsmanager.api.network.IPacketSync;
import advancedsystemsmanager.api.network.IPacketProvider;
import advancedsystemsmanager.api.network.IPacketWriter;
import advancedsystemsmanager.network.ASMPacket;

public abstract class UpdateElement implements IPacketSync, IPacketWriter
{
    protected IPacketProvider packetProvider;
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
        sendSyncPacket();
    }

    public void sendSyncPacket()
    {
        ASMPacket packet = packetProvider.getSyncPacket();
        packet.writeByte(id);
        if (writeData(packet))
            packetProvider.sendPacketToServer(packet);
    }
}
