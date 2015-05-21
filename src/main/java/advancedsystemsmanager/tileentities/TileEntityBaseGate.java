package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.util.ClusterMethodRegistration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;

public abstract class TileEntityBaseGate extends TileEntityClusterElement implements IPacketBlock
{

    protected boolean missingPlaceDirection;
    protected boolean hasUpdatedData;
    protected static final int[] ROTATION_SIDE_MAPPING = {0, 0, 0, 2, 3, 1};
    protected static final String NBT_DIRECTION = "Direction";
    protected static final int UPDATE_BUFFER_DISTANCE = 5;
    protected int placeDirection;

    @Override
    public void updateEntity()
    {
        if (missingPlaceDirection)
        {
            setPlaceDirection(getBlockMetadata());
            missingPlaceDirection = false;
        }
        if (worldObj.isRemote)
        {
            keepClientDataUpdated();
        }
    }

    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations()
    {
        return EnumSet.of(ClusterMethodRegistration.ON_BLOCK_PLACED_BY, ClusterMethodRegistration.ON_BLOCK_ACTIVATED);
    }

    @SideOnly(Side.CLIENT)
    private void keepClientDataUpdated()
    {
        if (isPartOfCluster())
        {
            return;
        }

        double distance = Minecraft.getMinecraft().thePlayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);

        if (distance > PacketHandler.BLOCK_UPDATE_SQ)
        {
            hasUpdatedData = false;
        } else if (!hasUpdatedData && distance < Math.pow(PacketHandler.BLOCK_UPDATE_RANGE - UPDATE_BUFFER_DISTANCE, 2))
        {
            hasUpdatedData = true;
            PacketHandler.sendBlockPacket(this, Minecraft.getMinecraft().thePlayer, 0);
        }
    }

    @Override
    public void writeData(DataWriter dw, EntityPlayer player, boolean onServer, int id)
    {
        if (onServer)
        {
            dw.writeData(placeDirection, DataBitHelper.PLACE_DIRECTION);
        } else
        {
            //nothing to write, empty packet
        }
    }

    @Override
    public void readData(DataReader dr, EntityPlayer player, boolean onServer, int id)
    {
        if (onServer)
        {
            //respond by sending the data to the client that required it
            PacketHandler.sendBlockPacket(this, player, 0);
        } else
        {
            placeDirection = dr.readData(DataBitHelper.PLACE_DIRECTION);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public int infoBitLength(boolean onServer)
    {
        return 0;
    }

    public int getPlaceDirection()
    {
        return placeDirection;
    }

    public void setPlaceDirection(int placeDirection)
    {
        if (this.placeDirection != placeDirection)
        {
            this.placeDirection = placeDirection;

            if (!isPartOfCluster() && worldObj != null && !worldObj.isRemote)
            {
                PacketHandler.sendBlockPacket(this, null, 0);
            }
        }
    }

    protected ForgeDirection getDirection()
    {
        return ForgeDirection.VALID_DIRECTIONS[getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length];
    }

    @Override
    protected void readContentFromNBT(NBTTagCompound tagCompound)
    {
        if (tagCompound.hasKey(NBT_DIRECTION))
        {
            setPlaceDirection(tagCompound.getByte(NBT_DIRECTION));
        } else
        {
            if (worldObj != null)
            {
                setPlaceDirection(getBlockMetadata());
            } else
            {
                missingPlaceDirection = true;
            }
        }
    }

    @Override
    protected void writeContentToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setByte(NBT_DIRECTION, (byte)placeDirection);
    }

    public abstract boolean isBlocked();
}
