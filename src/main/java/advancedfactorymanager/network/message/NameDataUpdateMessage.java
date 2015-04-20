package advancedfactorymanager.network.message;

import advancedfactorymanager.naming.BlockCoord;
import advancedfactorymanager.naming.NameRegistry;
import advancedfactorymanager.network.MessageHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class NameDataUpdateMessage implements IMessage, IMessageHandler<NameDataUpdateMessage, IMessage>
{
    public int dimId;
    public boolean remove;
    public BlockCoord blockCoord;

    public NameDataUpdateMessage()
    {
    }

    public NameDataUpdateMessage(int dim, BlockCoord coord)
    {
        this.dimId = dim;
        this.blockCoord = coord;
    }

    public NameDataUpdateMessage(int dim, BlockCoord coord, boolean remove)
    {
        this(dim, coord);
        this.remove = remove;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        dimId = buf.readInt();
        remove = buf.readBoolean();
        blockCoord = new BlockCoord(buf.readInt(), buf.readInt(), buf.readInt(), ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(dimId);
        buf.writeBoolean(remove);
        buf.writeInt(blockCoord.x);
        buf.writeInt(blockCoord.y);
        buf.writeInt(blockCoord.z);
        ByteBufUtils.writeUTF8String(buf, blockCoord.name);
    }

    @Override
    public IMessage onMessage(NameDataUpdateMessage message, MessageContext ctx)
    {
        if (message.remove) NameRegistry.removeName(message);
        else NameRegistry.saveName(message);
        if (ctx.side == Side.SERVER)
        {
            MessageHandler.INSTANCE.sendToAll(message);
        }
        return null;
    }
}
