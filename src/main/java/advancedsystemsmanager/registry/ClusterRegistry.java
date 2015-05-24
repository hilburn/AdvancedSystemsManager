package advancedsystemsmanager.registry;

import advancedsystemsmanager.api.tileentities.IClusterElement;
import advancedsystemsmanager.api.tileentities.IClusterTile;
import gnu.trove.map.hash.TByteObjectHashMap;
import net.minecraft.item.ItemStack;

public class ClusterRegistry
{
    private static final TByteObjectHashMap<IClusterElement> registry = new TByteObjectHashMap<IClusterElement>();

    public static void register(int id, IClusterElement element)
    {
        byte b = (byte)(id & 0xFF);
        if (registry.containsKey(b))
            throw new IllegalArgumentException("ID: " + id + " is already registered by " + getItemStack(id).getDisplayName());
        registry.put(b, element);
    }

    public static IClusterElement get(IClusterTile clusterTile)
    {
        for (IClusterElement element : registry.valueCollection())
            if (element.isInstance(clusterTile)) return element;
        return null;
    }

    public static IClusterElement get(ItemStack stack)
    {
        for (IClusterElement element : registry.valueCollection())
        {
            ItemStack elementStack = element.getItemStack(stack.getItemDamage());
            if (elementStack.isItemEqual(stack)) return element;
        }
        return null;
    }

    public static ItemStack getItemStack(int id)
    {
        return getItemStack((byte)(id & 0xFF), id>>8);
    }

    public static ItemStack getItemStack(byte id, int meta)
    {
        return registry.containsKey(id) ? registry.get(id).getItemStack(meta) : null;
    }

    public static IClusterElement get(byte id)
    {
        return registry.get(id);
    }
}
