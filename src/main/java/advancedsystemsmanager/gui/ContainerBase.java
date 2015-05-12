package advancedsystemsmanager.gui;

import advancedsystemsmanager.api.network.INetworkSync;
import advancedsystemsmanager.api.tileentities.ITileEntityInterface;
import advancedsystemsmanager.network.MessageHandler;
import advancedsystemsmanager.network.message.SyncMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;

import java.util.List;


public abstract class ContainerBase extends Container
{
    private ITileEntityInterface te;
    private InventoryPlayer player;


    protected ContainerBase(ITileEntityInterface te, InventoryPlayer player)
    {
        this.te = te;
        this.player = player;
    }

    public void syncNetworkElement(INetworkSync element, boolean loseFocus)
    {
        if (element.needsSync() && (crafters.size() > 1 || loseFocus))
        {
            MessageHandler.INSTANCE.sendToServer(new SyncMessage(element));
        }
    }

    public void updateServer(SyncMessage message, EntityPlayerMP player)
    {
        for (Object crafter : crafters)
        {
            if (crafter instanceof EntityPlayerMP)
            {
                if (!crafter.equals(player)) MessageHandler.INSTANCE.sendTo(message, player);
            }
        }
        te.readData(message.buf, player);
    }

    public void updateClient(SyncMessage message)
    {

    }

    public ITileEntityInterface getTileEntity()
    {
        return te;
    }

    public List<ICrafting> getCrafters()
    {
        return crafters;
    }
}
