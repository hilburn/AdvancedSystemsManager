package advancedsystemsmanager.flow.execution.buffers.buffers.items;

import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.execution.IBufferSubElement;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ItemBufferSubElement implements IBufferSubElement<ItemBufferSubElement, ItemStack, IInventory>
{
    protected ItemStack stack;
    protected IInventory inventory;
    protected int slot;
    protected int amount;

    public ItemBufferSubElement(IInventory inventory, int slot)
    {
        this.inventory = inventory;
        this.stack = inventory.getStackInSlot(slot);
        this.slot = slot;
        this.amount = stack.stackSize;
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
        return Math.min(stack.stackSize, amount);
    }

    @Override
    public int reduceBufferAmount(int amount)
    {
        stack.stackSize -= amount;
        this.amount -= amount;
        if (stack.stackSize == 0) inventory.setInventorySlotContents(slot, null);
        return amount;
    }

    @Override
    public int reduceContainerAmount(int amount)
    {
        return reduceBufferAmount(Math.min(getSizeLeft(), amount));
    }

    @Override
    public ItemStack getKey()
    {
        return stack;
    }

    @Override
    public IInventory getContainer()
    {
        return inventory;
    }

    @Override
    public ItemBufferSubElement getSplitElement(int elementAmount, int id, boolean fair)
    {
        ItemBufferSubElement element = new ItemBufferSubElement(this.inventory, this.slot);
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

    @Override
    public IBufferElement<?, ItemStack> getNewBufferElement()
    {
        return new ItemBufferElement(this);
    }
}
