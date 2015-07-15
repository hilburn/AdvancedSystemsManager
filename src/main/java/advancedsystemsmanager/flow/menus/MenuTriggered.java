package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.TextBoxNumberList;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.registry.ConnectionOption;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.EnumSet;

public abstract class MenuTriggered extends Menu
{

    public static final String NBT_DELAY = "Delay";
    public static final String NBT_COUNTDOWN = "Counter";
    public TextBoxNumberList textBoxes = new TextBoxNumberList();
    public int counter;

    public MenuTriggered(FlowComponent parent)
    {
        super(parent);
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiManager gui, int mX, int mY)
    {
        this.textBoxes.draw(gui, mX, mY);
    }

    public void onClick(int mX, int mY, int button)
    {
        this.textBoxes.onClick(mX, mY, button);
    }

    @SideOnly(Side.CLIENT)
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        return this.textBoxes.onKeyStroke(gui, c, k);
    }

    public void copyFrom(Menu menu)
    {
        this.setDelay(((MenuTriggered)menu).getDelay());
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setInteger(NBT_DELAY, this.getDelay());
        nbtTagCompound.setInteger(NBT_COUNTDOWN, this.counter);
    }

    public void writeData(ASMPacket dw)
    {
        int val = this.getDelay();
        if (val < getMin())
        {
            val = getMin();
        }
        dw.writeVarIntToBuffer(val);
    }

    public abstract int getDelay();

    public abstract void setDelay(int val);

    public int getMin()
    {
        return 1;
    }

    public void readData(ASMPacket dr)
    {
        this.setDelay(dr.readVarIntFromBuffer());
    }

    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        this.setDelay(nbtTagCompound.getInteger(NBT_DELAY));
        counter = nbtTagCompound.getInteger(NBT_COUNTDOWN);
    }

    public void setCountdown()
    {
        if (isVisible())
        {
            counter = 0;
            getParent().getManager().addQuickTrigger(getParent());
        }
    }

    public void tick()
    {
        if (isVisible() && ++counter >= getDelay())
        {
            act();
        }
    }

    public void act()
    {
        getParent().getManager().activateTrigger(getParent(), getConnectionSets());
        resetCounter();
    }

    public abstract EnumSet<ConnectionOption> getConnectionSets();

    public void resetCounter()
    {
        counter = 0;
    }

    public boolean remove()
    {
        return false;
    }
}
