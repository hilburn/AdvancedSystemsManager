package advancedsystemsmanager.items;

import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class ItemDuplicator extends ItemBase
{
    public ItemDuplicator()
    {
        super(Names.DUPLICATOR);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_)
    {
        if (stack.hasTagCompound() && validateNBT(stack))
        {
            if (stack.getTagCompound().hasKey("Author"))
            {
                list.add("Manager setup authored by:");
                list.add(stack.getTagCompound().getString("Author"));
            } else
            {
                int x = stack.getTagCompound().getInteger("x");
                int y = stack.getTagCompound().getInteger("y");
                int z = stack.getTagCompound().getInteger("z");
                list.add("Data stored from Manager at:");
                list.add("x: " + x + " y: " + y + " z: " + z);
            }
        }
    }

    public static boolean validateNBT(ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("ManagerContents"))
            return true;
        stack.setTagCompound(null);
        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote && player.isSneaking())
        {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityManager)
            {
                if (stack.hasTagCompound())
                {
                    ((TileEntityManager)te).readContentFromNBT(stack.getTagCompound(),false);
                    stack.setTagCompound(null);
                } else
                {
                    NBTTagCompound tagCompound = new NBTTagCompound();
                    ((TileEntityManager)te).writeContentToNBT(tagCompound, false);
                    tagCompound.setBoolean("ManagerContents", true);
                    tagCompound.setInteger("x", x);
                    tagCompound.setInteger("y", y);
                    tagCompound.setInteger("z", z);
                    tagCompound.setTag("ench", new NBTTagList());
                    stack.setTagCompound(tagCompound);
                }
                return true;
            }
        }
        validateNBT(stack);
        return false;
    }
}
