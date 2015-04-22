package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.INetworkReader;
import advancedsystemsmanager.network.PacketHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public abstract class Menu implements INetworkReader
{


    public FlowComponent parent;
    public int id;

    public Menu(FlowComponent parent)
    {
        this.parent = parent;
        id = parent.getMenus().size();
    }

    public abstract String getName();

    @SideOnly(Side.CLIENT)
    public abstract void draw(GuiManager gui, int mX, int mY);

    @SideOnly(Side.CLIENT)
    public abstract void drawMouseOver(GuiManager gui, int mX, int mY);

    public abstract void onClick(int mX, int mY, int button);

    public abstract void onDrag(int mX, int mY, boolean isMenuOpen);

    public abstract void onRelease(int mX, int mY, boolean isMenuOpen);

    @SideOnly(Side.CLIENT)
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        return false;
    }

    public FlowComponent getParent()
    {
        return parent;
    }

    public abstract void writeData(DataWriter dw);

    public abstract void readData(DataReader dr);

    public DataWriter getWriterForServerComponentPacket()
    {
        return PacketHandler.getWriterForServerComponentPacket(getParent(), this);
    }

    public DataWriter getWriterForClientComponentPacket(ContainerManager container)
    {
        return PacketHandler.getWriterForClientComponentPacket(container, getParent(), this);
    }

    public abstract void copyFrom(Menu menu);

    public abstract void refreshData(ContainerManager container, Menu newData);

    public int getId()
    {
        return id;
    }

    public abstract void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup);

    public abstract void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup);

    public void addErrors(List<String> errors)
    {
    }

    public boolean isVisible()
    {
        return true;
    }

    public void update(float partial)
    {
    }

    public void doScroll(int scroll)
    {
    }

    public void onGuiClosed()
    {
    }
}
