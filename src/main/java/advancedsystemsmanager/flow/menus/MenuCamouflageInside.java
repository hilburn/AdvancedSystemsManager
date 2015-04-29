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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;


public class MenuCamouflageInside extends MenuCamouflageAdvanced
{
    public MenuCamouflageInside(FlowComponent parent)
    {
        super(parent);

        radioButtons = new RadioButtonList()
        {
            @Override
            public void updateSelectedOption(int selectedOption)
            {
                setSelectedOption(selectedOption);

                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.CAMOUFLAGE_INSIDE);
                PacketHandler.sendDataToServer(dw);
            }
        };

        for (int i = 0; i < InsideSetType.values().length; i++)
        {
            radioButtons.add(new RadioButton(RADIO_BUTTON_X, RADIO_BUTTON_Y + i * RADIO_BUTTON_SPACING, InsideSetType.values()[i].name));
        }
    }

    public static final int RADIO_BUTTON_X = 5;
    public static final int RADIO_BUTTON_Y = 5;
    public static final int RADIO_BUTTON_SPACING = 12;

    public RadioButtonList radioButtons;

    @Override
    public String getWarningText()
    {
        return Names.INSIDE_WARNING;
    }

    @Override
    public String getName()
    {
        return Names.INSIDE_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        super.draw(gui, mX, mY);

        radioButtons.draw(gui, mX, mY);
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
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
        dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.CAMOUFLAGE_INSIDE);
    }

    @Override
    public void readData(DataReader dr)
    {
        radioButtons.setSelectedOption(dr.readData(DataBitHelper.CAMOUFLAGE_INSIDE));
    }

    @Override
    public void copyFrom(Menu menu)
    {
        radioButtons.setSelectedOption(((MenuCamouflageInside)menu).radioButtons.getSelectedOption());
    }

    @Override
    public void refreshData(ContainerManager container, Menu newData)
    {
        MenuCamouflageInside newDataInside = (MenuCamouflageInside)newData;

        if (radioButtons.getSelectedOption() != newDataInside.radioButtons.getSelectedOption())
        {
            radioButtons.setSelectedOption(newDataInside.radioButtons.getSelectedOption());

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.CAMOUFLAGE_INSIDE);
            PacketHandler.sendDataToListeningClients(container, dw);
        }
    }

    public static final String NBT_SETTING = "Setting";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
    {
        radioButtons.setSelectedOption(nbtTagCompound.getByte(NBT_SETTING));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setByte(NBT_SETTING, (byte)radioButtons.getSelectedOption());
    }

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        radioButtons.setSelectedOption(dr.readData(DataBitHelper.CAMOUFLAGE_INSIDE));
    }

    public InsideSetType getCurrentType()
    {
        return InsideSetType.values()[radioButtons.getSelectedOption()];
    }

    public enum InsideSetType
    {
        ONLY_OUTSIDE(Names.CAMOUFLAGE_ONLY_OUTSIDE),
        ONLY_INSIDE(Names.CAMOUFLAGE_ONLY_INSIDE),
        OPPOSITE(Names.CAMOUFLAGE_OPPOSITE_INSIDE),
        SAME(Names.CAMOUFLAGE_SAME_INSIDE),
        NOTHING(Names.CAMOUFLAGE_NO_UPDATE);


        public String name;

        InsideSetType(String name)
        {
            this.name = name;
        }
    }
}
