package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.CheckBox;
import advancedsystemsmanager.flow.elements.CheckBoxList;
import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.helpers.LocalizationHelper;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public abstract class MenuRedstoneSides extends Menu
{

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
    public static final String NBT_ACTIVE = "Selection";
    public static final String NBT_ALL = "RequireAll";
    public CheckBoxList checkBoxList;
    public RadioButtonList radioButtonList;
    public int selection;

    public MenuRedstoneSides(FlowComponent parent)
    {
        super(parent);

        selection = 0x3F; //All selected

        checkBoxList = new CheckBoxList();

        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++)
        {
            checkBoxList.addCheckBox(new CheckBoxSide(i));
        }

        radioButtonList = new RadioButtonList(getParent());

        radioButtonList.setSelectedOption(1);

        initRadioButtons();
    }

    public abstract void initRadioButtons();

    public void writeData(ASMPacket dw, boolean syncRequire)
    {
        dw.writeBoolean(syncRequire);
        if (syncRequire)
        {
            dw.writeBoolean(useFirstOption());
        } else
        {
            dw.writeByte(selection);
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
    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {
    }

    @Override
    public void copyFrom(Menu menu)
    {
        MenuRedstoneSides menuRedstone = (MenuRedstoneSides)menu;

        selection = menuRedstone.selection;
        setFirstOption(menuRedstone.useFirstOption());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        selection = nbtTagCompound.getByte(NBT_ACTIVE);
        setFirstOption(nbtTagCompound.getBoolean(NBT_ALL));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setByte(NBT_ACTIVE, (byte)selection);
        nbtTagCompound.setBoolean(NBT_ALL, useFirstOption());
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (isVisible() && selection == 0)
        {
            errors.add(Names.NO_REDSTONE_SIDES_ERROR);
        }
    }

    public boolean isSideRequired(int i)
    {
        return (selection & (1 << i)) != 0;
    }

    public class CheckBoxSide extends CheckBox
    {
        public int id;

        public CheckBoxSide(int id)
        {
            super(getParent(), LocalizationHelper.getDirectionString(id), CHECKBOX_X + CHECKBOX_SPACING_X * (id % 2), CHECKBOX_Y + CHECKBOX_SPACING_Y * (id / 2));

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
    }
}
