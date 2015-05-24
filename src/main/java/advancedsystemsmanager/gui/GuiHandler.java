package advancedsystemsmanager.gui;

import advancedsystemsmanager.api.items.IItemInterfaceProvider;
import advancedsystemsmanager.api.tileentities.ITileInterfaceProvider;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


public class GuiHandler implements IGuiHandler
{
    public GuiHandler()
    {

    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case 0:
                ItemStack stack = player.getCurrentEquippedItem();
                if (stack != null)
                {
                    Item item = stack.getItem();
                    if (item instanceof IItemInterfaceProvider)
                    {
                        return ((IItemInterfaceProvider)item).getContainer(stack, player);
                    }
                }
                break;
            case 1:
                TileEntity te = world.getTileEntity(x, y, z);

                if (te != null && te instanceof ITileInterfaceProvider)
                {
                    return ((ITileInterfaceProvider)te).getContainer(player);
                }
                break;
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {

        switch (ID)
        {
            case 0:
                ItemStack stack = player.getCurrentEquippedItem();
                if (stack != null)
                {
                    Item item = stack.getItem();
                    if (item instanceof IItemInterfaceProvider)
                    {
                        return ((IItemInterfaceProvider)item).getGui(stack, player);
                    }
                }
                break;
            case 1:
                TileEntity te = world.getTileEntity(x, y, z);

                if (te != null && te instanceof ITileInterfaceProvider)
                {
                    return ((ITileInterfaceProvider)te).getGui(player);
                }
                break;
        }
        return null;
    }



}
