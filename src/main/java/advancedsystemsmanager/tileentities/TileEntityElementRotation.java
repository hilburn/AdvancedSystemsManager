package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.tiletypes.IPlaceListener;
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
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setByte(DIRECTION, (byte) facing.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        setFacing(tag.getByte(DIRECTION));
    }

    private void setFacing(int i)
    {
        facing = ForgeDirection.getOrientation(i);
    }

    @Override
    public void writeData(ASMPacket packet, int id)
    {
        switch (PacketType.values()[id])
        {
            case ROTATION:
                packet.writeByte(facing.ordinal());
        }
    }

    @Override
    public void readData(ASMPacket packet, int id)
    {
        switch (PacketType.values()[id])
        {
            case ROTATION:
                facing = ForgeDirection.getOrientation(packet.readByte());
                markForRenderUpdate();
        }
    }

    public ForgeDirection getFacing()
    {
        return facing;
    }
}
