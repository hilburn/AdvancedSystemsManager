package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.*;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Comparator;


public class MenuListOrder extends Menu
{
    public static final int RADIO_BUTTON_X = 5;
    public static final int RADIO_BUTTON_Y = 20;
    public static final int RADIO_SPACING_Y = 12;
    public static final int CHECK_BOX_X = 5;
    public static final int CHECK_BOX_AMOUNT_Y = 5;
    public static final int CHECK_BOX_REVERSE_Y = 58;
    public static final int TEXT_BOX_X = 60;
    public static final int TEXT_BOX_Y = 3;
    public static final String NBT_ALL = "All";
    public static final String NBT_AMOUNT = "Amount";
    public static final String NBT_REVERSED = "Reversed";
    public static final String NBT_ORDER = "Order";
    public TextBoxNumberList textBoxes;
    public TextBoxNumber textBox;
    public RadioButtonList radioButtons;
    public CheckBoxList checkBoxes;
    public boolean reversed;
    public boolean all;

    public MenuListOrder(FlowComponent parent)
    {
        super(parent);

        radioButtons = new RadioButtonList()
        {
            @Override
            public void updateSelectedOption(int selectedOption)
            {
                setSelectedOption(selectedOption);
                sendServerData(UpdateType.TYPE);
            }
        };

        for (int i = 0; i < LoopOrder.values().length; i++)
        {
            int x = RADIO_BUTTON_X;
            int y = RADIO_BUTTON_Y + i * RADIO_SPACING_Y;

            radioButtons.add(new RadioButton(x, y, LoopOrder.values()[i].toString()));
        }

        checkBoxes = new CheckBoxList();
        checkBoxes.addCheckBox(new CheckBox(Names.USE_ALL, CHECK_BOX_X, CHECK_BOX_AMOUNT_Y)
        {
            @Override
            public void setValue(boolean val)
            {
                all = val;
            }

            @Override
            public boolean getValue()
            {
                return all;
            }

            @Override
            public void onUpdate()
            {
                sendServerData(UpdateType.USE_ALL);
            }
        });

        checkBoxes.addCheckBox(new CheckBox(Names.REVERSED, CHECK_BOX_X, CHECK_BOX_REVERSE_Y)
        {
            @Override
            public void setValue(boolean val)
            {
                reversed = val;
            }

            @Override
            public boolean getValue()
            {
                return reversed;
            }

            @Override
            public void onUpdate()
            {
                sendServerData(UpdateType.REVERSED);
            }

            @Override
            public boolean isVisible()
            {
                return canReverse();
            }
        });

        all = true;

        textBoxes = new TextBoxNumberList();
        textBoxes.addTextBox(textBox = new TextBoxNumber(TEXT_BOX_X, TEXT_BOX_Y, 2, false)
        {
            @Override
            public boolean isVisible()
            {
                return !all;
            }

            @Override
            public void onNumberChanged()
            {
                sendServerData(UpdateType.AMOUNT);
            }
        });

        textBox.setNumber(1);
    }

    public void sendServerData(UpdateType type)
    {
        DataWriter dw = getWriterForServerComponentPacket();
        writeData(dw, type);
        PacketHandler.sendDataToServer(dw);
    }

    public void writeData(DataWriter dw, UpdateType type)
    {
        dw.writeData(type.ordinal(), DataBitHelper.ORDER_TYPES);
        switch (type)
        {
            case USE_ALL:
                dw.writeBoolean(all);
                break;
            case AMOUNT:
                dw.writeData(textBox.getNumber(), DataBitHelper.ORDER_AMOUNT);
                break;
            case TYPE:
                dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.ORDER_TYPES);
                break;
            case REVERSED:
                dw.writeBoolean(reversed);
        }

    }

    public boolean canReverse()
    {
        return getOrder() != LoopOrder.RANDOM;
    }

    public LoopOrder getOrder()
    {
        return LoopOrder.values()[radioButtons.getSelectedOption()];
    }

    @Override
    public String getName()
    {
        return Names.LOOP_ORDER_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        radioButtons.draw(gui, mX, mY);
        checkBoxes.draw(gui, mX, mY);
        textBoxes.draw(gui, mX, mY);
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
        radioButtons.onClick(mX, mY, button);
        checkBoxes.onClick(mX, mY);
        textBoxes.onClick(mX, mY, button);
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

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        return textBoxes.onKeyStroke(gui, c, k);
    }

    @Override
    public void writeData(DataWriter dw)
    {
        dw.writeBoolean(all);
        dw.writeData(textBox.getNumber(), DataBitHelper.ORDER_AMOUNT);
        dw.writeBoolean(reversed);
        dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.ORDER_TYPES);
    }

    @Override
    public void readData(DataReader dr)
    {
        all = dr.readBoolean();
        textBox.setNumber(dr.readData(DataBitHelper.ORDER_AMOUNT));
        reversed = dr.readBoolean();
        radioButtons.setSelectedOption(dr.readData(DataBitHelper.ORDER_TYPES));
    }

    @Override
    public void copyFrom(Menu menu)
    {
        MenuListOrder menuOrder = ((MenuListOrder)menu);
        all = menuOrder.all;
        textBox.setNumber(menuOrder.textBox.getNumber());
        reversed = menuOrder.reversed;
        radioButtons.setSelectedOption(menuOrder.radioButtons.getSelectedOption());
    }

    @Override
    public void refreshData(ContainerManager container, Menu newData)
    {
        MenuListOrder newDataOrder = (MenuListOrder)newData;

        if (all != newDataOrder.all)
        {
            all = newDataOrder.all;
            sendClientData(container, UpdateType.USE_ALL);
        }

        if (textBox.getNumber() != newDataOrder.textBox.getNumber())
        {
            textBox.setNumber(newDataOrder.textBox.getNumber());
        }

        if (reversed != newDataOrder.reversed)
        {
            reversed = newDataOrder.reversed;
            sendClientData(container, UpdateType.REVERSED);
        }

        if (radioButtons.getSelectedOption() != newDataOrder.radioButtons.getSelectedOption())
        {
            radioButtons.setSelectedOption(newDataOrder.radioButtons.getSelectedOption());

            sendClientData(container, UpdateType.TYPE);
        }
    }

    public void sendClientData(ContainerManager container, UpdateType type)
    {
        DataWriter dw = getWriterForClientComponentPacket(container);
        writeData(dw, type);
        PacketHandler.sendDataToListeningClients(container, dw);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        all = nbtTagCompound.getBoolean(NBT_ALL);
        textBox.setNumber(nbtTagCompound.getByte(NBT_AMOUNT));
        reversed = nbtTagCompound.getBoolean(NBT_REVERSED);
        radioButtons.setSelectedOption(nbtTagCompound.getByte(NBT_ORDER));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setBoolean(NBT_ALL, all);
        nbtTagCompound.setByte(NBT_AMOUNT, (byte)textBox.getNumber());
        nbtTagCompound.setBoolean(NBT_REVERSED, reversed);
        nbtTagCompound.setByte(NBT_ORDER, (byte)radioButtons.getSelectedOption());
    }

    @Override
    public void readNetworkComponent(ByteBuf dr)
    {
        UpdateType type = UpdateType.values()[dr.readByte()];
        switch (type)
        {
            case USE_ALL:
                all = dr.readBoolean();
                break;
            case AMOUNT:
                textBox.setNumber(dr.readByte());
                break;
            case REVERSED:
                reversed = dr.readBoolean();
                break;
            case TYPE:
                radioButtons.setSelectedOption(dr.readByte());
        }

    }

    public Comparator<? super Integer> getComparator()
    {
        return reversed ? getOrder().reversedComparator : getOrder().comparator;
    }

    public boolean isReversed()
    {
        return reversed;
    }

    public int getAmount()
    {
        return textBox.getNumber();
    }

    public boolean useAll()
    {
        return all;
    }

    public enum LoopOrder
    {
        NORMAL(Names.ORDER_STANDARD, null),
        CABLE(Names.ORDER_CABLE, new Comparator<Integer>()
        {
            @Override
            public int compare(Integer o1, Integer o2)
            {
                return o1 < o2 ? -1 : 1;
            }
        }),
        RANDOM(Names.ORDER_RANDOM, null);

        public String name;
        public Comparator<Integer> comparator;
        public Comparator<Integer> reversedComparator;

        LoopOrder(String name, final Comparator<Integer> comparator)
        {
            this.name = name;
            this.comparator = comparator;
            if (comparator != null)
            {
                reversedComparator = new Comparator<Integer>()
                {
                    @Override
                    public int compare(Integer o1, Integer o2)
                    {
                        return comparator.compare(o2, o1);
                    }
                };
            }
        }


        @Override
        public String toString()
        {
            return name;
        }
    }

    public enum UpdateType
    {
        USE_ALL,
        AMOUNT,
        TYPE,
        REVERSED
    }
}
