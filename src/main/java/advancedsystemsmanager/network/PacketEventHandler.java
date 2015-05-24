package advancedsystemsmanager.network;

import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.gui.ContainerBase;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;

public class PacketEventHandler
{

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event)
    {
        handlePacket(event.packet.payload(), FMLClientHandler.instance().getClient().thePlayer);
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event)
    {
        handlePacket(event.packet.payload(), ((NetHandlerPlayServer)event.handler).playerEntity);
    }

    public void handlePacket(ByteBuf buffer, EntityPlayer player)
    {
        ASMPacket packet = new ASMPacket(buffer);
        if (player instanceof EntityPlayerMP) packet.setPlayer((EntityPlayerMP)player);
        boolean useContainer = packet.readBoolean();

        if (useContainer)
        {
            int containerId = packet.readByte();
            Container container = player.openContainer;

            if (container != null && container.windowId == containerId && container instanceof ContainerBase)
            {
                if (player instanceof EntityPlayerMP)
                {
                    ((ContainerBase)container).updateServer(packet, (EntityPlayerMP)player);
                } else
                {
                    ((ContainerBase)container).updateClient(packet, player);
                }

            }
        } else
        {
            int x = packet.readInt();
            int y = packet.readUnsignedByte();
            int z = packet.readInt();

            TileEntity te = player.worldObj.getTileEntity(x, y, z);
            if (te != null && te instanceof IPacketBlock)
            {
                int id = packet.readByte();
                ((IPacketBlock)te).readData(packet, id);
            }
        }
    }

}
