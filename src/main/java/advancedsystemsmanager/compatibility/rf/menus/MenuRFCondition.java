package advancedsystemsmanager.compatibility.rf.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.CheckBox;
import advancedsystemsmanager.flow.elements.CheckBoxList;
import advancedsystemsmanager.client.gui.GuiManager;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class MenuRFCondition extends MenuRFAmount
{
    public CheckBoxList checkBoxes = new CheckBoxList();
    public boolean triggerBelow;

    public MenuRFCondition(FlowComponent parent)
    {
        super(parent);
        this.checkBoxes.addCheckBox(new CheckBox(getParent(), Names.BELOW, 5, 50)
        {
            public void setValue(boolean val)
            {
                MenuRFCondition.this.triggerBelow = val;
            }

            public boolean getValue()
            {
                return MenuRFCondition.this.triggerBelow;
            }
        });
    }

    @Override
    public String getName()
    {
        return Names.RF_CONDITION_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        gui.drawSplitString(Names.RF_CONDITION_INFO, 5, 5, 110, 0.7F, 4210752);
        this.checkBoxes.draw(gui, mX, mY);
        this.textBoxes.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onClick(int mX, int mY, int button)
    {
        super.onClick(mX, mY, button);
        this.checkBoxes.onClick(mX, mY);
    }

    @Override
    public void copyFrom(Menu menu)
    {
        super.copyFrom(menu);
        this.triggerBelow = ((MenuRFCondition)menu).triggerBelow;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        super.readFromNBT(nbtTagCompound, pickup);
        this.triggerBelow = nbtTagCompound.getBoolean("Inverted");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        super.writeToNBT(nbtTagCompound, pickup);
        nbtTagCompound.setBoolean("Inverted", this.triggerBelow);
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (textBox.getNumber() == 0 && triggerBelow) errors.add(Names.RF_CONDITION_ERROR);
    }

    public boolean isLessThan()
    {
        return this.triggerBelow;
    }

}
