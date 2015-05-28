package advancedsystemsmanager.flow;

import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public class Connection
{
    public static final String NBT_CONNECTION_INPUT_CONNECTION = "ICo";
    public static final String NBT_CONNECTION_OUTPUT_COMPONENT = "OC";
    public static final String NBT_CONNECTION_OUTPUT_CONNECTION = "OCo";
    public static final String NBT_NODES = "N";
    public static final String NBT_POS_X = "X";
    public static final String NBT_POS_Y = "Y";

    public int inputId;
    public int inputConnection;
    public int outputId;
    public int outputConnection;
    public List<Point> nodes;
    public int selected = -1;

    public Connection(int componentId, int connectionId)
    {
        this(componentId, connectionId, -1, -1);
    }

    public Connection(int newInput, int newOutput, Connection connection)
    {
        this(newInput, connection.getInputConnection(), newOutput, connection.getOutputConnection());
    }

    public Connection(int inputId, int inputConnection, int outputId, int outputConnection)
    {
        this.inputId = inputId;
        this.inputConnection = inputConnection;
        this.outputId = outputId;
        this.outputConnection = outputConnection;
        nodes = new ArrayList<Point>();
    }

    public int getInputId()
    {
        return inputId;
    }

    public void setInputId(int componentId)
    {
        this.outputId = this.inputId;
        this.inputId = componentId;
    }

    public int getInputConnection()
    {
        return inputConnection;
    }

    public void setInputConnection(int connectionId)
    {
        this.outputConnection = this.inputConnection;
        this.inputConnection = connectionId;
    }

    public void setOutputId(int componentId)
    {
        this.outputId = componentId;
    }

    public void setOutputConnection(int connectionId)
    {
        this.outputConnection = connectionId;
    }

    public void setConnection(TileEntityManager manager)
    {
        FlowComponent a = manager.getFlowItem(inputId);
        FlowComponent b = manager.getFlowItem(outputId);
        if (a != null && !a.equals(b))
        {
            a.setConnection(inputConnection, this);
            if (b != null) b.setConnection(outputConnection, this);
        }
    }

    public void deleteConnection(TileEntityManager manager)
    {
        FlowComponent a = manager.getFlowItem(inputId);
        FlowComponent b = manager.getFlowItem(outputId);
        if (a != null) a.setConnection(inputConnection, null);
        if (b != null) b.setConnection(outputConnection, null);
    }

    public Connection copy()
    {
        Connection copy = new Connection(inputId, inputConnection, outputId, outputConnection);
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
        return selected == -1 ? null : nodes.get(selected);
    }

    public int getSelected()
    {
        return selected;
    }

    public void setSelected(int selected)
    {
        this.selected = selected;
    }

    public void writeToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setByte(NBT_CONNECTION_INPUT_CONNECTION, (byte)getInputConnection());
        tagCompound.setInteger(NBT_CONNECTION_OUTPUT_COMPONENT, getOutputId());
        tagCompound.setByte(NBT_CONNECTION_OUTPUT_CONNECTION, (byte)getOutputConnection());


        if (this.nodes.size() > 0)
        {
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
    }

    public static void readFromNBT(Connection[] connections, NBTTagCompound tagCompound, int inputId)
    {
        Connection connection = new Connection(inputId, tagCompound.getByte(NBT_CONNECTION_INPUT_CONNECTION),
                tagCompound.getInteger(NBT_CONNECTION_OUTPUT_COMPONENT), tagCompound.getByte(NBT_CONNECTION_OUTPUT_CONNECTION));
        if (connection.inputId != connection.outputId)
        {
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
            connections[connection.getInputConnection()] = connection;
        }
    }

    public int getOutputId()
    {
        return outputId;
    }

    public int getOutputConnection()
    {
        return outputConnection;
    }
}
