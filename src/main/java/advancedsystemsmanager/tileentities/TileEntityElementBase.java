package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.api.ITileFactory;
import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.api.tileentities.ITileInterfaceProvider;
import advancedsystemsmanager.api.tiletypes.ITileElement;
import advancedsystemsmanager.blocks.BlockTileElement;
import advancedsystemsmanager.blocks.TileFactory;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public abstract class TileEntityElementBase extends TileEntity implements ITileElement, IPacketBlock
{
    protected enum PacketType
    {
        SUBTYPE, ROTATION, CAMOUFLAGE, TILE_DATA
    }
    private static final String NBT_SUBTYPE = "s";
    protected int subtype;
    protected boolean isPartOfCluster;

    @Override
    public IIcon getIcon(int side)
    {
        return getTileFactory().getIcons()[0];
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
    public void setSubtype(int subtype)
    {
        this.subtype = subtype;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        readContentFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        writeContentToNBT(tag);
    }

    @Override
    public void readContentFromNBT(NBTTagCompound tag)
    {
        this.subtype = tag.getByte(NBT_SUBTYPE);
    }

    @Override
    public void writeContentToNBT(NBTTagCompound tag)
    {
        if (subtype != 0)
        {
            tag.setByte(NBT_SUBTYPE, (byte) subtype);
        }
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
    public void readData(ASMPacket packet, int id)
    {

    }

    @Override
    public void writeData(ASMPacket packet, int id)
    {

    }

    public void sendBlockPacket(EntityPlayer player, int id)
    {
        if (!this.isPartOfCluster && !worldObj.isRemote)
        {
            PacketHandler.sendBlockPacket(this, player, id);
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
