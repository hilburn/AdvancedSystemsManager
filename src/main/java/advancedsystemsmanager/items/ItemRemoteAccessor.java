package advancedsystemsmanager.items;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class ItemRemoteAccessor extends ItemBase
{
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";

    public ItemRemoteAccessor()
    {
        super(Names.REMOTE_ACCESS);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
        {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityManager)
            {
                NBTTagCompound tagCompound = new NBTTagCompound();
//                tagCompound.setByte("world", wor);
                tagCompound.setInteger(X, te.xCoord);
                tagCompound.setInteger(Y, te.yCoord);
                tagCompound.setInteger(Z, te.zCoord);
                stack.setTagCompound(tagCompound);
                return true;
            }
        }
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_)
    {
        if (stack.hasTagCompound())
        {
            int x = stack.getTagCompound().getInteger(X);
            int y = stack.getTagCompound().getInteger(Y);
            int z = stack.getTagCompound().getInteger(Z);
            list.add("Linked to Manager at:");
            list.add("x: " + x + " y: " + y + " z: " + z);
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote && stack.hasTagCompound())
        {
            int x = stack.getTagCompound().getInteger(X);
            int y = stack.getTagCompound().getInteger(Y);
            int z = stack.getTagCompound().getInteger(Z);
            if (world.getChunkProvider().chunkExists(x/16, z/16))
            {
                if (world.getTileEntity(x, y, z) instanceof TileEntityManager)
                    FMLNetworkHandler.openGui(player, AdvancedSystemsManager.INSTANCE, 1, world, x, y, z);
            }
        }
        return super.onItemRightClick(stack, world, player);
    }
}
