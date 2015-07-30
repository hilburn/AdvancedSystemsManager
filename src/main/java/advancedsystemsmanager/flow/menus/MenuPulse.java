package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.*;
import advancedsystemsmanager.containers.ContainerManager;
import advancedsystemsmanager.client.gui.GuiManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public class MenuPulse extends Menu
{

    public static final int CHECK_BOX_X = 5;
    public static final int CHECK_BOX_Y = 5;
    public static final int RADIO_BUTTON_X = 5;
    public static final int RADIO_BUTTON_Y = 44;
    public static final int RADIO_BUTTON_SPACING_X = 67;
    public static final int RADIO_BUTTON_SPACING_Y = 12;
    public static final int TEXT_BOX_X_LEFT = 10;
    public static final int TEXT_BOX_X_RIGHT = 70;
    public static final int TEXT_BOX_Y = 25;
    public static final String NBT_USE_PULSE = "UsePulse";
    public static final String NBT_TYPE = "Type";
    public static final String NBT_SECOND = "Seconds";
    public static final String NBT_TICK = "Ticks";
    public CheckBoxList checkBoxes;
    public boolean usePulse;
    public RadioButtonList radioButtons;
    public TextBoxNumberList textBoxes;
    public TextBoxNumber ticksTextBox;
    public TextBoxNumber secondsTextBox;

    public MenuPulse(FlowComponent parent)
    {
        super(parent);

        checkBoxes = new CheckBoxList();
        checkBoxes.addCheckBox(new CheckBox(getParent(), Names.DO_EMIT_PULSE, CHECK_BOX_X, CHECK_BOX_Y)
        {
            @Override
            public void setValue(boolean val)
            {
                usePulse = val;
            }

            @Override
            public boolean getValue()
            {
                return usePulse;
            }
        });

        radioButtons = new RadioButtonList(getParent());

        for (int i = 0; i < PULSE_OPTIONS.values().length; i++)
        {
            int x = i % 2;
            int y = i / 2;


            radioButtons.add(new RadioButton(RADIO_BUTTON_X + x * RADIO_BUTTON_SPACING_X, RADIO_BUTTON_Y + y * RADIO_BUTTON_SPACING_Y, PULSE_OPTIONS.values()[i].getName()));
        }


        textBoxes = new TextBoxNumberList();
        textBoxes.addTextBox(secondsTextBox = new TextBoxNumber(getParent(), TEXT_BOX_X_LEFT, TEXT_BOX_Y, 2, true));
        textBoxes.addTextBox(ticksTextBox = new TextBoxNumber(getParent(), TEXT_BOX_X_RIGHT, TEXT_BOX_Y, 2, true)
        {
            @Override
            public int getMaxNumber()
            {
                return 19;
            }
        });

        setDefault();
    }

    public void setDefault()
    {
        radioButtons.setSelectedOption(0);
        secondsTextBox.setNumber(0);
        ticksTextBox.setNumber(10);
    }

    @Override
    public String getName()
    {
        return Names.PULSE_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        checkBoxes.draw(gui, mX, mY);
        if (usePulse)
        {
            radioButtons.draw(gui, mX, mY);
            textBoxes.draw(gui, mX, mY);

            gui.drawCenteredString(Names.SECONDS, secondsTextBox.getX(), secondsTextBox.getY() - 7, 0.7F, secondsTextBox.getWidth(), 0x404040);
            gui.drawCenteredString(Names.TICKS, ticksTextBox.getX(), ticksTextBox.getY() - 7, 0.7F, ticksTextBox.getWidth(), 0x404040);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onClick(int mX, int mY, int button)
    {
        checkBoxes.onClick(mX, mY);
        if (usePulse)
        {
            radioButtons.onClick(mX, mY, button);
            textBoxes.onClick(mX, mY, button);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        return usePulse && textBoxes.onKeyStroke(gui, c, k);

    }

    @Override
    public void copyFrom(Menu menu)
    {
        MenuPulse menuPulse = (MenuPulse)menu;
        usePulse = menuPulse.usePulse;
        if (usePulse)
        {
            radioButtons.setSelectedOption(menuPulse.radioButtons.getSelectedOption());
            secondsTextBox.setNumber(menuPulse.secondsTextBox.getNumber());
            ticksTextBox.setNumber(menuPulse.ticksTextBox.getNumber());
        } else
        {
            setDefault();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        usePulse = nbtTagCompound.getBoolean(NBT_USE_PULSE);
        if (usePulse)
        {
            radioButtons.setSelectedOption(nbtTagCompound.getByte(NBT_TYPE));
            secondsTextBox.setNumber(nbtTagCompound.getByte(NBT_SECOND));
            ticksTextBox.setNumber(nbtTagCompound.getByte(NBT_TICK));
        } else
        {
            setDefault();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setBoolean(NBT_USE_PULSE, usePulse);
        if (usePulse)
        {
            nbtTagCompound.setByte(NBT_TYPE, (byte)radioButtons.getSelectedOption());
            nbtTagCompound.setByte(NBT_SECOND, (byte)secondsTextBox.getNumber());
            nbtTagCompound.setByte(NBT_TICK, (byte)ticksTextBox.getNumber());
        }
    }

    public void sendClientPacket(ContainerManager container, ComponentSyncType type)
    {
        ASMPacket dw = getWriterForClientComponentPacket(container);
        writeData(dw, type);
        PacketHandler.sendDataToListeningClients(container, dw);
    }

    public void writeData(ASMPacket dw, ComponentSyncType type)
    {
        dw.writeByte(type.ordinal());
        switch (type)
        {
            case CHECK_BOX:
                dw.writeBoolean(usePulse);
                break;
            case RADIO_BUTTON:
                dw.writeByte(radioButtons.getSelectedOption());
                break;
            case TEXT_BOX_1:
                dw.writeVarIntToBuffer(secondsTextBox.getNumber());
                break;
            case TEXT_BOX_2:
                dw.writeByte(ticksTextBox.getNumber());

        }
    }

    public boolean shouldEmitPulse()
    {
        return usePulse;
    }

    public PULSE_OPTIONS getSelectedPulseOverride()
    {
        return PULSE_OPTIONS.values()[radioButtons.getSelectedOption()];
    }

    public int getPulseTime()
    {
        return secondsTextBox.getNumber() * 20 + ticksTextBox.getNumber();
    }

    public enum PULSE_OPTIONS
    {
        EXTEND_OLD(Names.EXTEND_OLD),
        KEEP_ALL(Names.KEEP_ALL),
        KEEP_OLD(Names.KEEP_OLD),
        KEEP_NEW(Names.KEEP_NEW);

        public String name;

        PULSE_OPTIONS(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }

        public String getName()
        {
            return name;
        }
    }

    public enum ComponentSyncType
    {
        CHECK_BOX,
        RADIO_BUTTON,
        TEXT_BOX_1,
        TEXT_BOX_2
    }
}
