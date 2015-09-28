package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.tileentities.ITileElement;
import advancedsystemsmanager.api.tileentities.ITileFactory;
import advancedsystemsmanager.helpers.RemapHelper;
import advancedsystemsmanager.registry.ClusterRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityRemapping extends TileEntity
{
    private NBTTagCompound tagCompound;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        ITileFactory factory = getFactory(tagCompound.getString("id"));
        if (factory != null && factory.getBlock() != null)
        {
            int subtype = getBlockMetadata() & 8;
            if (factory == ClusterRegistry.CAMO)
            {
                subtype = getBlockMetadata();
            }
            worldObj.setBlock(xCoord, yCoord, zCoord, factory.getBlock(), factory.getMetadata(), 3);
            TileEntity te = factory.createTileEntity(worldObj, factory.getMetadata());
            te.readFromNBT(tagCompound);
            ((ITileElement)te).setSubtype(subtype);
            worldObj.removeTileEntity(xCoord, yCoord, zCoord);
            worldObj.getChunkFromBlockCoords(xCoord, zCoord).addTileEntity(te);
        } else
        {
            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
            worldObj.removeTileEntity(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        this.tagCompound = tag;
    }


    private ITileFactory getFactory(String name)
    {
        return ClusterRegistry.get(RemapHelper.getNewTile(name));
    }
}
