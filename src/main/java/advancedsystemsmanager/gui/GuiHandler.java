package advancedsystemsmanager.gui;

import advancedsystemsmanager.api.tileentities.ITileEntityInterface;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
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
        if (ID > 0)
        {
            TileEntity te = world.getTileEntity(x, y, z);

            if (te != null && te instanceof ITileEntityInterface)
            {
                return ((ITileEntityInterface)te).getContainer(player);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {

        if (ID == 0) return new GuiLabeler(player.getCurrentEquippedItem(), player);
        else
        {
            TileEntity te = world.getTileEntity(x, y, z);

            if (te != null && te instanceof ITileEntityInterface)
            {
                return ((ITileEntityInterface)te).getGui(player);
            }
        }
        return null;
    }

}
