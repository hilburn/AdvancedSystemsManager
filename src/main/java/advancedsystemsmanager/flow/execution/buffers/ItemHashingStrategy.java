package advancedsystemsmanager.flow.execution.buffers;

import advancedsystemsmanager.api.execution.IInventoryBufferElement;
import gnu.trove.strategy.HashingStrategy;

public class ItemHashingStrategy implements HashingStrategy<IInventoryBufferElement>
{
    public static ItemStackHashingStrategy ITEM_HASH = new ItemStackHashingStrategy();

    @Override
    public int computeHashCode(IInventoryBufferElement object)
    {
        return ITEM_HASH.computeHashCode(object.getKey());
    }

    @Override
    public boolean equals(IInventoryBufferElement o1, IInventoryBufferElement o2)
    {
        return ITEM_HASH.equals(o1.getKey(), o1.getKey());
    }
}
