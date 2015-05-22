package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
            //PacketHandler.sendBlockPacket(this, Minecraft.getMinecraft().thePlayer, 0);
        }
    }

    @Override
    public void writeData(ASMPacket packet)
    {
        packet.writeByte(placeDirection);
    }

    @Override
    public void readData(ASMPacket packet)
    {
        placeDirection = packet.readByte();
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
                //PacketHandler.sendBlockPacket(this, null, 0);
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

    public void onNeighbourBlockChange(World world, int x, int y, int z, Block block)
    {
    }
}
