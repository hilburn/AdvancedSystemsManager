package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.tileentities.ICluster;
import advancedsystemsmanager.api.tileentities.IPlaceListener;
import advancedsystemsmanager.helpers.BlockHelper;
import advancedsystemsmanager.network.ASMPacket;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityElementRotation extends TileEntityElementBase implements IPlaceListener
{
    private static final String DIRECTION = "d";
    protected ForgeDirection facing;

    @Override
    public IIcon getIcon(int side)
    {
        return facing == null ? super.getIcon(side) : side == facing.ordinal() ? super.getIcon(side) : getTileFactory().getIcons()[1];
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entity, ItemStack item)
    {
        int direction = BlockHelper.getThreeAxisDirection(entity);
        if (entity.isSneaking())
        {
            direction = BlockHelper.getReverseDirection(direction);
        }
        setFacing(direction);
    }

    @Override
    public void onAddedToCluster(ICluster cluster)
    {
        this.facing = cluster.getFacing();
    }

    @Override
    public void writeToTileNBT(NBTTagCompound tag)
    {
        super.writeToTileNBT(tag);
        tag.setByte(DIRECTION, (byte) facing.ordinal());
    }

    @Override
    public void readFromTileNBT(NBTTagCompound tag)
    {
        super.readFromTileNBT(tag);
        setFacing(tag.getByte(DIRECTION));
    }

    private void setFacing(int i)
    {
        facing = ForgeDirection.getOrientation(i);
        setMessageType(CLIENT_SYNC);
    }

    @Override
    public void writeClientSyncData(ASMPacket packet)
    {
        super.writeClientSyncData(packet);
        packet.writeByte(getFacing().ordinal());
    }

    @Override
    public void readClientSyncData(ASMPacket packet)
    {
        super.readClientSyncData(packet);
        facing = ForgeDirection.getOrientation(packet.readByte());
    }

    public ForgeDirection getFacing()
    {
        return facing == null ? ForgeDirection.UNKNOWN : facing;
    }
}
