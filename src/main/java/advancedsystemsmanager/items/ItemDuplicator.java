package advancedsystemsmanager.items;

import advancedsystemsmanager.api.items.ILeftClickItem;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemDuplicator extends ItemBase implements ILeftClickItem
{
    public static final String AUTHOR = "Author";
    public static final String CONTENTS = "ManagerContents";

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
            if (stack.getTagCompound().hasKey(AUTHOR))
            {
                list.add(StatCollector.translateToLocalFormatted(Names.AUTHORED, stack.getTagCompound().getString(AUTHOR)));
            } else
            {
                int x = stack.getTagCompound().getInteger(X);
                int y = stack.getTagCompound().getInteger(Y);
                int z = stack.getTagCompound().getInteger(Z);
                list.add(StatCollector.translateToLocal(Names.STORED_LOCATION));
                list.add(StatCollector.translateToLocalFormatted(Names.LOCATION, x, y, z));
            }
        }
    }

    public static boolean validateNBT(ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(CONTENTS))
            return true;
        stack.setTagCompound(null);
        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote && player.isSneaking())
        {
            TileEntity te = ClusterRegistry.MANAGER.getTileEntity(world, x, y, z);
            if (te instanceof TileEntityManager)
            {
                if (stack.hasTagCompound())
                {
                    ((TileEntityManager)te).readFromTileNBT(stack.getTagCompound());
                    stack.setTagCompound(null);
                } else
                {
                    NBTTagCompound tagCompound = new NBTTagCompound();
                    ((TileEntityManager)te).writeToTileNBT(tagCompound);
                    tagCompound.setBoolean(CONTENTS, true);
                    tagCompound.setInteger(X, x);
                    tagCompound.setInteger(Y, y);
                    tagCompound.setInteger(Z, z);
                    tagCompound.setTag("ench", new NBTTagList());
                    stack.setTagCompound(tagCompound);
                }
                return true;
            }
        }
        validateNBT(stack);
        return false;
    }

    @Override
    public boolean leftClick(EntityPlayer player, ItemStack stack, World world, int x, int y, int z, int face)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityManager)
        {
            world.removeTileEntity(x, y, z);
            TileEntityManager manager = new TileEntityManager();
            if (stack.hasTagCompound() && ItemDuplicator.validateNBT(stack))
            {
                manager.readFromNBT(stack.getTagCompound());
                stack.setTagCompound(null);
            }
            world.setTileEntity(x, y, z, manager);
            return true;
        }
        return false;
    }
}
