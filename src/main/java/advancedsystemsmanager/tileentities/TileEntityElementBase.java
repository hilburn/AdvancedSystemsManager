package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.api.tileentities.ICluster;
import advancedsystemsmanager.api.tileentities.ITileFactory;
import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.api.tileentities.ITileInterfaceProvider;
import advancedsystemsmanager.api.tileentities.ITileElement;
import advancedsystemsmanager.blocks.BlockTileElement;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public abstract class TileEntityElementBase extends TileEntity implements ITileElement, IPacketBlock
{
    public static final int CLIENT_SYNC = 1;
    public static final int TILE_DATA = 2;
    protected static final String NBT_SUBTYPE = "s";
    protected int subtype;
    protected boolean isPartOfCluster;
    private int message;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        sendPacketToClient();
    }

    @Override
    public IIcon getIcon(int side)
    {
        return getTileFactory().getIcon(side, subtype);
    }

    @Override
    public boolean isPartOfCluster()
    {
        return isPartOfCluster;
    }

    @Override
    public void setPartOfCluster(boolean partOfCluster)
    {
        this.isPartOfCluster = partOfCluster;
    }

    @Override
    public void onAddedToCluster(ICluster cluster)
    {
    }

    @Override
    public int getSubtype()
    {
        return subtype;
    }

    @Override
    public void setSubtype(int subtype)
    {
        if (subtype != this.subtype)
        {
            this.subtype = subtype;
            setMessageType(CLIENT_SYNC);
        }
    }

    @Override
    public final void readFromNBT(NBTTagCompound tag)
    {
        if (tag != null)
        {
            super.readFromNBT(tag);
            readFromTileNBT(tag);
            readItemNBT(tag);
        }
    }

    @Override
    public final void writeToNBT(NBTTagCompound tag)
    {
        if (tag != null)
        {
            super.writeToNBT(tag);
            writeToTileNBT(tag);
            writeItemNBT(tag);
        }
    }

    public void readFromTileNBT(NBTTagCompound tag)
    {
        setSubtype(tag.getByte(NBT_SUBTYPE));
    }

    public void writeToTileNBT(NBTTagCompound tag)
    {
        if (subtype != 0)
        {
            tag.setByte(NBT_SUBTYPE, (byte) subtype);
        }
    }

    @Override
    public void readItemNBT(NBTTagCompound tag)
    {
    }

    @Override
    public void writeItemNBT(NBTTagCompound tag)
    {
    }

    public boolean onBlockActivated(EntityPlayer player, int side, float xSide, float ySide, float zSide)
    {
        if (this instanceof ITileInterfaceProvider && !worldObj.isRemote)
        {
            FMLNetworkHandler.openGui(player, AdvancedSystemsManager.INSTANCE, 1, worldObj, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        sendPacketToClient(CLIENT_SYNC);
        return null;
    }

    @Override
    public final void readData(ASMPacket packet, int id)
    {
        if ((id & CLIENT_SYNC) > 0)
            readClientSyncData(packet);
        if ((id & TILE_DATA) > 0)
            readTileData(packet);
    }

    @Override
    public final void writeData(ASMPacket packet, int id)
    {
        if ((id & CLIENT_SYNC) > 0)
            writeClientSyncData(packet);
        if ((id & TILE_DATA) > 0)
            writeTileData(packet);
    }

    public void writeClientSyncData(ASMPacket packet)
    {
        packet.writeByte(subtype);
    }


    public void writeTileData(ASMPacket packet)
    {
    }

    public void readClientSyncData(ASMPacket packet)
    {
        setSubtype(packet.readByte());
        markForRenderUpdate();
    }

    public void readTileData(ASMPacket packet)
    {
    }

    public void setMessageType(int type)
    {
        message |= type;
    }

    public void sendPacketToClient()
    {
        sendPacketToClient(message);
        message = 0;
    }

    public void sendPacketToClient(int message)
    {
        if (message > 0 && !worldObj.isRemote)
        {
            if ((message < TILE_DATA && !this.isPartOfCluster))
            {
                PacketHandler.sendBlockPacket(this, null, message);
            }
        }
    }

    public void markForRenderUpdate()
    {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public ITileFactory getTileFactory()
    {
        return ((BlockTileElement)worldObj.getBlock(xCoord, yCoord, zCoord)).getTileFactory(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
    }
}
