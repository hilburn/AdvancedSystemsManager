package advancedsystemsmanager.api.tileentities;

import advancedsystemsmanager.api.tiletypes.IActivateListener;
import advancedsystemsmanager.network.ASMPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public interface ITileInterfaceProvider extends IActivateListener
{
    Container getContainer(EntityPlayer player);

    @SideOnly(Side.CLIENT)
    GuiScreen getGui(EntityPlayer player);

    boolean writeData(ASMPacket packet);

    boolean readData(ASMPacket packet, EntityPlayer player);
}
