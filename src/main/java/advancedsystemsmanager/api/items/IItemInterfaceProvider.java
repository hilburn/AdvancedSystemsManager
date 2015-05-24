package advancedsystemsmanager.api.items;

import advancedsystemsmanager.network.ASMPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public interface IItemInterfaceProvider
{
    public abstract Container getContainer(ItemStack stack, EntityPlayer player);

    @SideOnly(Side.CLIENT)
    public abstract GuiScreen getGui(ItemStack stack, EntityPlayer player);

    void readData(ItemStack stack, ASMPacket buf, EntityPlayer player);
}
