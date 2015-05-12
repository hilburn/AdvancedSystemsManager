package advancedsystemsmanager.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public interface IBufferMessage extends IMessage
{
    ByteBuf getBuffer();
}
