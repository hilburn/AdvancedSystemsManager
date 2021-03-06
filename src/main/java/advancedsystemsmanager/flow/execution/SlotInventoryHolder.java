package advancedsystemsmanager.flow.execution;


import advancedsystemsmanager.api.tileentities.IRedstoneNode;
import advancedsystemsmanager.api.tileentities.ITriggerNode;
import advancedsystemsmanager.tileentities.*;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.HashMap;
import java.util.Map;

public class SlotInventoryHolder
{
    public TileEntity inventory;
    public Map<Integer, SlotSideTarget> validSlots;
    public int sharedOption;
    public int id;

    public SlotInventoryHolder(int id, TileEntity inventory, int sharedOption)
    {
        this.id = id;
        this.inventory = inventory;
        this.sharedOption = sharedOption;
    }

    public int getId()
    {
        return id;
    }

    public IInventory getInventory()
    {
        return (IInventory)inventory;
    }

    public IFluidHandler getTank()
    {
        return (IFluidHandler)inventory;
    }

    public TileEntityEmitter getEmitter()
    {
        return (TileEntityEmitter)inventory;
    }

    public IRedstoneNode getNode()
    {
        return (IRedstoneNode)inventory;
    }

    public TileEntityReceiver getReceiver()
    {
        return (TileEntityReceiver)inventory;
    }

    public TileEntityBUD getBUD()
    {
        return (TileEntityBUD)inventory;
    }

    public TileEntityCamouflage getCamouflage()
    {
        return (TileEntityCamouflage)inventory;
    }

    public TileEntitySignUpdater getSign()
    {
        return (TileEntitySignUpdater)inventory;
    }

    public Map<Integer, SlotSideTarget> getValidSlots()
    {
        if (validSlots == null)
        {
            validSlots = new HashMap<Integer, SlotSideTarget>();
        }

        return validSlots;
    }

    public boolean isShared()
    {
        return sharedOption == 0;
    }

    public int getSharedOption()
    {
        return sharedOption;
    }

    @Override
    public int hashCode()
    {
        return inventory.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SlotInventoryHolder that = (SlotInventoryHolder)o;

        return inventory.xCoord == that.inventory.xCoord && inventory.yCoord == that.inventory.yCoord && inventory.zCoord == that.inventory.yCoord;
    }

    public TileEntity getTile()
    {
        return inventory;
    }

    public ITriggerNode getTrigger()
    {
        return (ITriggerNode)inventory;
    }
}
