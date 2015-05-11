package advancedsystemsmanager.api.network;

import io.netty.buffer.ByteBuf;

public interface INetworkWriter
{
    void writeNetworkComponent(ByteBuf buf);
}
