package advancedsystemsmanager.gui;

import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.registry.ItemRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

public class ContainerManager extends ContainerBase<TileEntityManager>
{
    @SideOnly(Side.CLIENT)
    protected GuiManager gui;

    public ContainerManager(TileEntityManager manager, InventoryPlayer player)
    {
        super(manager, player);
    }

    @Override
    public void addCraftingToCrafters(ICrafting player)
    {
        super.addCraftingToCrafters(player);

        if (player instanceof EntityPlayerMP && !te.getWorldObj().isRemote)
        {
            PacketHandler.sendAllData(this, player, te);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void detectAndSendChanges()
    {
        //TODO: Whatttt hi
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return super.canInteractWith(entityplayer) || entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().getItem() == ItemRegistry.remoteAccessor;
    }
}
