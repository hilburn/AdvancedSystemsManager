package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.api.network.INetworkSync;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public abstract class Menu implements INetworkSync
{
    public FlowComponent parent;
    boolean needsSync;
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

    public DataWriter getWriterForServerComponentPacket()
    {
        return PacketHandler.getWriterForServerComponentPacket(getParent(), this);
    }

    public FlowComponent getParent()
    {
        return parent;
    }

    public DataWriter getWriterForClientComponentPacket(ContainerManager container)
    {
        return PacketHandler.getWriterForClientComponentPacket(container, getParent(), this);
    }

    public abstract void copyFrom(Menu menu);

    public int getId()
    {
        return id;
    }

    public abstract void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup);

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

    @Override
    public void readData(ByteBuf buf)
    {
        readFromNBT(ByteBufUtils.readTag(buf), false);
    }

    @Override
    public void writeNetworkComponent(ByteBuf buf)
    {
        buf.writeInt(parent.getId());
        buf.writeBoolean(true);
        buf.writeByte(id);
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound, false);
        ByteBufUtils.writeTag(buf, tagCompound);
    }

    @Override
    public boolean needsSync()
    {
        return needsSync;
    }

    @Override
    public void setSynced()
    {
        needsSync = false;
    }
}
