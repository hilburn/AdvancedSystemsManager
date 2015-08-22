package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.CheckBox;
import advancedsystemsmanager.flow.elements.CheckBoxList;
import advancedsystemsmanager.flow.elements.TextBoxNumber;
import advancedsystemsmanager.flow.elements.TextBoxNumberList;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;


public class MenuRedstoneStrength extends Menu
{
    public static final int CHECK_BOX_X = 5;
    public static final int CHECK_BOX_Y = 50;
    public static final int TEXT_BOX_X_LEFT = 10;
    public static final int TEXT_BOX_X_RIGHT = 77;
    public static final int TEXT_BOX_Y = 30;
    public static final int TEXT_BOX_TEXT_X = 46;
    public static final int TEXT_BOX_TEXT_Y = 33;
    public static final int MENU_WIDTH = 120;
    public static final int TEXT_MARGIN_X = 5;
    public static final int TEXT_Y = 5;
    public static final String NBT_LOW = "LowRange";
    public static final String NBT_HIGH = "HighRange";
    public static final String NBT_INVERTED = "Inverted";
    public CheckBoxList checkBoxes;
    public TextBoxNumberList textBoxes;
    public boolean inverted;
    public TextBoxNumber lowTextBox;
    public TextBoxNumber highTextBox;

    public MenuRedstoneStrength(FlowComponent parent)
    {
        super(parent);

        checkBoxes = new CheckBoxList();
        checkBoxes.addCheckBox(new CheckBox(getParent(), Names.INVERT_SELECTION, CHECK_BOX_X, CHECK_BOX_Y)
        {

            @Override
            public void setValue(boolean val)
            {
                inverted = val;
            }

            @Override
            public boolean getValue()
            {
                return inverted;
            }
        });

        textBoxes = new TextBoxNumberList();
        textBoxes.addTextBox(lowTextBox = new TextBoxNumber(getParent(), TEXT_BOX_X_LEFT, TEXT_BOX_Y, 2, true)
        {
            @Override
            public int getMaxNumber()
            {
                return 15;
            }
        });

        textBoxes.addTextBox(highTextBox = new TextBoxNumber(getParent(), TEXT_BOX_X_RIGHT, TEXT_BOX_Y, 2, true)
        {
            @Override
            public int getMaxNumber()
            {
                return 15;
            }
        });

        lowTextBox.setNumber(1);
        highTextBox.setNumber(15);
    }

    public void writeData(ASMPacket dw, int id)
    {
        boolean isTextBox = id != 2;
        dw.writeBoolean(isTextBox);
        if (isTextBox)
        {
            boolean isHigh = id == 1;
            dw.writeBoolean(isHigh);
            TextBoxNumber textBox = isHigh ? highTextBox : lowTextBox;
            dw.writeByte(textBox.getNumber());
        } else
        {
            dw.writeBoolean(inverted);
        }
    }

    @Override
    public String getName()
    {
        return Names.REDSTONE_STRENGTH_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiBase gui, int mX, int mY)
    {
        gui.drawSplitString(Names.REDSTONE_STRENGTH_INFO, TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - 2 * TEXT_MARGIN_X, 0.7F, 0x404040);
        gui.drawString(Names.THROUGH, TEXT_BOX_TEXT_X, TEXT_BOX_TEXT_Y, 0.7F, 0x404040);

        checkBoxes.draw(gui, mX, mY);
        textBoxes.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onClick(int mX, int mY, int button)
    {
        checkBoxes.onClick(mX, mY);
        textBoxes.onClick(mX, mY, button);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiBase gui, char c, int k)
    {
        return textBoxes.onKeyStroke(gui, c, k);
    }

    @Override
    public void copyFrom(Menu menu)
    {
        MenuRedstoneStrength menuStrength = (MenuRedstoneStrength)menu;

        lowTextBox.setNumber(menuStrength.lowTextBox.getNumber());
        highTextBox.setNumber(menuStrength.highTextBox.getNumber());
        inverted = menuStrength.inverted;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        lowTextBox.setNumber(nbtTagCompound.getByte(NBT_LOW));
        highTextBox.setNumber(nbtTagCompound.getByte(NBT_HIGH));
        inverted = nbtTagCompound.getBoolean(NBT_INVERTED);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setByte(NBT_LOW, (byte)lowTextBox.getNumber());
        nbtTagCompound.setByte(NBT_HIGH, (byte)highTextBox.getNumber());
        nbtTagCompound.setBoolean(NBT_INVERTED, inverted);
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (getLow() > getHigh())
        {
            errors.add(Names.INVALID_REDSTONE_RANGE_ERROR);
        } else if (getLow() == 0 && getHigh() == 15)
        {
            errors.add(Names.REDUNDANT_REDSTONE_RANGE_ERROR);
        }
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() == ConnectionSet.REDSTONE;
    }

    public int getLow()
    {
        return lowTextBox.getNumber();
    }

    public int getHigh()
    {
        return highTextBox.getNumber();
    }


    public boolean isInverted()
    {
        return inverted;
    }
}
