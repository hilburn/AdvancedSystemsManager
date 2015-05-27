package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.tileentities.IClusterElement;
import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.util.ClusterMethodRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.EnumSet;


public abstract class TileEntityClusterElement extends TileEntity implements IClusterTile
{
    private IClusterElement clusterElement;
    private boolean isPartOfCluster;
    private int meta;

    protected TileEntityClusterElement()
    {
        clusterElement = ClusterRegistry.get(this);
    }

    @Override
    public ItemStack getItemStackFromBlock()
    {
        return clusterElement.getItemStack(getMetadata());
    }

    @Override
    public boolean isPartOfCluster()
    {
        return isPartOfCluster;
    }

    @Override
    public void setPartOfCluster(boolean partOfCluster)
    {
        isPartOfCluster = partOfCluster;
    }

    @Override
    public void setMetaData(int meta)
    {
        if (isPartOfCluster)
        {
            this.meta = meta;
        } else
        {
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta, 2);
        }
    }

    @Override
    public final void readFromNBT(NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);
        readContentFromNBT(tagCompound);
    }

    @Override
    public final void writeToNBT(NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);
        writeContentToNBT(tagCompound);
    }

    @Override
    public int getMetadata()
    {
        if (isPartOfCluster)
        {
            return meta;
        } else
        {
            return super.getBlockMetadata();
        }
    }

    public void markBlockForUpdate()
    {
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void writeContentToNBT(NBTTagCompound tagCompound)
    {
    }

    @Override
    public void readContentFromNBT(NBTTagCompound tagCompound)
    {
    }

    public abstract EnumSet<ClusterMethodRegistration> getRegistrations();
}
