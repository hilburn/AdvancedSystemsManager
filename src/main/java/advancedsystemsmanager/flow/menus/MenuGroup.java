package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.interfaces.ContainerManager;
import advancedsystemsmanager.interfaces.GuiManager;
import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;


public class MenuGroup extends Menu
{
    public MenuGroup(FlowComponent parent)
    {
        super(parent);
    }

    public static final int MENU_WIDTH = 120;
    public static final int TEXT_MARGIN_X = 5;
    public static final int TEXT_Y = 5;

    @Override
    public String getName()
    {
        return Localization.GROUP_MENU.toString();
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        gui.drawSplitString(Localization.GROUP_INFO.toString(), TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X * 2, 1F, 0x404040);
    }

    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean inBounds(int mX, int mY)
    {
        return (CollisionHelper.inBounds(0, 0, MENU_WIDTH, FlowComponent.getMenuOpenSize(), mX, mY));
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        if (inBounds(mX, mY)) getParent().getManager().setSelectedComponent(getParent());
    }

    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {
        if (isMenuOpen && inBounds(mX, mY))
        {
            for (FlowComponent component : getParent().getManager().getFlowItems())
            {
                if (component.isBeingMoved())
                {
                    if (!component.equals(getParent()))
                    {
                        DataWriter dw = getWriterForServerComponentPacket();
                        dw.writeComponentId(getParent().getManager(), component.getId());
                        dw.writeBoolean(GuiScreen.isShiftKeyDown());
                        PacketHandler.sendDataToServer(dw);
                    }

                    break;
                }
            }
        }
    }

    @Override
    public void writeData(DataWriter dw)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void readData(DataReader dr)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void copyFrom(Menu menu)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void refreshData(ContainerManager container, Menu newData)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        if (!getParent().getManager().getWorldObj().isRemote)
        {
            int id = dr.readComponentId();
            FlowComponent component = getParent().getManager().getFlowItems().get(id);
            boolean moveCluster = dr.readBoolean();

            moveComponents(component, getParent(), moveCluster);
        }
    }

    public static void moveComponents(FlowComponent component, FlowComponent parent, boolean moveCluster)
    {

        if (moveCluster)
        {
            List<FlowComponent> cluster = new ArrayList<FlowComponent>();
            findCluster(cluster, component, parent);
            for (FlowComponent flowComponent : cluster)
            {
                flowComponent.setParent(parent);
                if (parent != null)
                {
                    for (int i = 0; i < flowComponent.getConnectionSet().getConnections().length; i++)
                    {
                        Connection connection = flowComponent.getConnection(i);
                        if (connection != null && connection.getComponentId() == parent.getId())
                        {
                            flowComponent.removeConnection(i); //remove all connections to the component we're moving stuff into
                        }
                    }
                }
            }
        } else if (!component.equals(parent))
        {
            component.setParent(parent);
            component.removeAllConnections();
        }
    }

    public static void findCluster(List<FlowComponent> components, FlowComponent component, FlowComponent parent)
    {
        if (!components.contains(component) && !component.equals(parent))
        {
            components.add(component);

            for (int i = 0; i < component.getConnectionSet().getConnections().length; i++)
            {
                Connection connection = component.getConnection(i);
                if (connection != null)
                {
                    findCluster(components, component.getManager().getFlowItems().get(connection.getComponentId()), parent);
                }
            }
        }
    }
}
