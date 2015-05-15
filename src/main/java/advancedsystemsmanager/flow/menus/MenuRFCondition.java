package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.CheckBox;
import advancedsystemsmanager.flow.elements.CheckBoxList;
import advancedsystemsmanager.flow.elements.WideNumberBox;
import advancedsystemsmanager.flow.elements.WideNumberBoxList;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class MenuRFCondition extends Menu
{
    public CheckBoxList checkBoxes = new CheckBoxList();
    public WideNumberBoxList textBoxes;
    public WideNumberBox textBox;
    public boolean triggerBelow;

    public MenuRFCondition(FlowComponent parent)
    {
        super(parent);
        this.checkBoxes.addCheckBox(new CheckBox(Names.BELOW, 5, 50)
        {
            public void setValue(boolean val)
            {
                MenuRFCondition.this.triggerBelow = val;
            }

            public boolean getValue()
            {
                return MenuRFCondition.this.triggerBelow;
            }

            public void onUpdate()
            {
                MenuRFCondition.this.needSync = true;
            }
        });
        this.textBoxes = new WideNumberBoxList();
        this.textBoxes.addTextBox(this.textBox = new WideNumberBox(5, 30, 31)
        {
            @Override
            public void onNumberChanged()
            {
                MenuRFCondition.this.needSync = true;
            }
        });
        this.textBox.setNumber(0);
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
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onClick(int mX, int mY, int button)
    {
        this.checkBoxes.onClick(mX, mY);
        this.textBoxes.onClick(mX, mY, button);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        return this.textBoxes.onKeyStroke(gui, c, k);
    }

    @Override
    public void copyFrom(Menu menu)
    {
        MenuRFCondition menuStrength = (MenuRFCondition)menu;
        this.textBox.setNumber(menuStrength.textBox.getNumber());
        this.triggerBelow = menuStrength.triggerBelow;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        this.textBox.setNumber(nbtTagCompound.getInteger("textBox"));
        this.triggerBelow = nbtTagCompound.getBoolean("Inverted");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setInteger("textBox", this.textBox.getNumber());
        nbtTagCompound.setBoolean("Inverted", this.triggerBelow);
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (textBox.getNumber() == 0 && triggerBelow) errors.add(Names.RF_CONDITION_ERROR);
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }


    public boolean isLessThan()
    {
        return this.triggerBelow;
    }

    public int getAmount()
    {
        return this.textBox.getNumber();
    }

}
