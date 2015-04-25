package advancedsystemsmanager.gui;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import advancedsystemsmanager.util.ConnectionBlock;
import advancedsystemsmanager.util.WorldCoordinate;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;


public class ContainerManager extends ContainerBase
{

    private TileEntityManager manager;

    public ContainerManager(TileEntityManager manager, InventoryPlayer player)
    {
        super(manager, player);
        this.manager = manager;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return entityplayer.getDistanceSq(manager.xCoord, manager.yCoord, manager.zCoord) <= 64;
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
                for (int i = 0; i < oldInventories.size(); i++)
                {
                    TileEntity tileEntity = manager.getConnectedInventories().get(i).getTileEntity();
                    if (oldInventories.get(i).equals(new WorldCoordinate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord)))
                    {
                        hasInventoriesChanged = true;
                        break;
                    }
                }
            }


            if (hasInventoriesChanged)
            {
                oldInventories.clear();
                for (ConnectionBlock connection : manager.getConnectedInventories())
                {
                    oldInventories.add(new WorldCoordinate(connection.getTileEntity().xCoord, connection.getTileEntity().yCoord, connection.getTileEntity().zCoord));
                }
                PacketHandler.sendUpdateInventoryPacket(this);
            }
        }
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
        oldInventories = new ArrayList<WorldCoordinate>();
        for (ConnectionBlock connection : manager.getConnectedInventories())
        {
            oldInventories.add(new WorldCoordinate(connection.getTileEntity().xCoord, connection.getTileEntity().yCoord, connection.getTileEntity().zCoord));
        }
        oldIdIndexToRemove = manager.getRemovedIds().size();
    }


    private TIntObjectHashMap<FlowComponent> oldComponents;
    private List<WorldCoordinate> oldInventories;
    private int oldIdIndexToRemove;


}
