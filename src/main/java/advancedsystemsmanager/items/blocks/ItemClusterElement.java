package advancedsystemsmanager.items.blocks;

import advancedsystemsmanager.api.items.IClusterItem;
import advancedsystemsmanager.api.tileentities.IClusterElement;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemClusterElement extends ItemBlock implements IClusterItem
{
    private IClusterElement element;

    public ItemClusterElement(Block block)
    {
        super(block);
        if (block instanceof IClusterElement) element = (IClusterElement)block;
    }

    @Override
    public IClusterElement getClusterElement(ItemStack stack)
    {
        return element;
    }
}
