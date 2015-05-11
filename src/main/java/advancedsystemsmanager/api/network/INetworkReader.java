package advancedsystemsmanager.api.network;

import io.netty.buffer.ByteBuf;

public interface INetworkReader
{
    void readNetworkComponent(ByteBuf buf);
}
