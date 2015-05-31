package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.ScrollVariable;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class MenuVariableLoop extends Menu
{
    public static final int DISPLAY_X = 45;
    public static final int DISPLAY_Y_TOP = 5;
    public static final int DISPLAY_Y_BOT = 25;
    public static final String NBT_ELEMENT = "Element";
    private ScrollVariable variables;

    public MenuVariableLoop(FlowComponent parent)
    {
        super(parent);
        variables = new ScrollVariable(parent);
    }

    @Override
    public String getName()
    {
        return Names.LOOP_VARIABLE_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
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
        variables.onClick(mX, mY, button);
    }

    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {
    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {
        if (isMenuOpen)
        {
            variables.onRelease(mX, mY);
        }
    }

    @Override
    public void copyFrom(Menu menu)
    {
        variables.selected = ((MenuVariableLoop)menu).variables.selected;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        variables.selected = nbtTagCompound.getInteger(NBT_ELEMENT);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setInteger(NBT_ELEMENT, variables.selected);
    }

    @Override
    public void addErrors(List<String> errors)
    {
        Variable variable = getVariable();
        if (variable == null || !getVariable().isValid())
        {
            errors.add(Names.LIST_NOT_DECLARED);
        }
    }

    public Variable getVariable()
    {
        return getParent().getManager().getVariable(variables.selected);
    }

}
