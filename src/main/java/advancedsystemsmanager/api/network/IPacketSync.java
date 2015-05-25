package advancedsystemsmanager.api.network;

public interface IPacketSync extends IPacketReader
{
    void onUpdate();

    void setId(int id);
}
