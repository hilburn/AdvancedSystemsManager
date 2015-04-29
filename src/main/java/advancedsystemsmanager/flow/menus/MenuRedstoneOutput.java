package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.flow.elements.TextBoxNumber;
import advancedsystemsmanager.flow.elements.TextBoxNumberList;
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

public class MenuRedstoneOutput extends Menu
{
    public MenuRedstoneOutput(FlowComponent parent)
    {
        super(parent);

        textBoxes = new TextBoxNumberList();

        textBoxes.addTextBox(textBox = new TextBoxNumber(TEXT_BOX_X, TEXT_BOX_Y, 2, true)
        {
            @Override
            public int getMaxNumber()
            {
                return 15;
            }

            @Override
            public void onNumberChanged()
            {
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeBoolean(true); //header
                dw.writeData(getNumber(), DataBitHelper.MENU_REDSTONE_ANALOG);
                PacketHandler.sendDataToServer(dw);
            }
        });
        textBox.setNumber(15);

        radioButtons = new RadioButtonList()
        {
            @Override
            public void updateSelectedOption(int selectedOption)
            {
                setSelectedOption(selectedOption);
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeBoolean(false); //header
                dw.writeData(selectedOption, DataBitHelper.MENU_REDSTONE_OUTPUT_TYPE);
                PacketHandler.sendDataToServer(dw);
            }
        };

        for (int i = 0; i < Settings.values().length; i++)
        {
            int ix = i % 2;
            int iy = i / 2;

            int x = RADIO_BUTTON_X + ix * RADIO_SPACING_X;
            int y = RADIO_BUTTON_Y + iy * RADIO_SPACING_Y;


            radioButtons.add(new RadioButton(x, y, Settings.values()[i].getName()));
        }
    }

    public TextBoxNumberList textBoxes;
    public TextBoxNumber textBox;
    public RadioButtonList radioButtons;

    public int getSelectedStrength()
    {
        return textBox.getNumber();
    }

    public static enum Settings
    {
        FIXED(Names.FIXED),
        TOGGLE(Names.TOGGLE),
        MAX(Names.MAX),
        MIN(Names.MIN),
        INCREASE(Names.INCREASE),
        DECREASE(Names.DECREASE),
        FORWARD(Names.FORWARD),
        BACKWARD(Names.BACKWARD);

        public String name;

        Settings(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    public static final int RADIO_BUTTON_X = 5;
    public static final int RADIO_BUTTON_Y = 22;
    public static final int RADIO_SPACING_X = 68;
    public static final int RADIO_SPACING_Y = 12;

    public static final int TEXT_BOX_X = 80;
    public static final int TEXT_BOX_Y = 5;

    public static final int TEXT_X = 5;
    public static final int TEXT_Y = 9;

    @Override
    public String getName()
    {
        return Names.REDSTONE_OUTPUT_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        if (useStrengthSetting())
        {
            gui.drawString(Names.REDSTONE_STRENGTH, TEXT_X, TEXT_Y, 0.7F, 0x404040);
            textBoxes.draw(gui, mX, mY);
        } else
        {
            gui.drawString(Names.DIGITAL_TOGGLE, TEXT_X, TEXT_Y, 0.7F, 0x404040);
        }
        radioButtons.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        if (useStrengthSetting())
        {
            return textBoxes.onKeyStroke(gui, c, k);
        } else
        {
            return super.onKeyStroke(gui, c, k);
        }
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        if (useStrengthSetting())
        {
            textBoxes.onClick(mX, mY, button);
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
        dw.writeData(textBox.getNumber(), DataBitHelper.MENU_REDSTONE_ANALOG);
        dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.MENU_REDSTONE_OUTPUT_TYPE);
    }

    @Override
    public void readData(DataReader dr)
    {
        textBox.setNumber(dr.readData(DataBitHelper.MENU_REDSTONE_ANALOG));
        radioButtons.setSelectedOption(dr.readData(DataBitHelper.MENU_REDSTONE_OUTPUT_TYPE));
    }

    @Override
    public void copyFrom(Menu menu)
    {
        MenuRedstoneOutput menuOutput = (MenuRedstoneOutput)menu;

        textBox.setNumber(menuOutput.textBox.getNumber());
        radioButtons.setSelectedOption(menuOutput.radioButtons.getSelectedOption());
    }

    @Override
    public void refreshData(ContainerManager container, Menu newData)
    {
        MenuRedstoneOutput newDataOutput = (MenuRedstoneOutput)newData;

        if (textBox.getNumber() != newDataOutput.textBox.getNumber())
        {
            textBox.setNumber(newDataOutput.textBox.getNumber());

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeBoolean(true); //header
            dw.writeData(textBox.getNumber(), DataBitHelper.MENU_REDSTONE_ANALOG);
            PacketHandler.sendDataToListeningClients(container, dw);
        }

        if (radioButtons.getSelectedOption() != newDataOutput.radioButtons.getSelectedOption())
        {
            radioButtons.setSelectedOption(newDataOutput.radioButtons.getSelectedOption());

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeBoolean(false); //header
            dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.MENU_REDSTONE_OUTPUT_TYPE);
            PacketHandler.sendDataToListeningClients(container, dw);
        }
    }

    public static final String NBT_NUMBER = "Strength";
    public static final String NBT_TYPE = "OutputType";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
    {
        textBox.setNumber(nbtTagCompound.getByte(NBT_NUMBER));
        radioButtons.setSelectedOption(nbtTagCompound.getByte(NBT_TYPE));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setByte(NBT_NUMBER, (byte)textBox.getNumber());
        nbtTagCompound.setByte(NBT_TYPE, (byte)radioButtons.getSelectedOption());
    }

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        boolean isNumber = dr.readBoolean();
        if (isNumber)
        {
            textBox.setNumber(dr.readData(DataBitHelper.MENU_REDSTONE_ANALOG));
        } else
        {
            int type = dr.readData(DataBitHelper.MENU_REDSTONE_OUTPUT_TYPE);
            radioButtons.setSelectedOption(type);
        }
    }

    public boolean useStrengthSetting()
    {
        return getSelectedSetting() != Settings.TOGGLE;
    }

    public Settings getSelectedSetting()
    {
        return Settings.values()[radioButtons.getSelectedOption()];
    }
}
