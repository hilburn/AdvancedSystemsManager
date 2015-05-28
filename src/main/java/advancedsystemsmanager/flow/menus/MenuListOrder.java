package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.*;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


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

        radioButtons = new RadioButtonList(getParent());

        for (int i = 0; i < LoopOrder.values().length; i++)
        {
            int x = RADIO_BUTTON_X;
            int y = RADIO_BUTTON_Y + i * RADIO_SPACING_Y;

            radioButtons.add(new RadioButton(x, y, LoopOrder.values()[i].toString()));
        }

        checkBoxes = new CheckBoxList();
        checkBoxes.addCheckBox(new CheckBox(getParent(), Names.USE_ALL, CHECK_BOX_X, CHECK_BOX_AMOUNT_Y)
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
        });

        checkBoxes.addCheckBox(new CheckBox(getParent(), Names.REVERSED, CHECK_BOX_X, CHECK_BOX_REVERSE_Y)
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
            public boolean isVisible()
            {
                return canReverse();
            }
        });

        all = true;

        textBoxes = new TextBoxNumberList();
        textBoxes.addTextBox(textBox = new TextBoxNumber(getParent(), TEXT_BOX_X, TEXT_BOX_Y, 2, false)
        {
            @Override
            public boolean isVisible()
            {
                return !all;
            }
        });

        textBox.setNumber(1);
    }

    public void writeData(ASMPacket dw, UpdateType type)
    {
        dw.writeByte(type.ordinal());
        switch (type)
        {
            case USE_ALL:
                dw.writeBoolean(all);
                break;
            case AMOUNT:
                dw.writeVarIntToBuffer(textBox.getNumber());
                break;
            case TYPE:
                dw.writeByte(radioButtons.getSelectedOption());
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
    public void copyFrom(Menu menu)
    {
        MenuListOrder menuOrder = ((MenuListOrder)menu);
        all = menuOrder.all;
        textBox.setNumber(menuOrder.textBox.getNumber());
        reversed = menuOrder.reversed;
        radioButtons.setSelectedOption(menuOrder.radioButtons.getSelectedOption());
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

    public Comparator<? super Integer> getComparator()
    {
        return getOrder().comparator;
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
        })
                {
                    @Override
                    protected void sort(List<Integer> inventories)
                    {
                        Collections.sort(inventories, comparator);
                    }
                },
        RANDOM(Names.ORDER_RANDOM, null)
                {
                    @Override
                    protected void sort(List<Integer> inventories)
                    {
                        Collections.shuffle(inventories);
                    }
                };

        public String name;
        public Comparator<Integer> comparator;

        LoopOrder(String name, final Comparator<Integer> comparator)
        {
            this.name = name;
            this.comparator = comparator;
        }

        public List<Integer> applyOrder(List<Integer> inventories, MenuListOrder menu)
        {
            ArrayList<Integer> ret = new ArrayList<Integer>(inventories);
            sort(ret);
            if (menu.isReversed()) Collections.reverse(ret);
            if (!menu.useAll())
            {
                int len = menu.getAmount();

                while (ret.size() > len)
                {
                    ret.remove(ret.size() - 1);
                }
            }
            return ret;
        }

        protected void sort(List<Integer> inventories)
        {
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
