package advancedsystemsmanager.network.message;

import advancedsystemsmanager.api.network.INetworkWriter;
import advancedsystemsmanager.gui.ContainerBase;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

public class SyncMessage implements IBufferMessage, IMessageHandler<SyncMessage, IMessage>
{
    public INetworkWriter sync;
    public ByteBuf buf;

    public SyncMessage()
    {
    }

    public SyncMessage(ByteBuf buf)
    {
        this.buf = buf;
    }

    public SyncMessage(INetworkWriter sync)
    {
        this.sync = sync;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.buf = buf;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        if (this.buf == null)
        {
            buf.writeByte(getID());
            writeExtraData(buf);
            sync.writeNetworkComponent(buf);
        } else
        {
            buf.writeBytes(this.buf.copy());
        }
    }

    public int getID()
    {
        return 3;
    }

    public void writeExtraData(ByteBuf buf)
    {
    }

    @Override
    public IMessage onMessage(SyncMessage message, MessageContext ctx)
    {
        if (ctx.side == Side.CLIENT)
        {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            Container container = player.openContainer;
            if (container instanceof ContainerBase)
            {
                ((ContainerBase)container).updateClient(message, player);
            }
        } else
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            Container container = player.openContainer;
            if (container instanceof ContainerBase)
            {
                ((ContainerBase)container).updateServer(message, player);
            }
        }
        return null;
    }

    @Override
    public ByteBuf getBuffer()
    {
        return buf.copy();
    }
}
