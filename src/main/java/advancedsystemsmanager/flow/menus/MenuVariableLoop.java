package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.flow.elements.VariableDisplay;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class MenuVariableLoop extends Menu
{
    public static final int DISPLAY_X = 45;
    public static final int DISPLAY_Y_TOP = 5;
    public static final int DISPLAY_Y_BOT = 25;
    public static final String NBT_LIST = "List";
    public static final String NBT_ELEMENT = "Element";
    public VariableDisplay listDisplay;
    public VariableDisplay elementDisplay;
    public int selectedList;
    public int selectedElement;

    public MenuVariableLoop(FlowComponent parent)
    {
        super(parent);

        listDisplay = new VariableDisplay(Names.VARIABLE_LIST, DISPLAY_X, DISPLAY_Y_TOP)
        {
            @Override
            public int getValue()
            {
                return selectedList;
            }

            @Override
            public void setValue(int val)
            {
                selectedList = val;
            }

            @Override
            public void onUpdate()
            {
                sendServerData(true);
            }
        };

        elementDisplay = new VariableDisplay(Names.VARIABLE_ELEMENT, DISPLAY_X, DISPLAY_Y_BOT)
        {
            @Override
            public int getValue()
            {
                return selectedElement;
            }

            @Override
            public void setValue(int val)
            {
                selectedElement = val;
            }

            @Override
            public void onUpdate()
            {
                sendServerData(false);
            }
        };

        selectedList = 0;
        selectedElement = 1;
    }

    public void sendServerData(boolean useList)
    {
        int val = useList ? selectedList : selectedElement;
        DataWriter dw = getWriterForServerComponentPacket();
        dw.writeBoolean(useList);
        dw.writeData(val, DataBitHelper.VARIABLE_TYPE);
        PacketHandler.sendDataToServer(dw);
    }

    @Override
    public String getName()
    {
        return Names.LOOP_VARIABLE_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        listDisplay.draw(gui, mX, mY);
        elementDisplay.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        listDisplay.drawMouseOver(gui, mX, mY);
        elementDisplay.drawMouseOver(gui, mX, mY);
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        listDisplay.onClick(mX, mY);
        elementDisplay.onClick(mX, mY);
    }

    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void copyFrom(Menu menu)
    {
        selectedList = ((MenuVariableLoop)menu).selectedList;
        selectedElement = ((MenuVariableLoop)menu).selectedElement;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        selectedList = nbtTagCompound.getByte(NBT_LIST);
        selectedElement = nbtTagCompound.getByte(NBT_ELEMENT);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setByte(NBT_LIST, (byte)selectedList);
        nbtTagCompound.setByte(NBT_ELEMENT, (byte)selectedElement);
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (!getListVariable().isValid())
        {
            errors.add(Names.LIST_NOT_DECLARED);
        }

        if (!getElementVariable().isValid())
        {
            errors.add(Names.ELEMENT_NOT_DECLARED);
        }
    }

    public Variable getListVariable()
    {
        return getParent().getManager().getVariables()[selectedList];
    }

    public Variable getElementVariable()
    {
        return getParent().getManager().getVariables()[selectedElement];
    }
}
