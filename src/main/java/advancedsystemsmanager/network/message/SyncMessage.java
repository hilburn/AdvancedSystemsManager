package advancedsystemsmanager.network.message;

import advancedsystemsmanager.api.network.INetworkSync;
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

public class SyncMessage implements IMessage, IMessageHandler<SyncMessage, IMessage>
{
    public INetworkSync sync;
    public ByteBuf buf;

    public SyncMessage(INetworkSync sync)
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
        sync.writeNetworkComponent(buf);
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
                ((ContainerBase)container).updateClient(message);
            }
        }
        else
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
}
