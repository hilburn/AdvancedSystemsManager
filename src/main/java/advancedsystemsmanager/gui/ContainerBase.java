package advancedsystemsmanager.gui;

import advancedsystemsmanager.api.network.INetworkSync;
import advancedsystemsmanager.api.tileentities.ITileEntityInterface;
import advancedsystemsmanager.network.MessageHandler;
import advancedsystemsmanager.network.message.FinalSyncMessage;
import advancedsystemsmanager.network.message.IBufferMessage;
import advancedsystemsmanager.network.message.SyncMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.List;


public abstract class ContainerBase<T extends TileEntity & ITileEntityInterface> extends Container
{
    protected T te;
    private InventoryPlayer player;
    int playerInventoryEnd;

    protected ContainerBase(T te, InventoryPlayer player)
    {
        this.te = te;
        this.player = player;
    }

    protected void bindPlayerInventory(int x, int y)
    {
        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(player, i, x + i * 18, y + 58));
        }

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(player, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }
        playerInventoryEnd = inventorySlots.size();
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
                MessageHandler.INSTANCE.sendTo(message, (EntityPlayerMP)crafter);
            }
        }
        te.readData(message.getBuffer(), player);
    }

    public void updateClient(IBufferMessage message, EntityPlayer player)
    {
        te.readData(message.getBuffer(), player);
    }

    public T getTileEntity()
    {
        return te;
    }

    public ITileEntityInterface getInterface()
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
        if (element != null)
            MessageHandler.INSTANCE.sendToServer(new FinalSyncMessage(te, element));
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return entityplayer.getDistanceSq(te.xCoord, te.yCoord, te.zCoord) <= 64;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slot)
    {
        Slot slotObject = (Slot) inventorySlots.get(slot);
        ItemStack stack = null;

        if (slotObject != null && slotObject.getHasStack())
        {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            if (slot < playerInventoryEnd)
            {
                if (!mergeItemStack(stackInSlot, playerInventoryEnd, inventorySlots.size(), false))
                {
                    return null;
                }
            } else
            {
                if (!mergeItemStack(stackInSlot, 0, playerInventoryEnd, false))
                {
                    return null;
                }
            }
            if (stackInSlot.stackSize == 0)
            {
                slotObject.putStack(null);
            } else
            {
                slotObject.onSlotChanged();
            }
            if (stackInSlot.stackSize == stack.stackSize)
            {
                return null;
            }
            slotObject.onPickupFromSlot(entityPlayer, stackInSlot);
        }
        return stack;
    }


}
