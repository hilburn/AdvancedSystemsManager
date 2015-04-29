package advancedsystemsmanager.flow.elements;


import advancedsystemsmanager.api.gui.IContainerSelection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.gui.GuiManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class Variable implements IContainerSelection<GuiManager>
{
    public int id;
    public FlowComponent declaration;
    public List<Integer> containers;
    public boolean executed;

    public Variable(int id)
    {
        this.id = id;
        containers = new ArrayList<Integer>();
    }

    public boolean isValid()
    {
        return declaration != null;
    }

    @Override
    public int getId()
    {
        return id;
    }

    public static final int VARIABLE_SRC_X = 32;
    public static final int VARIABLE_SRC_Y = 130;
    public static final int VARIABLE_SIZE = 14;

    @Override
    public void draw(GuiManager gui, int x, int y)
    {
        VariableColor.values()[id].applyColor();
        gui.drawTexture(x + 1, y + 1, VARIABLE_SRC_X, VARIABLE_SRC_Y, VARIABLE_SIZE, VARIABLE_SIZE);
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    @Override
    public String getDescription(GuiManager gui)
    {
        VariableColor color = VariableColor.values()[id];

        return color.getTextColor().toString() + getNameFromColor(color);
    }

    @Override
    public String getName(GuiManager gui)
    {
        return getNameFromColor(VariableColor.values()[id]);
    }

    @Override
    public boolean isVariable()
    {
        return true;
    }

    public String getNameFromColor(VariableColor color)
    {
        if (getDeclaration() == null || getDeclaration().getComponentName() == null)
        {
            return StatCollector.translateToLocal(color.toString());
        } else
        {
            return getDeclaration().getComponentName();
        }

    }

    public void setDeclaration(FlowComponent flowComponent)
    {
        if (flowComponent == null || declaration == null)
        {
            declaration = flowComponent;
        }
    }

    public FlowComponent getDeclaration()
    {
        return declaration;
    }

    public List<Integer> getContainers()
    {
        return containers;
    }

    public void add(int id)
    {
        if (!containers.contains((Integer)id))
        {
            containers.add(id);
        }
    }

    public void setContainers(List<Integer> containers)
    {
        this.containers = containers;
    }

    public boolean hasBeenExecuted()
    {
        return executed;
    }

    public void setExecuted(boolean executed)
    {
        this.executed = executed;
    }

    public void clearContainers()
    {
        containers.clear();
    }

    public void remove(int id)
    {
        containers.remove((Integer)id);
    }

    public static final String NBT_EXECUTED = "Executed";
    public static final String NBT_SELECTION = "Selection";
    public static final String NBT_SELECTION_ID = "Id";

    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        executed = nbtTagCompound.getBoolean(NBT_EXECUTED);
        containers.clear();
        NBTTagList tagList = nbtTagCompound.getTagList(NBT_SELECTION, 10);

        for (int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound selectionTag = tagList.getCompoundTagAt(i);

            containers.add((int)selectionTag.getShort(NBT_SELECTION_ID));
        }
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        nbtTagCompound.setBoolean(NBT_EXECUTED, executed);

        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < containers.size(); i++)
        {
            NBTTagCompound selectionTag = new NBTTagCompound();

            selectionTag.setShort(NBT_SELECTION_ID, (short)(int)containers.get(i));
            tagList.appendTag(selectionTag);
        }

        nbtTagCompound.setTag(NBT_SELECTION, tagList);
    }
}
