package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Names;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class MenuGroup extends Menu
{
    public static final int MENU_WIDTH = 120;
    public static final int TEXT_MARGIN_X = 5;
    public static final int TEXT_Y = 5;

    public MenuGroup(FlowComponent parent)
    {
        super(parent);
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
                        if (connection != null && (connection.getInputId() == parent.getId() || connection.getOutputId() == parent.getId()))
                        {
                            flowComponent.removeConnection(i);
                        }
                    }
                }
            }
        } else if (!component.equals(parent))
        {
            component.setParent(parent);
            component.deleteConnections();
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
                    findCluster(components, component.getManager().getFlowItem(connection.getOutputId() == component.getId() ? connection.getInputId() : connection.getOutputId()), parent);
                }
            }
        }
    }

    @Override
    public String getName()
    {
        return Names.GROUP_MENU;
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        gui.drawSplitString(Names.GROUP_INFO, TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X * 2, 1F, 0x404040);
    }

    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        if (inBounds(mX, mY)) getParent().getManager().setSelectedGroup(getParent());
    }

    public boolean inBounds(int mX, int mY)
    {
        return (CollisionHelper.inBounds(0, 0, MENU_WIDTH, FlowComponent.getMenuOpenSize(), mX, mY));
    }

    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {
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
                        component.sendNewParentData(getParent(), GuiScreen.isShiftKeyDown());
                        moveComponents(component, getParent(), GuiScreen.isShiftKeyDown());
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void copyFrom(Menu menu)
    {
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
    }

}
