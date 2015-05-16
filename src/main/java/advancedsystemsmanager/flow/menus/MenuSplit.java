package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.CheckBox;
import advancedsystemsmanager.flow.elements.CheckBoxList;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public class MenuSplit extends Menu
{
    public static final int RADIO_X = 5;
    public static final int RADIO_Y = 5;
    public static final int CHECK_BOX_X = 15;
    public static final int SPACING_Y = 15;
    public static final String NBT_SPLIT = "Split";
    public static final String NBT_FAIR = "Fair";
    public static final String NBT_EMPTY = "Empty";
    public RadioButtonList radioButtons;
    public CheckBoxList checkBoxes;
    public boolean useFair;
    public boolean useEmpty;

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

        radioButtons.add(new RadioButton(RADIO_X, RADIO_Y, Names.SEQUENTIAL));
        radioButtons.add(new RadioButton(RADIO_X, RADIO_Y + SPACING_Y, Names.SPLIT));

        checkBoxes = new CheckBoxList();

        checkBoxes.addCheckBox(new CheckBox(Names.FAIR_SPLIT, CHECK_BOX_X, RADIO_Y + 2 * SPACING_Y)
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

        checkBoxes.addCheckBox(new CheckBox(Names.EMPTY_PINS, CHECK_BOX_X, RADIO_Y + 3 * SPACING_Y)
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

    public boolean useSplit()
    {
        return radioButtons.getSelectedOption() == 1;
    }

    public boolean useFair()
    {
        return useFair;
    }

    public boolean useEmpty()
    {
        return useEmpty;
    }

    public void setFair(boolean val)
    {
        useFair = val;
    }

    public void setEmpty(boolean val)
    {
        useEmpty = val;
    }

    @Override
    public String getName()
    {
        return Names.SPLIT_MENU;
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
    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {
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
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
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
    public boolean isVisible()
    {
        return isSplitConnection(getParent());
    }

    public static boolean isSplitConnection(FlowComponent component)
    {
        return component.getConnectionSet() == ConnectionSet.MULTIPLE_OUTPUT_2 || component.getConnectionSet() == ConnectionSet.MULTIPLE_OUTPUT_5;
    }

    public void setSplit(boolean val)
    {
        radioButtons.setSelectedOption(val ? 1 : 0);
    }
}
