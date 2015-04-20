package advancedfactorymanager.components;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import stevesaddons.asm.StevesHooks;
import advancedfactorymanager.interfaces.ContainerManager;
import advancedfactorymanager.interfaces.GuiManager;
import advancedfactorymanager.network.DataBitHelper;
import advancedfactorymanager.network.DataReader;
import advancedfactorymanager.network.DataWriter;
import advancedfactorymanager.network.PacketHandler;

import java.util.EnumSet;


public abstract class ComponentMenuTriggered extends ComponentMenu
{

    protected TextBoxNumberList textBoxes = new TextBoxNumberList();

    private static final String NBT_DELAY = "Delay";
    private static final String NBT_COUNTDOWN = "Counter";
    protected int counter;

    public ComponentMenuTriggered(FlowComponent parent) {
        super(parent);
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiManager gui, int mX, int mY) {
        this.textBoxes.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
    }

    public void onClick(int mX, int mY, int button) {
        this.textBoxes.onClick(mX, mY, button);
    }

    @SideOnly(Side.CLIENT)
    public boolean onKeyStroke(GuiManager gui, char c, int k) {
        return this.textBoxes.onKeyStroke(gui, c, k);
    }

    public void onDrag(int mX, int mY, boolean isMenuOpen) {
    }

    public void onRelease(int mX, int mY, boolean isMenuOpen) {
    }

    public void writeData(DataWriter dw) {
        int val = this.getDelay();
        if(val < getMin()) {
            val = getMin();
        }
        dw.writeData(val, DataBitHelper.MENU_INTERVAL);
    }

    public void readData(DataReader dr) {
        this.setDelay(dr.readData(DataBitHelper.MENU_INTERVAL));
    }

    public void copyFrom(ComponentMenu menu) {
        this.setDelay(((ComponentMenuTriggered)menu).getDelay());
    }

    public void refreshData(ContainerManager container, ComponentMenu newData) {
        ComponentMenuTriggered newDataTriggered = (ComponentMenuTriggered)newData;
        if(newDataTriggered.getDelay() != this.getDelay()) {
            copyFrom(newData);
            DataWriter dw = this.getWriterForClientComponentPacket(container);
            dw.writeData(getDelay(), DataBitHelper.MENU_INTERVAL);
            PacketHandler.sendDataToListeningClients(container, dw);
        }
    }

    public void readNetworkComponent(DataReader dr) {
        readData(dr);
    }

    public abstract int getDelay();

    public abstract void setDelay(int val);

    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup) {
        this.setDelay(nbtTagCompound.getInteger(NBT_DELAY));
        counter = nbtTagCompound.getInteger(NBT_COUNTDOWN);
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup) {
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

    protected void act()
    {
        getParent().getManager().activateTrigger(getParent(), getConnectionSets());
        resetCounter();
    }

    protected void resetCounter()
    {
        counter = 0;
    }

    protected abstract EnumSet<ConnectionOption> getConnectionSets();
}
