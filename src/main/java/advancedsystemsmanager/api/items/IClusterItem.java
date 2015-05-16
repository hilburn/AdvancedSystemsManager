package advancedsystemsmanager.api.items;

import advancedsystemsmanager.registry.ClusterRegistry;
import net.minecraft.item.ItemStack;

public interface IClusterItem
{
    ClusterRegistry getClusterRegistry(ItemStack stack);
}
