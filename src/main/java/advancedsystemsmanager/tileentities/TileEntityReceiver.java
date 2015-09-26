package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.tileentities.IRedstoneReceiver;
import advancedsystemsmanager.api.tileentities.ISystemListener;
import advancedsystemsmanager.api.tileentities.ITriggerNode;
import advancedsystemsmanager.api.tiletypes.IBUDListener;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import advancedsystemsmanager.util.ClusterMethodRegistration;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


public class TileEntityReceiver extends TileEntityClusterElement implements IRedstoneReceiver, ISystemListener, ITriggerNode, IBUDListener
{
    private static final String NBT_SIDES = "Sides";
    private List<TileEntityManager> managerList = new ArrayList<TileEntityManager>();
    private int[] oldPowered = new int[ForgeDirection.VALID_DIRECTIONS.length];
    private int[] isPowered = new int[ForgeDirection.VALID_DIRECTIONS.length];

    @Override
    public void added(TileEntityManager owner)
    {
        if (!managerList.contains(owner))
        {
            managerList.add(owner);
        }
    }

    @Override
    public void removed(TileEntityManager owner)
    {
        managerList.remove(owner);
    }

    public void triggerRedstone()
    {
        isPowered = new int[isPowered.length];
        boolean change = false;
        for (int i = 0; i < isPowered.length; i++)
        {
            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[i];
            isPowered[i] = worldObj.getIndirectPowerLevelTo(direction.offsetX + this.xCoord, direction.offsetY + this.yCoord, direction.offsetZ + this.zCoord, direction.ordinal());
            if (isPowered[i] == oldPowered[i]) change = true;
        }

        if (change)
        {
            for (int i = managerList.size() - 1; i >= 0; i--)
            {
                managerList.get(i).triggerRedstone(this);
            }
            oldPowered = isPowered;
        }
    }

    @Override
    public int[] getPower()
    {
        return isPowered;
    }

    @Override
    public void writeContentToNBT(NBTTagCompound tag)
    {
        super.writeContentToNBT(tag);
        byte[] sides = new byte[isPowered.length];
        for (int i = 0; i < sides.length; i++)
        {
            sides[i] = (byte)isPowered[i];
        }
        tag.setByteArray(NBT_SIDES, sides);
    }

    @Override
    public void readContentFromNBT(NBTTagCompound nbtTagCompound)
    {
        byte[] sides = nbtTagCompound.getByteArray(NBT_SIDES);
        int[] powered = new int[ForgeDirection.VALID_DIRECTIONS.length];
        for (int i = 0; i < sides.length; i++)
        {
            powered[i] = sides[i];

        }
        oldPowered = isPowered = powered;
    }

    @Override
    public int[] getData()
    {
        return isPowered;
    }

    @Override
    public int[] getOldData()
    {
        return oldPowered;
    }

    @Override
    public void onNeighborBlockChange()
    {
        triggerRedstone();
    }
}
