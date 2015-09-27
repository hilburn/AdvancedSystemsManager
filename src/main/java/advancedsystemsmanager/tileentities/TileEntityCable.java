package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.tileentities.IBUDListener;
import advancedsystemsmanager.helpers.BlockHelper;
import advancedsystemsmanager.util.SystemCoord;

public class TileEntityCable extends TileEntityElementBase implements IBUDListener
{
    @Override
    public boolean canUpdate()
    {
        return false;
    }

    @Override
    public void onNeighborBlockChange()
    {
        BlockHelper.updateInventories(new SystemCoord(xCoord, yCoord, zCoord, worldObj));
    }

    @Override
    public void validate()
    {
        super.validate();
        onNeighborBlockChange();
    }
}
