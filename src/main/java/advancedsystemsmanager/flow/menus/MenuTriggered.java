package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.TextBoxNumberList;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.util.StevesHooks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.EnumSet;


public abstract class MenuTriggered extends Menu
{

    public TextBoxNumberList textBoxes = new TextBoxNumberList();

    public static final String NBT_DELAY = "Delay";
    public static final String NBT_COUNTDOWN = "Counter";
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

    @SideOnly(Side.CLIENT)
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
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

    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {
    }

    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {
    }

    public void writeData(DataWriter dw)
    {
        int val = this.getDelay();
        if (val < getMin())
        {
            val = getMin();
        }
        dw.writeData(val, DataBitHelper.MENU_INTERVAL);
    }

    public void readData(DataReader dr)
    {
        this.setDelay(dr.readData(DataBitHelper.MENU_INTERVAL));
    }

    public void copyFrom(Menu menu)
    {
        this.setDelay(((MenuTriggered)menu).getDelay());
    }

    public void refreshData(ContainerManager container, Menu newData)
    {
        MenuTriggered newDataTriggered = (MenuTriggered)newData;
        if (newDataTriggered.getDelay() != this.getDelay())
        {
            copyFrom(newData);
            DataWriter dw = this.getWriterForClientComponentPacket(container);
            dw.writeData(getDelay(), DataBitHelper.MENU_INTERVAL);
            PacketHandler.sendDataToListeningClients(container, dw);
        }
    }

    public void readNetworkComponent(DataReader dr)
    {
        readData(dr);
    }

    public abstract int getDelay();

    public abstract void setDelay(int val);

    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
    {
        this.setDelay(nbtTagCompound.getInteger(NBT_DELAY));
        counter = nbtTagCompound.getInteger(NBT_COUNTDOWN);
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setInteger(NBT_DELAY, this.getDelay());
        nbtTagCompound.setInteger(NBT_COUNTDOWN, this.counter);
    }

    public int getMin()
    {
        return 1;
    }

    public void setCountdown()
    {
        if (isVisible())
        {
            counter = 0;
            StevesHooks.registerTicker(getParent(), this);
        }
    }

    public void tick()
    {
        if (isVisible() && ++counter >= getDelay())
        {
            act();
        }
    }

    public boolean remove()
    {
        return false;
    }

    public void act()
    {
        getParent().getManager().activateTrigger(getParent(), getConnectionSets());
        resetCounter();
    }

    public void resetCounter()
    {
        counter = 0;
    }

    public abstract EnumSet<ConnectionOption> getConnectionSets();
}
