package advancedsystemsmanager.flow.execution.buffers;

import advancedsystemsmanager.api.execution.Key;
import net.minecraft.item.ItemStack;

public class ItemBuffer extends Buffer<ItemStack>
{
    @Override
    public Key<ItemStack> getKey(ItemStack key)
    {
        return new Key.ItemKey(key);
    }

    @Override
    public Buffer<ItemStack> getNewBuffer()
    {
        return new ItemBuffer();
    }
}
