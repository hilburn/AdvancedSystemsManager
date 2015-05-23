package advancedsystemsmanager.network.message;

import advancedsystemsmanager.api.network.IPacketWriter;
import advancedsystemsmanager.gui.ContainerBase;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerMessage implements IBufferMessage, IMessageHandler<ContainerMessage, IMessage>
{
    protected IPacketWriter writer;
    protected ByteBuf buf;

    public ContainerMessage()
    {
    }

    public ContainerMessage(IPacketWriter writer)
    {
        this.writer = writer;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.buf = buf;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        //writer.writeNetworkComponent(buf);
    }

    @Override
    public IMessage onMessage(ContainerMessage message, MessageContext ctx)
    {
        EntityPlayer player;
        if (ctx.side == Side.CLIENT)
        {
            player = Minecraft.getMinecraft().thePlayer;
        } else
        {
            player = ctx.getServerHandler().playerEntity;
        }
        Container container = player.openContainer;
        if (container instanceof ContainerBase)
        {
            return onMessage(message, (ContainerBase)container, player);
        }
        return null;
    }

    public IMessage onMessage(ContainerMessage message, ContainerBase container, EntityPlayer player)
    {
        //container.getInterface().readData(message.buf, player);
        return null;
    }

    @Override
    public ByteBuf getBuffer()
    {
        return buf;
    }
}
