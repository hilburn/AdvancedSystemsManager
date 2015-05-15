package advancedsystemsmanager.api.network;

import io.netty.buffer.ByteBuf;

public interface INetworkReader
{
    void readData(ByteBuf buf);
}
