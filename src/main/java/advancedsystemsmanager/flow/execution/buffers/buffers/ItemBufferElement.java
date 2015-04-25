package advancedsystemsmanager.flow.execution.buffers.buffers;

import advancedsystemsmanager.api.execution.Key;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ItemBufferElement extends BufferElementBase<ItemStack>
{
    protected IInventory inventory;
    protected int slot;

    public ItemBufferElement(int id, IInventory inventory, int slot)
    {
        super(id);
        this.inventory = inventory;
        this.content = inventory.getStackInSlot(slot);
        this.slot = slot;
        this.amount = content.stackSize;
    }

    @Override
    public void remove()
    {
    }

    @Override
    public void onUpdate()
    {
        inventory.markDirty();
    }

    @Override
    public int getSizeLeft()
    {
        return Math.min(content.stackSize, amount);
    }

    @Override
    public int reduceBufferAmount(int amount)
    {
        content.stackSize -= amount;
        this.amount -= amount;
        if (content.stackSize == 0) inventory.setInventorySlotContents(slot, null);
        onUpdate();
        return amount;
    }

    @Override
    public Key<ItemStack> getKey()
    {
        return new Key.ItemKey(content);
    }

    @Override
    public ItemBufferElement getSplitElement(int elementAmount, int id, boolean fair)
    {
        ItemBufferElement element = new ItemBufferElement(this.id, this.inventory, this.slot);
        int oldAmount = getSizeLeft();
        int amount = oldAmount / elementAmount;
        if (!fair)
        {
            int amountLeft = oldAmount % elementAmount;
            if (id < amountLeft)
            {
                amount++;
            }
        }
        element.amount = amount;
        return element;
    }
}
