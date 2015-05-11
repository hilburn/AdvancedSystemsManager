package advancedsystemsmanager.api.tileentities;

import advancedsystemsmanager.api.network.INetworkWriter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public interface ITileEntityInterface extends INetworkWriter
{
    public abstract Container getContainer(TileEntity te, InventoryPlayer inv);

    @SideOnly(Side.CLIENT)
    public abstract GuiScreen getGui(TileEntity te, InventoryPlayer inv);

    void readData(ByteBuf buf, EntityPlayer player);
}
