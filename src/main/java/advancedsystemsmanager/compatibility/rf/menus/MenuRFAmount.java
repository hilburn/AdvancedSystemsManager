package advancedsystemsmanager.compatibility.rf.menus;

import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.TextBoxNumberList;
import advancedsystemsmanager.flow.elements.WideNumberBox;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public class MenuRFAmount extends Menu
{
    public TextBoxNumberList textBoxes;
    public WideNumberBox textBox;

    public MenuRFAmount(FlowComponent parent)
    {
        super(parent);
        this.textBoxes = new TextBoxNumberList();
        this.textBoxes.addTextBox(this.textBox = new WideNumberBox(getParent(), 5, 30, 31));
        this.textBox.setNumber(0);
    }

    @Override
    public String getName()
    {
        return Names.RF_AMOUNT;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiBase gui, int mX, int mY)
    {
        gui.drawSplitString(Names.RF_CONDITION_INFO, 5, 5, 110, 0.7F, 4210752);
        this.textBoxes.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onClick(int mX, int mY, int button)
    {
        this.textBoxes.onClick(mX, mY, button);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiBase gui, char c, int k)
    {
        return this.textBoxes.onKeyStroke(gui, c, k);
    }

    @Override
    public void copyFrom(Menu menu)
    {
        MenuRFAmount menuStrength = (MenuRFAmount)menu;
        this.textBox.setNumber(menuStrength.textBox.getNumber());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        this.textBox.setNumber(nbtTagCompound.getInteger("textBox"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setInteger("textBox", this.textBox.getNumber());
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    public int getAmount()
    {
        return this.textBox.getNumber();
    }
}
