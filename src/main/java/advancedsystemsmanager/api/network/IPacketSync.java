package advancedsystemsmanager.api.network;

public interface IPacketSync extends IPacketReader, IPacketWriter
{
    void onUpdate();

    void setId(int id);
}
