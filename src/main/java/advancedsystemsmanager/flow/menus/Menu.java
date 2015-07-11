package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public abstract class Menu
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
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
    }

    @SideOnly(Side.CLIENT)
    public abstract void onClick(int mX, int mY, int button);

    @SideOnly(Side.CLIENT)
    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {
    }

    @SideOnly(Side.CLIENT)
    public void onRelease(int mX, int mY, int button, boolean isMenuOpen)
    {
    }

    @SideOnly(Side.CLIENT)
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        return false;
    }

    public ASMPacket getWriterForClientComponentPacket(ContainerManager container)
    {
        return PacketHandler.getWriterForClientComponentPacket(container, getParent(), this);
    }

    public FlowComponent getParent()
    {
        return parent;
    }

    public void copyFrom(Menu menu)
    {
    }

    public int getId()
    {
        return id;
    }

    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
    }

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
