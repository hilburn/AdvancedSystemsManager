package advancedsystemsmanager.api.network;

public interface INetworkSync extends INetworkReader, INetworkWriter
{
    boolean needsSync();

    void setSynced();
}
