package advancedsystemsmanager.flow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public class Connection
{
    public static final String NBT_CONNECTION_POS = "CP";
    public static final String NBT_CONNECTION_TARGET_COMPONENT = "CT";
    public static final String NBT_CONNECTION_TARGET_CONNECTION = "CC";
    public static final String NBT_NODES = "N";
    public static final String NBT_POS_X = "X";
    public static final String NBT_POS_Y = "Y";

    public int componentId;
    public int connectionId;
    public List<Point> nodes;
    public int selected = -1;

    public Connection(int componentId, int connectionId)
    {
        this.componentId = componentId;
        this.connectionId = connectionId;
        nodes = new ArrayList<Point>();
    }

    public int getComponentId()
    {
        return componentId;
    }

    public void setComponentId(int componentId)
    {
        this.componentId = componentId;
    }

    public int getConnectionId()
    {
        return connectionId;
    }

    public void setConnectionId(int connectionId)
    {
        this.connectionId = connectionId;
    }

    public Connection copy()
    {
        Connection copy = new Connection(this.componentId, this.connectionId);
        for (Point node : nodes)
        {
            copy.nodes.add(node.copy());
        }
        return copy;
    }

    public void addAndSelectNode(int mX, int mY, int id)
    {
        nodes.add(selected = id, new Point(mX, mY));
    }

    @SideOnly(Side.CLIENT)
    public void update(int mX, int mY)
    {
        if (selected != -1)
        {
            Point selected = getSelectedNode();
            selected.setX(mX);
            selected.setY(mY);

            if (GuiScreen.isShiftKeyDown())
            {
                selected.adjustToGrid(10);
            }
        }
    }

    public void adjustAllToGrid(int grid)
    {
        for (Point node : nodes)
        {
            node.adjustToGrid(grid);
        }
    }

    public List<Point> getNodes()
    {
        return nodes;
    }

    public Point getSelectedNode()
    {
        return selected == -1? null : nodes.get(selected);
    }

    public int getSelected()
    {
        return selected;
    }

    public void setSelected(int selected)
    {
        this.selected = selected;
    }

    public void writeToNBT(NBTTagCompound tagCompound, int index)
    {
        tagCompound.setByte(NBT_CONNECTION_POS, (byte)index);
        tagCompound.setInteger(NBT_CONNECTION_TARGET_COMPONENT, getComponentId());
        tagCompound.setByte(NBT_CONNECTION_TARGET_CONNECTION, (byte)getConnectionId());


        NBTTagList nodes = new NBTTagList();
        for (Point point : this.nodes)
        {
            NBTTagCompound nodeTag = new NBTTagCompound();

            nodeTag.setShort(NBT_POS_X, (short)point.getX());
            nodeTag.setShort(NBT_POS_Y, (short)point.getY());

            nodes.appendTag(nodeTag);
        }

        tagCompound.setTag(NBT_NODES, nodes);
    }

    public static void readFromNBT(Connection[] connections, NBTTagCompound tagCompound)
    {
        int componentId = tagCompound.getInteger(NBT_CONNECTION_TARGET_COMPONENT);
        Connection connection = new Connection(componentId, tagCompound.getByte(NBT_CONNECTION_TARGET_CONNECTION));

        if (tagCompound.hasKey(NBT_NODES))
        {
            connection.getNodes().clear();
            NBTTagList nodes = tagCompound.getTagList(NBT_NODES, 10);
            for (int j = 0; j < nodes.tagCount(); j++)
            {
                NBTTagCompound nodeTag = nodes.getCompoundTagAt(j);

                connection.getNodes().add(new Point(nodeTag.getShort(NBT_POS_X), nodeTag.getShort(NBT_POS_Y)));
            }
        }

        connections[(int)tagCompound.getByte(NBT_CONNECTION_POS)] = connection;
    }
}
