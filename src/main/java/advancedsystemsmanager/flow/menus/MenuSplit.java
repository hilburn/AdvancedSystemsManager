package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.flow.elements.CheckBox;
import advancedsystemsmanager.flow.elements.CheckBoxList;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;


public class MenuSplit extends Menu
{
    public MenuSplit(FlowComponent parent)
    {
        super(parent);


        radioButtons = new RadioButtonList()
        {
            @Override
            public void updateSelectedOption(int selectedOption)
            {
                setSelectedOption(selectedOption);
                sendServerData(0);
            }
        };

        radioButtons.add(new RadioButton(RADIO_X, RADIO_Y, Localization.SEQUENTIAL));
        radioButtons.add(new RadioButton(RADIO_X, RADIO_Y + SPACING_Y, Localization.SPLIT));

        checkBoxes = new CheckBoxList();

        checkBoxes.addCheckBox(new CheckBox(Localization.FAIR_SPLIT, CHECK_BOX_X, RADIO_Y + 2 * SPACING_Y)
        {
            @Override
            public void setValue(boolean val)
            {
                setFair(val);
            }

            @Override
            public boolean getValue()
            {
                return useFair();
            }

            @Override
            public void onUpdate()
            {
                sendServerData(1);
            }
        });

        checkBoxes.addCheckBox(new CheckBox(Localization.EMPTY_PINS, CHECK_BOX_X, RADIO_Y + 3 * SPACING_Y)
        {
            @Override
            public void setValue(boolean val)
            {
                setEmpty(val);
            }

            @Override
            public boolean getValue()
            {
                return useEmpty();
            }

            @Override
            public void onUpdate()
            {
                sendServerData(2);
            }
        });
    }

    public RadioButtonList radioButtons;
    public CheckBoxList checkBoxes;
    public boolean useFair;
    public boolean useEmpty;

    public static final int RADIO_X = 5;
    public static final int RADIO_Y = 5;
    public static final int CHECK_BOX_X = 15;
    public static final int SPACING_Y = 15;

    @Override
    public String getName()
    {
        return Localization.SPLIT_MENU.toString();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        if (useSplit())
        {
            checkBoxes.draw(gui, mX, mY);
        }
        radioButtons.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        if (useSplit())
        {
            checkBoxes.onClick(mX, mY);
        }
        radioButtons.onClick(mX, mY, button);
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
    public void writeData(DataWriter dw)
    {
        dw.writeBoolean(useSplit());
        if (useSplit())
        {
            dw.writeBoolean(useFair());
            dw.writeBoolean(useEmpty());
        }
    }

    @Override
    public void readData(DataReader dr)
    {
        setSplit(dr.readBoolean());
        if (useSplit())
        {
            setFair(dr.readBoolean());
            setEmpty(dr.readBoolean());
        } else
        {
            setFair(false);
            setEmpty(false);
        }
    }

    @Override
    public void copyFrom(Menu menu)
    {
        MenuSplit menuSplit = (MenuSplit)menu;
        setSplit(menuSplit.useSplit());
        setFair(menuSplit.useFair());
        setEmpty(menuSplit.useEmpty());
    }

    @Override
    public void refreshData(ContainerManager container, Menu newData)
    {
        MenuSplit newDataSplit = (MenuSplit)newData;

        if (useSplit() != newDataSplit.useSplit())
        {
            setSplit(newDataSplit.useSplit());

            sendClientData(container, 0);
        }

        if (useFair() != newDataSplit.useFair())
        {
            setFair(newDataSplit.useFair());

            sendClientData(container, 1);
        }

        if (useEmpty() != newDataSplit.useEmpty())
        {
            setEmpty(newDataSplit.useEmpty());

            sendClientData(container, 2);
        }
    }

    public void sendClientData(ContainerManager container, int id)
    {
        DataWriter dw = getWriterForClientComponentPacket(container);
        writeData(dw, id);
        PacketHandler.sendDataToListeningClients(container, dw);
    }

    public void sendServerData(int id)
    {
        DataWriter dw = getWriterForServerComponentPacket();
        writeData(dw, id);
        PacketHandler.sendDataToServer(dw);
    }

    public void writeData(DataWriter dw, int id)
    {
        dw.writeData(id, DataBitHelper.MENU_SPLIT_DATA_ID);
        switch (id)
        {
            case 0:
                dw.writeBoolean(useSplit());
                break;
            case 1:
                dw.writeBoolean(useFair());
                break;
            case 2:
                dw.writeBoolean(useEmpty());
                break;
        }
    }

    public static final String NBT_SPLIT = "Split";
    public static final String NBT_FAIR = "Fair";
    public static final String NBT_EMPTY = "Empty";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
    {
        setSplit(nbtTagCompound.getBoolean(NBT_SPLIT));
        if (useSplit())
        {
            setFair(nbtTagCompound.getBoolean(NBT_FAIR));
            setEmpty(nbtTagCompound.getBoolean(NBT_EMPTY));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setBoolean(NBT_SPLIT, useSplit());
        if (useSplit())
        {
            nbtTagCompound.setBoolean(NBT_FAIR, useFair());
            nbtTagCompound.setBoolean(NBT_EMPTY, useEmpty());
        }
    }

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        int id = dr.readData(DataBitHelper.MENU_SPLIT_DATA_ID);
        switch (id)
        {
            case 0:
                setSplit(dr.readBoolean());
                break;
            case 1:
                setFair(dr.readBoolean());
                break;
            case 2:
                setEmpty(dr.readBoolean());
                break;
        }
    }

    @Override
    public boolean isVisible()
    {
        return isSplitConnection(getParent());
    }

    public static boolean isSplitConnection(FlowComponent component)
    {
        return component.getConnectionSet() == ConnectionSet.MULTIPLE_OUTPUT_2 || component.getConnectionSet() == ConnectionSet.MULTIPLE_OUTPUT_5;
    }

    public boolean useSplit()
    {
        return radioButtons.getSelectedOption() == 1;
    }

    public void setSplit(boolean val)
    {
        radioButtons.setSelectedOption(val ? 1 : 0);
    }

    public boolean useFair()
    {
        return useFair;
    }

    public void setFair(boolean val)
    {
        useFair = val;
    }

    public boolean useEmpty()
    {
        return useEmpty;
    }

    public void setEmpty(boolean val)
    {
        useEmpty = val;
    }

}
