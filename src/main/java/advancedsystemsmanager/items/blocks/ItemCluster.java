package advancedsystemsmanager.items.blocks;

import advancedsystemsmanager.api.items.IClusterItem;
import advancedsystemsmanager.api.tileentities.IClusterElement;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ClusterRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ItemCluster extends ItemAdvanced
{
    public static final String NBT_CABLE = "Cable";
    public static final String NBT_TYPES = "Types";
    public static final String NBT_DAMAGE = "Damage";
    public static final Comparator<ItemStack> ALPHABETICAL_STACKS = new Comparator<ItemStack>()
    {
        @Override
        public int compare(ItemStack o1, ItemStack o2)
        {
            return String.CASE_INSENSITIVE_ORDER.compare(o1.getDisplayName(), o2.getDisplayName());
        }
    };

    public ItemCluster(Block block)
    {
        super(block);
    }

    public static List<IClusterElement> getTypes(NBTTagCompound tag)
    {
        List<IClusterElement> result = new ArrayList<IClusterElement>();
        byte[] types = tag.getByteArray(NBT_TYPES);
        for (byte type : types)
        {
            IClusterElement element = ClusterRegistry.get(type);
            if (element != null) result.add(element);
        }
        return result;
    }

    public static void setClusterTag(ItemStack output, List<ItemStack> stacks)
    {
        Collections.sort(stacks, ALPHABETICAL_STACKS);
        NBTTagCompound tag = new NBTTagCompound();
        output.setTagCompound(tag);
        NBTTagCompound cable = new NBTTagCompound();
        byte[] types = new byte[stacks.size()];
        byte[] damages = new byte[stacks.size()];
        for (int i = 0; i < stacks.size(); i++)
        {
            ItemStack stack = stacks.get(i);
            IClusterElement element = ((IClusterItem)stack.getItem()).getClusterElement(stack);
            types[i] = element.getId();
            damages[i] = (byte)stack.getItemDamage();
        }
        cable.setByteArray(NBT_TYPES, types);
        cable.setByteArray(NBT_DAMAGE, damages);
        tag.setTag(NBT_CABLE, cable);
    }

    @Override
    public IClusterElement getClusterElement(ItemStack stack)
    {
        return null;
    }

    @Override
    public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
//        NBTTagCompound compound = item.getTagCompound();
//        if (compound != null && compound.hasKey(NBT_CABLE))
//        {
//            NBTTagCompound cable = compound.getCompoundTag(NBT_CABLE);
//            if (cable.hasKey(NBT_TYPES))
//            {
        return super.onItemUse(item, player, world, x, y, z, side, hitX, hitY, hitZ);
//            }
//        }
//
//        return false;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void addInformation(ItemStack item, EntityPlayer player, List list, boolean extraInfo)
    {
        NBTTagCompound compound = item.getTagCompound();
        if (compound != null && compound.hasKey(NBT_CABLE))
        {
            for (ItemStack stack : getItemStacks(compound.getCompoundTag(NBT_CABLE)))
            {
                list.add(stack.getDisplayName());
            }
        } else
        {
            list.add(StatCollector.translateToLocal(Names.EMPTY_CLUSTER));
        }
    }

    public static List<ItemStack> getItemStacks(NBTTagCompound tag)
    {
        List<ItemStack> stacks = new ArrayList<ItemStack>();
        byte[] types = tag.getByteArray(NBT_TYPES);
        byte[] damages = tag.hasKey(NBT_DAMAGE) ? tag.getByteArray(NBT_DAMAGE) : new byte[types.length];
        for (int i = 0; i < types.length; i++)
        {
            ItemStack stack = ClusterRegistry.getItemStack(types[i], damages[i]);
            if (stack != null) stacks.add(stack);
        }
        return stacks;
    }
}
