package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.*;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class MenuVariable extends Menu
{
    public static final int RADIO_BUTTON_X = 5;
    public static final int RADIO_BUTTON_Y = 40;
    public static final int RADIO_BUTTON_SPACING = 12;
    public static final String NBT_VARIABLE = "Variable";
    public static final String NBT_MODE = "Mode";
    public static final String NBT_EXECUTED = "Executed";
    public RadioButtonList radioButtons;
    public ScrollVariable variables;
    public boolean executed;

    public MenuVariable(final FlowComponent parent)
    {
        super(parent);

        radioButtons = new RadioButtonList(getParent());

        for (VariableMode mode : VariableMode.values())
        {
            int y = 52 ;
            int x = RADIO_BUTTON_X + mode.ordinal() * 34;
            radioButtons.add(new RadioButton(x, y, mode.toString())
            {
                @Override
                public boolean isVisible()
                {
                    return !isDeclaration();
                }
            });
        }

        variables = new ScrollVariable(parent)
        {
            @Override
            public void setSelected(int val)
            {
                boolean declaration = isDeclaration();
                if (declaration)
                {
                    getParent().getManager().removeVariableDeclaration(variables.variable, getParent());
                    variable = val;
                    getParent().getManager().updateDeclaration(getParent(), variables.variable);
                } else
                {
                    variable = val;
                }
            }

            @Override
            public int getVisibleRows()
            {
                return isDeclaration() ? super.getVisibleRows() : 1;
            }
        };
        variables.variable = -1;

    }

    public boolean isDeclaration()
    {
        return getParent().getConnectionSet() == ConnectionSet.EMPTY;
    }

    public VariableMode getVariableMode()
    {
        return VariableMode.values()[radioButtons.getSelectedOption()];
    }

    @Override
    public String getName()
    {
        return Names.VARIABLE_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        radioButtons.draw(gui, mX, mY);
        variables.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        variables.drawMouseOver(gui, mX, mY);
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        radioButtons.onClick(mX, mY, button);
        variables.onClick(mX, mY, button);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        return variables.onKeyStroke(gui, c, k);
    }

    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {
    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {
        variables.onRelease(mX, mY);
    }

    @Override
    public void doScroll(int scroll)
    {
        variables.doScroll(scroll);
    }

    @Override
    public void update(float partial)
    {
        variables.update(partial);
    }

    @Override
    public void copyFrom(Menu menu)
    {
        variables.setSelected(((MenuVariable)menu).getSelectedVariable());
        radioButtons.setSelectedOption(((MenuVariable)menu).radioButtons.getSelectedOption());
        executed = ((MenuVariable)menu).executed;
    }

    public Variable getVariable()
    {
        return getParent().getManager().getVariable(getSelectedVariable());
    }

    public int getSelectedVariable()
    {
        return variables.variable;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        variables.setSelected(nbtTagCompound.getInteger(NBT_VARIABLE));
        radioButtons.setSelectedOption(nbtTagCompound.getByte(NBT_MODE));
        executed = nbtTagCompound.getBoolean(NBT_EXECUTED);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setInteger(NBT_VARIABLE, variables.variable);
        nbtTagCompound.setByte(NBT_MODE, (byte)radioButtons.getSelectedOption());
        nbtTagCompound.setBoolean(NBT_EXECUTED, executed);
    }

    @Override
    public void addErrors(List<String> errors)
    {
        Variable variable = getVariable();
        if (variable == null || !variable.isValid())
        {
            errors.add(Names.NOT_DECLARED_ERROR);
        } else if (isDeclaration() && variable.getDeclaration().getId() != getParent().getId())
        {
            errors.add(Names.ALREADY_DECLARED_ERROR);
        }
    }

    public enum VariableMode
    {
        ADD(Names.ADD),
        SET(Names.SET),
        REMOVE(Names.REMOVE);

        public String name;

        VariableMode(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }
}
