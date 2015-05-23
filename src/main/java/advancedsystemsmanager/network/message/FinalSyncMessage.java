package advancedsystemsmanager.network.message;

import advancedsystemsmanager.api.network.IPacketWriter;
import advancedsystemsmanager.api.tileentities.ITileEntityInterface;
import advancedsystemsmanager.gui.ContainerBase;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


public class FinalSyncMessage extends SyncMessage
{
    private int x, y, z, dim;

    public FinalSyncMessage()
    {
    }

    public FinalSyncMessage(TileEntity tileEntity, IPacketWriter writer)
    {
        super(writer);
        this.x = tileEntity.xCoord;
        this.y = tileEntity.yCoord;
        this.z = tileEntity.zCoord;
        this.dim = tileEntity.getWorldObj().provider.dimensionId;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(dim);
        buf.writeInt(x);
        buf.writeByte(y);
        buf.writeInt(z);
        super.toBytes(buf);
    }

    @Override
    public IMessage onMessage(SyncMessage message, MessageContext ctx)
    {
        int dim = message.buf.readByte();
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dim);
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        TileEntity tileEntity = world.getTileEntity(message.buf.readInt(), message.buf.readUnsignedByte(), message.buf.readInt());
        if (tileEntity instanceof ITileEntityInterface)
        {
            for (Object players : MinecraftServer.getServer().getConfigurationManager().playerEntityList)
            {
                if (players instanceof EntityPlayerMP)
                {
                    Container container = ((EntityPlayerMP)players).openContainer;
                    if (container instanceof ContainerBase && ((ContainerBase)container).getTileEntity() == tileEntity)
                    {
//                        ((ContainerBase)container).updateServer(getSyncMessage(), player);
                        return null;
                    }
                }
            }
            //((ITileEntityInterface)tileEntity).readData(message.buf, player);
        }
        return null;
    }

    public SyncMessage getSyncMessage()
    {
        return new SyncMessage(buf.discardReadBytes());
    }
}
