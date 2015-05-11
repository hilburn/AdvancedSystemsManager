package advancedsystemsmanager.gui;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.registry.ItemRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import advancedsystemsmanager.util.SystemCoord;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

public class ContainerManager extends ContainerBase
{
    public TileEntityManager manager;
    private TIntObjectHashMap<FlowComponent> oldComponents;
    private TLongObjectHashMap<SystemCoord> oldInventories;
    private int oldIdIndexToRemove;

    public ContainerManager(TileEntityManager manager, InventoryPlayer player)
    {
        super(manager, player);
        this.manager = manager;
    }

    @Override
    public void addCraftingToCrafters(ICrafting player)
    {
        super.addCraftingToCrafters(player);

        PacketHandler.sendAllData(this, player, manager);
        oldComponents = new TIntObjectHashMap<FlowComponent>();
        for (FlowComponent component : manager.getFlowItems())
        {
            oldComponents.put(component.getId(), component.copy());
        }
        manager.updateInventories();
        oldInventories = new TLongObjectHashMap<SystemCoord>(manager.getNetwork());
        oldIdIndexToRemove = manager.getRemovedIds().size();
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if (oldComponents != null)
        {
            if (oldIdIndexToRemove < manager.getRemovedIds().size())
            {
                int idToRemove = manager.getRemovedIds().get(oldIdIndexToRemove);
                oldIdIndexToRemove++;
                manager.removeFlowComponent(idToRemove);
                PacketHandler.sendRemovalPacket(this, idToRemove);
            }


            for (FlowComponent component : manager.getFlowItems())
            {
                if (!oldComponents.containsKey(component.getId()))
                {
                    PacketHandler.sendNewFlowComponent(this, component);
                    oldComponents.put(component.getId(), component.copy());
                } else
                {
                    oldComponents.get(component.getId()).refreshData(this, component);
                }
            }

            boolean hasInventoriesChanged = oldInventories.size() != manager.getConnectedInventories().size();

            if (!hasInventoriesChanged)
            {
                for (long key : oldInventories.keys())
                {
                    if (!manager.hasInventory(key))
                    {
                        hasInventoriesChanged = true;
                        break;
                    }
                }
            }


            if (hasInventoriesChanged)
            {
                oldInventories.clear();
                oldInventories.putAll(manager.getNetwork());
                PacketHandler.sendUpdateInventoryPacket(this);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return entityplayer.getDistanceSq(manager.xCoord, manager.yCoord, manager.zCoord) <= 64 || (entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().getItem() == ItemRegistry.remoteAccessor);
    }
}
