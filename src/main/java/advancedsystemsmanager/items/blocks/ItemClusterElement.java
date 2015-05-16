package advancedsystemsmanager.items.blocks;

import advancedsystemsmanager.api.items.IClusterItem;
import advancedsystemsmanager.registry.ClusterRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemClusterElement extends ItemBlock implements IClusterItem
{
    public ItemClusterElement(Block block)
    {
        super(block);
    }

    @Override
    public ClusterRegistry getClusterRegistry(ItemStack stack)
    {
        return ClusterRegistry.get(stack);
    }
}
