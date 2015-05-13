package advancedsystemsmanager.network.message;

import advancedsystemsmanager.api.network.INetworkSync;
import advancedsystemsmanager.api.network.INetworkWriter;
import advancedsystemsmanager.api.tileentities.ITileEntityInterface;
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
import net.minecraft.tileentity.TileEntity;

public class SyncMessage implements IBufferMessage, IMessageHandler<SyncMessage, IMessage>
{
    private TileEntity tile;
    public INetworkWriter sync;
    public ByteBuf buf;

    public SyncMessage()
    {
    }

    public SyncMessage(INetworkWriter sync)
    {
        this.sync = sync;
    }

    public SyncMessage(TileEntity te, INetworkWriter sync)
    {
        this(sync);
        this.tile = te;
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
            if (tile != null)
            {
                buf.writeBoolean(true);
                buf.writeInt(tile.xCoord);
                buf.writeInt(tile.yCoord);
                buf.writeInt(tile.zCoord);
            }
            else
            {
                buf.writeBoolean(false);
            }
            buf.writeByte(getID());
            writeExtraData(buf);
            sync.writeNetworkComponent(buf);
        }else
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
            if (message.buf.readBoolean()){}
                //for (int i = 0; i< 3; i++) message.buf.readInt();
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            Container container = player.openContainer;
            if (container instanceof ContainerBase)
            {
                ((ContainerBase)container).updateClient(message, player);
            }
        }
        else
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (message.buf.readBoolean())
            {
                TileEntity tileEntity = player.worldObj.getTileEntity(message.buf.readInt(), message.buf.readInt(), message.buf.readInt());
                if (tileEntity instanceof ITileEntityInterface)
                    ((ITileEntityInterface)tileEntity).readData(message.buf, player);
            }else
            {
                Container container = player.openContainer;
                if (container instanceof ContainerBase)
                {
                    ((ContainerBase)container).updateServer(message, player);
                }
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
