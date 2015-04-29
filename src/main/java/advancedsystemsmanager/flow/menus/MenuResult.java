package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.registry.ConnectionSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public class MenuResult extends Menu
{

    public MenuResult(FlowComponent parent)
    {
        super(parent);

        sets = parent.getType().getSets();

        radioButtons = new RadioButtonList()
        {
            @Override
            public void updateSelectedOption(int selectedOption)
            {
                DataWriter dw = getWriterForServerComponentPacket();
                writeData(dw, selectedOption);
                PacketHandler.sendDataToServer(dw);
            }

            @Override
            public void setSelectedOption(int selectedOption)
            {
                super.setSelectedOption(selectedOption);

                if (selectedOption >= sets.length)
                {
                    System.out.println(getParent().getType().getLongName());
                }
                getParent().setConnectionSet(sets[radioButtons.getSelectedOption()]);

                if (getParent().getType() == CommandRegistry.VARIABLE)
                {
                    getParent().getManager().updateVariables();
                } else if (getParent().getType() == CommandRegistry.NODE)
                {
                    getParent().setParent(getParent().getParent());
                }
            }
        };

        for (int i = 0; i < sets.length; i++)
        {
            radioButtons.add(new RadioButton(RADIO_X, RADIO_Y + i * RADIO_MARGIN, sets[i].getName()));
        }

        for (int i = 0; i < sets.length; i++)
        {
            ConnectionSet set = sets[i];

            if (parent.getConnectionSet().equals(set))
            {
                radioButtons.setSelectedOption(i);
                break;
            }
        }
    }

    public static final int RADIO_X = 5;
    public static final int RADIO_Y = 5;
    public static final int RADIO_MARGIN = 13;

    public ConnectionSet[] sets;
    public RadioButtonList radioButtons;

    @Override
    public String getName()
    {
        return Names.CONNECTIONS_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
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
    public void writeData(DataWriter dw)
    {
        writeData(dw, radioButtons.getSelectedOption());
    }

    @Override
    public void readData(DataReader dr)
    {
        readTheData(dr);
    }

    @Override
    public void copyFrom(Menu menu)
    {
        radioButtons.setSelectedOption(((MenuResult)menu).radioButtons.getSelectedOption());
    }

    @Override
    public void refreshData(ContainerManager container, Menu newData)
    {
        MenuResult newDataResult = ((MenuResult)newData);

        if (radioButtons.getSelectedOption() != newDataResult.radioButtons.getSelectedOption())
        {
            radioButtons.setSelectedOption(newDataResult.radioButtons.getSelectedOption());

            DataWriter dw = getWriterForClientComponentPacket(container);
            writeData(dw, radioButtons.getSelectedOption());
            PacketHandler.sendDataToListeningClients(container, dw);
        }
    }

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        readTheData(dr);
    }

    public void readTheData(DataReader dr)
    {
        radioButtons.setSelectedOption(dr.readData(DataBitHelper.MENU_CONNECTION_TYPE_ID));
    }

    public void writeData(DataWriter dw, int val)
    {
        dw.writeData(val, DataBitHelper.MENU_CONNECTION_TYPE_ID);
    }


    public static final String NBT_SELECTED = "SelectedOption";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
    {
        radioButtons.setSelectedOption(nbtTagCompound.getByte(NBT_SELECTED));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setByte(NBT_SELECTED, (byte)radioButtons.getSelectedOption());
    }


    @Override
    public boolean isVisible()
    {
        return sets.length > 1;
    }
}
