package advancedsystemsmanager.tileentities;

public abstract class TileEntityClusterElement extends TileEntityElementBase
{


    public void markBlockForUpdate()
    {
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void markBlockForRenderUpdate()
    {
        this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
    }
}
