package advancedsystemsmanager.flow.execution.buffers.maps;

import advancedsystemsmanager.api.execution.IBufferElement;
import gnu.trove.strategy.HashingStrategy;
import net.minecraft.item.ItemStack;

public class ItemHashingStrategy implements HashingStrategy<IBufferElement<ItemStack>>
{
    public static ItemStackHashingStrategy ITEM_HASH = new ItemStackHashingStrategy();

    @Override
    public int computeHashCode(IBufferElement<ItemStack> object)
    {
        return ITEM_HASH.computeHashCode(object.getKey());
    }

    @Override
    public boolean equals(IBufferElement<ItemStack> o1, IBufferElement<ItemStack> o2)
    {
        return ITEM_HASH.equals(o1.getKey(), o1.getKey());
    }
}
