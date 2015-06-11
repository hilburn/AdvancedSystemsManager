package advancedsystemsmanager.network;

import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.api.tileentities.ITileInterfaceProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.gui.ContainerBase;
import advancedsystemsmanager.gui.ContainerManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.tileentity.TileEntity;


public class PacketHandler
{
    public static final double BLOCK_UPDATE_RANGE = 128;
    public static final double BLOCK_UPDATE_SQ = BLOCK_UPDATE_RANGE * BLOCK_UPDATE_RANGE;
    public static final int SYNC_ALL = 0;
    public static final byte SETTING_MESSAGE = 1;
    public static final int SYNC_COMPONENT = 2;
    public static final int NEW_VARIABLE = 4;
    public static final int BUTTON_CLICK = 5;
    public static final int SPECIAL_DATA = 42;

    public static final int CONTAINER = 0;
    public static final int BLOCK = 1;
    public static final int COMMAND = 2;


    public static void sendDataToServer(ASMPacket dw)
    {
        dw.sendServerPacket();
    }

    public static void sendAllData(Container container, ICrafting crafting, ITileInterfaceProvider te)
    {
        ASMPacket dw = new ASMPacket();

        dw.writeByte(CONTAINER);
        dw.writeByte(container.windowId);
        te.writeData(dw);

        sendDataToPlayer(crafting, dw);
    }

    public static void sendDataToPlayer(ICrafting crafting, ASMPacket dw)
    {
        if (crafting instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP)crafting;

            dw.sendPlayerPacket(player);
        }
    }

    public static ASMPacket getWriterForUpdate(Container container)
    {
        ASMPacket dw = new ASMPacket();

        dw.writeByte(CONTAINER);
        dw.writeByte(container.windowId);
        dw.writeBoolean(true); //updated data

        return dw;
    }

    public static ASMPacket getCommandPacket()
    {
        ASMPacket packet = new ASMPacket();
        packet.writeByte(COMMAND);
        return packet;
    }



   /* public static void readBlockPacket(DataReader data) {
        int x = data.readData(DataBitHelper.WORLD_COORDINATE);
        int y = data.readData(DataBitHelper.WORLD_COORDINATE);
        int z = data.readData(DataBitHelper.WORLD_COORDINATE);

        World world = Minecraft.getMinecraft().theWorld;
        if (world.getBlockId(x, y, z) == Blocks.blockCable.blockID) {
            Blocks.blockCable.update(world, x, y, z);
        }
    }*/

    @SideOnly(Side.CLIENT)
    public static ASMPacket getBaseContainerPacket()
    {
        Container container = Minecraft.getMinecraft().thePlayer.openContainer;
        if (container != null)
        {
            return getContainerPacket(container);
        } else
        {
            return null;
        }
    }

    public static ASMPacket getContainerPacket(Container container)
    {
        ASMPacket dw = new ASMPacket();
        dw.writeByte(CONTAINER);
        dw.writeByte(container.windowId);
        return dw;
    }

    public static void sendDataToListeningClients(ContainerBase container, ASMPacket dw)
    {
        dw.sendPlayerPackets(true, container);
    }

    public static ASMPacket getComponentPacket(FlowComponent component)
    {
        ASMPacket dw = PacketHandler.getServerPacket();
        createComponentPacket(dw, component);
        return dw;
    }

    @SideOnly(Side.CLIENT)
    public static ASMPacket getServerPacket()
    {
        return getBaseContainerPacket();
    }

    private static void createComponentPacket(ASMPacket dw, FlowComponent component)
    {
        dw.writeByte(SYNC_COMPONENT); //this is a packet for a specific FlowComponent
        dw.writeVarIntToBuffer(component.getId());
    }

    public static ASMPacket getWriterForClientComponentPacket(ContainerManager container, FlowComponent component, Menu menu)
    {
        ASMPacket dw = PacketHandler.getContainerPacket(container);
        createComponentPacket(dw, component);
        return dw;
    }


    public static void writeAllComponentData(ASMPacket dw, FlowComponent flowComponent)
    {
//        dw.writeData(flowComponent.getX(), DataBitHelper.FLOW_CONTROL_X);
//        dw.writeData(flowComponent.getY(), DataBitHelper.FLOW_CONTROL_Y);
//        dw.writeData(flowComponent.getType().getId(), DataBitHelper.FLOW_CONTROL_TYPE_ID);
//        dw.writeComponentId(flowComponent.manager, flowComponent.getId());
//        dw.writeString(flowComponent.getComponentName(), DataBitHelper.NAME_LENGTH);
//        if (flowComponent.getParent() != null)
//        {
//            dw.writeBoolean(true);
//            dw.writeComponentId(flowComponent.getManager(), flowComponent.getParent().getId());
//        } else
//        {
//            dw.writeBoolean(false);
//        }
//        for (Menu menu : flowComponent.getMenus())
//        {
//            //menu.writeData(dw);
//        }
//
//        for (int i = 0; i < flowComponent.getConnectionSet().getConnections().length; i++)
//        {
//            Connection connection = flowComponent.getConnection(i);
//            dw.writeBoolean(connection != null);
//            if (connection != null)
//            {
//                dw.writeComponentId(flowComponent.getManager(), connection.getComponentId());
//                dw.writeData(connection.getConnectionId(), DataBitHelper.CONNECTION_ID);
//
//                dw.writeData(connection.getNodes().size(), DataBitHelper.NODE_ID);
//                for (Point point : connection.getNodes())
//                {
//                    dw.writeData(point.getX(), DataBitHelper.FLOW_CONTROL_X);
//                    dw.writeData(point.getY(), DataBitHelper.FLOW_CONTROL_Y);
//                }
//            }
//        }
//
//        flowComponent.getManager().updateVariables();
    }

    public static ASMPacket constructBlockPacket(TileEntity te, IPacketBlock block, int id)
    {
        ASMPacket dw = new ASMPacket(20);
        dw.writeByte(BLOCK);
        dw.writeInt(te.xCoord);
        dw.writeByte(te.yCoord);
        dw.writeInt(te.zCoord);
        dw.writeByte(id);

        block.writeData(dw, id);
        return dw;
    }

    public static void sendBlockPacket(IPacketBlock block, EntityPlayer player, int id)
    {
        if (block instanceof TileEntity)
        {
            TileEntity te = (TileEntity)block;
            ASMPacket packet = constructBlockPacket(te, block, id);
            boolean onServer = player == null || !player.worldObj.isRemote;

            if (!onServer)
            {
                packet.sendServerPacket();
            } else if (player != null)
            {
                packet.sendPlayerPacket((EntityPlayerMP)player);
            } else
            {
                packet.sendPlayerPackets(te.xCoord + 0.5, te.yCoord + 0.5, te.zCoord + 0.5, BLOCK_UPDATE_RANGE, te.getWorldObj().provider.dimensionId);
            }
        }
    }

    public static void sendButtonPacket(int index, IManagerButton button)
    {
        ASMPacket packet = PacketHandler.getServerPacket();
        packet.writeByte(BUTTON_CLICK);
        packet.writeByte(index);
        if (button.writeData(packet))
            PacketHandler.sendDataToServer(packet);
    }

    public static void sendVariablePacket(int colour)
    {
        ASMPacket packet = PacketHandler.getServerPacket();
        packet.writeByte(NEW_VARIABLE);
        packet.writeMedium(colour);
        packet.sendServerPacket();
    }
}
