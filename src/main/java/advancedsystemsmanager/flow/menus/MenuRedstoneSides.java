package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.flow.elements.CheckBox;
import advancedsystemsmanager.flow.elements.CheckBoxList;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public abstract class MenuRedstoneSides extends Menu
{

    public MenuRedstoneSides(FlowComponent parent)
    {
        super(parent);

        selection = 0x3F; //All selected

        checkBoxList = new CheckBoxList();

        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++)
        {
            checkBoxList.addCheckBox(new CheckBoxSide(i));
        }

        radioButtonList = new RadioButtonList()
        {
            @Override
            public void updateSelectedOption(int selectedOption)
            {
                setFirstOption(selectedOption == 0);
                sendServerData(true);
            }
        };

        radioButtonList.setSelectedOption(1);

        initRadioButtons();
    }

    public abstract void initRadioButtons();

    public static final int RADIO_BUTTON_X_LEFT = 5;
    public static final int RADIO_BUTTON_X_RIGHT = 65;
    public static final int RADIO_BUTTON_Y = 23;

    public static final int CHECKBOX_X = 5;
    public static final int CHECKBOX_Y = 35;
    public static final int CHECKBOX_SPACING_X = 70;
    public static final int CHECKBOX_SPACING_Y = 12;

    public static final int MENU_WIDTH = 120;
    public static final int TEXT_MARGIN_X = 5;
    public static final int TEXT_Y = 5;

    public class CheckBoxSide extends CheckBox
    {
        public int id;

        public CheckBoxSide(int id)
        {
            super(Localization.getForgeDirectionLocalization(id), CHECKBOX_X + CHECKBOX_SPACING_X * (id % 2), CHECKBOX_Y + CHECKBOX_SPACING_Y * (id / 2));

            this.id = id;
        }

        @Override
        public void setValue(boolean val)
        {
            if (val)
            {
                selection |= 1 << id;
            } else
            {
                selection &= ~(1 << id);
            }
        }

        @Override
        public boolean getValue()
        {
            return (selection & (1 << id)) != 0;
        }

        @Override
        public void onUpdate()
        {
            sendServerData(false);
        }
    }

    public CheckBoxList checkBoxList;
    public RadioButtonList radioButtonList;
    public int selection;


    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        gui.drawSplitString(getMessage(), TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X, 0.7F, 0x404040);

        checkBoxList.draw(gui, mX, mY);
        radioButtonList.draw(gui, mX, mY);
    }

    public abstract String getMessage();

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        checkBoxList.onClick(mX, mY);
        radioButtonList.onClick(mX, mY, button);
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
        dw.writeBoolean(useFirstOption());
        dw.writeData(selection, DataBitHelper.MENU_REDSTONE_SETTING);
    }

    @Override
    public void readData(DataReader dr)
    {
        setFirstOption(dr.readBoolean());
        selection = dr.readData(DataBitHelper.MENU_REDSTONE_SETTING);
    }

    @Override
    public void copyFrom(Menu menu)
    {
        MenuRedstoneSides menuRedstone = (MenuRedstoneSides)menu;

        selection = menuRedstone.selection;
        setFirstOption(menuRedstone.useFirstOption());
    }

    @Override
    public void refreshData(ContainerManager container, Menu newData)
    {
        MenuRedstoneSides newDataRedstone = (MenuRedstoneSides)newData;

        if (useFirstOption() != newDataRedstone.useFirstOption())
        {
            setFirstOption(newDataRedstone.useFirstOption());

            sendClientData(container, true);
        }

        if (selection != newDataRedstone.selection)
        {
            selection = newDataRedstone.selection;

            sendClientData(container, false);
        }
    }

    public void sendClientData(ContainerManager container, boolean syncRequire)
    {
        DataWriter dw = getWriterForClientComponentPacket(container);
        writeData(dw, syncRequire);
        PacketHandler.sendDataToListeningClients(container, dw);
    }

    public void sendServerData(boolean syncRequire)
    {
        DataWriter dw = getWriterForServerComponentPacket();
        writeData(dw, syncRequire);
        PacketHandler.sendDataToServer(dw);
    }

    public void writeData(DataWriter dw, boolean syncRequire)
    {
        dw.writeBoolean(syncRequire);
        if (syncRequire)
        {
            dw.writeBoolean(useFirstOption());
        } else
        {
            dw.writeData(selection, DataBitHelper.MENU_REDSTONE_SETTING);
        }
    }

    public static final String NBT_ACTIVE = "Selection";
    public static final String NBT_ALL = "RequrieAll";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
    {
        //Forgot to save it in earlier versions
        if (version >= 3)
        {
            selection = nbtTagCompound.getByte(NBT_ACTIVE);
            setFirstOption(nbtTagCompound.getBoolean(NBT_ALL));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setByte(NBT_ACTIVE, (byte)selection);
        nbtTagCompound.setBoolean(NBT_ALL, useFirstOption());
    }

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        if (dr.readBoolean())
        {
            setFirstOption(dr.readBoolean());
        } else
        {
            selection = dr.readData(DataBitHelper.MENU_REDSTONE_SETTING);
        }
    }


    public boolean useFirstOption()
    {
        return radioButtonList.getSelectedOption() == 0;
    }

    public void setFirstOption(boolean val)
    {
        radioButtonList.setSelectedOption(val ? 0 : 1);
    }

    public boolean isSideRequired(int i)
    {
        return (selection & (1 << i)) != 0;
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (isVisible() && selection == 0)
        {
            errors.add(Localization.NO_REDSTONE_SIDES_ERROR.toString());
        }
    }
}
