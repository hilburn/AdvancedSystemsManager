package advancedsystemsmanager.items.blocks;

import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ClusterRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemCluster extends ItemAdvanced
{
    public ItemCluster(Block block)
    {
        super(block);
    }

    public static final String NBT_CABLE = "Cable";
    public static final String NBT_TYPES = "Types";

    @Override
    public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        NBTTagCompound compound = item.getTagCompound();
        if (compound != null && compound.hasKey(NBT_CABLE))
        {
            NBTTagCompound cable = compound.getCompoundTag(NBT_CABLE);
            if (cable.hasKey(NBT_TYPES))
            {
                return super.onItemUse(item, player, world, x, y, z, side, hitX, hitY, hitZ);
            }
        }

        return false;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void addInformation(ItemStack item, EntityPlayer player, List list, boolean extraInfo)
    {
        NBTTagCompound compound = item.getTagCompound();
        if (compound != null && compound.hasKey(NBT_CABLE))
        {
            NBTTagCompound cable = compound.getCompoundTag(NBT_CABLE);
            byte[] types = cable.getByteArray(ItemCluster.NBT_TYPES);
            for (byte type : types)
            {
                list.add(ClusterRegistry.getRegistryList().get(type).getItemStack().getDisplayName());
            }
        } else
        {
            list.add(StatCollector.translateToLocal(Names.EMPTY_CLUSTER));
        }
    }
}
