package advancedsystemsmanager.api.tileentities;

import advancedsystemsmanager.api.network.IPacketWriter;
import advancedsystemsmanager.network.ASMPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public interface ITileEntityInterface extends IPacketWriter
{
    public abstract Container getContainer(EntityPlayer player);

    @SideOnly(Side.CLIENT)
    public abstract GuiScreen getGui(EntityPlayer player);

    void readData(ASMPacket buf, EntityPlayer player);
}
