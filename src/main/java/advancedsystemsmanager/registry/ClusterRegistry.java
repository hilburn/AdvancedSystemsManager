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
        if (registry.containsKey(tileFactory.getKey()))
            throw new IllegalArgumentException("ID: " + tileFactory.getKey() + " is already registered by " + registry.get(tileFactory.getKey()).getClass());
        registry.put(tileFactory.getKey(), tileFactory);
    }

    public static ItemStack getItemStack(String id)
    {
        return registry.containsKey(id) ? registry.get(id).getItemStack(0) : null;
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
