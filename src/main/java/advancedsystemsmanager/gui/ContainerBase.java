package advancedsystemsmanager.gui;

import advancedsystemsmanager.api.network.INetworkSync;
import advancedsystemsmanager.api.tileentities.ITileEntityInterface;
import advancedsystemsmanager.network.MessageHandler;
import advancedsystemsmanager.network.message.IBufferMessage;
import advancedsystemsmanager.network.message.SyncMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.tileentity.TileEntity;

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
        if (element != null && element.needsSync() && (crafters.size() > 1 || loseFocus))
        {
            MessageHandler.INSTANCE.sendToServer(new SyncMessage(element));
        }
    }

    public void updateServer(IBufferMessage message, EntityPlayerMP player)
    {
        for (Object crafter : crafters)
        {
            if (crafter instanceof EntityPlayerMP)
            {
                MessageHandler.INSTANCE.sendTo(message, player);
            }
        }
        te.readData(message.getBuffer(), player);
    }

    public void updateClient(IBufferMessage message, EntityPlayer player)
    {
        te.readData(message.getBuffer(), player);
    }

    public ITileEntityInterface getTileEntity()
    {
        return te;
    }

    public List<ICrafting> getCrafters()
    {
        return crafters;
    }

    @SideOnly(Side.CLIENT)
    public void sendFinalUpdate(INetworkSync element)
    {
        MessageHandler.INSTANCE.sendToServer(new SyncMessage((TileEntity)te, element));
    }
}
