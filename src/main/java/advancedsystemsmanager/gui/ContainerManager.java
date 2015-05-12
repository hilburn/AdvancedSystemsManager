package advancedsystemsmanager.gui;

import advancedsystemsmanager.network.MessageHandler;
import advancedsystemsmanager.network.message.ContainerMessage;
import advancedsystemsmanager.registry.ItemRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

public class ContainerManager extends ContainerBase
{
    public TileEntityManager manager;

    public ContainerManager(TileEntityManager manager, InventoryPlayer player)
    {
        super(manager, player);
        this.manager = manager;
    }

    @Override
    public void addCraftingToCrafters(ICrafting player)
    {
        super.addCraftingToCrafters(player);

        if (player instanceof EntityPlayerMP && !manager.getWorldObj().isRemote)
            MessageHandler.INSTANCE.sendTo(new ContainerMessage(manager), (EntityPlayerMP)player);
    }

    @Override
    public void detectAndSendChanges()
    {

    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return entityplayer.getDistanceSq(manager.xCoord, manager.yCoord, manager.zCoord) <= 64 || (entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().getItem() == ItemRegistry.remoteAccessor);
    }
}
