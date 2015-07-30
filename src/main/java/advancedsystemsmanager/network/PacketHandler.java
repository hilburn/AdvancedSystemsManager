package advancedsystemsmanager.network;

import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.api.tileentities.ITileInterfaceProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.containers.ContainerBase;
import advancedsystemsmanager.containers.ContainerManager;
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
    public static final int SYNC_ALL = 0;
    public static final int SETTING_MESSAGE = 1;
    public static final int SYNC_COMPONENT = 2;
    public static final int NEW_VARIABLE = 4;
    public static final int BUTTON_CLICK = 5;
    public static final int SPECIAL_DATA = 42;

    public static final int CONTAINER = 0;
    public static final int BLOCK = 1;
    public static final int COMMAND = 2;

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

    @SideOnly(Side.CLIENT)
    public static ASMPacket getBaseContainerPacket()
    {
        Container container = Minecraft.getMinecraft().thePlayer.openContainer;
        if (container != null) return getContainerPacket(container);
        return null;
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
        Container container = Minecraft.getMinecraft().thePlayer.openContainer;
        return getContainerPacket(container);
    }

    private static void createComponentPacket(ASMPacket dw, FlowComponent component)
    {
        dw.writeByte(SYNC_COMPONENT);
        dw.writeVarIntToBuffer(component.getId());
    }

    public static ASMPacket getWriterForClientComponentPacket(ContainerManager container, FlowComponent component, Menu menu)
    {
        ASMPacket dw = PacketHandler.getContainerPacket(container);
        createComponentPacket(dw, component);
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

    public static void sendButtonPacket(int index, IManagerButton button)
    {
        ASMPacket packet = PacketHandler.getServerPacket();
        packet.writeByte(BUTTON_CLICK);
        packet.writeByte(index);
        if (button.writeData(packet))
            PacketHandler.sendDataToServer(packet);
    }

    public static void sendDataToServer(ASMPacket dw)
    {
        dw.sendServerPacket();
    }

    public static void sendVariablePacket(int colour)
    {
        ASMPacket packet = PacketHandler.getServerPacket();
        packet.writeByte(NEW_VARIABLE);
        packet.writeMedium(colour);
        packet.sendServerPacket();
    }
}
