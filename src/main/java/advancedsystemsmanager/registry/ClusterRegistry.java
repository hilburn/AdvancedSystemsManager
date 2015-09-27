package advancedsystemsmanager.registry;

import advancedsystemsmanager.api.tileentities.ITileFactory;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class ClusterRegistry
{
    private static final Map<String, ITileFactory> registry = new TreeMap<String, ITileFactory>();

    public static void register(ITileFactory tileFactory)
    {
        if (registry.containsKey(tileFactory.getUnlocalizedName()))
            throw new IllegalArgumentException("ID: " + tileFactory.getUnlocalizedName() + " is already registered by " + getItemStack(tileFactory.getUnlocalizedName()).getDisplayName());
        registry.put(tileFactory.getUnlocalizedName(), tileFactory);
    }

    public static ItemStack getItemStack(String id)
    {
        return registry.containsKey(id) ? registry.get(id).getItemStack() : null;
    }

    public static ITileFactory get(String id)
    {
        return registry.get(id);
    }

    public static Collection<ITileFactory> getFactories()
    {
        return registry.values();
    }
}
