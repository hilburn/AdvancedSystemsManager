package advancedsystemsmanager.flow.execution.buffers.elements;

import advancedsystemsmanager.api.execution.Key;
import advancedsystemsmanager.flow.setting.Setting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ItemBufferElement extends BufferElementBase<ItemStack>
{
    protected IInventory inventory;
    protected int slot;

    public ItemBufferElement(int id, IInventory inventory, int slot, Setting<ItemStack> setting, boolean whitelist)
    {
        //this(id, inventory, slot);
        super(id);
        this.setting = setting;
        this.whitelist = whitelist;
        this.inventory = inventory;
        this.content = inventory.getStackInSlot(slot);
        this.slot = slot;
        this.amount = getMaxWithSetting(content.stackSize);
    }

    private ItemBufferElement(int id, IInventory inventory, int slot)
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
        return getMaxWithSetting(Math.min(content.stackSize, amount));
    }

    @Override
    public int reduceBufferAmount(int amount)
    {
        content.stackSize -= amount;
        this.amount -= amount;
        if (content.stackSize == 0) inventory.setInventorySlotContents(slot, null);
        return amount;
    }

    @Override
    public ItemBufferElement getSplitElement(int elementAmount, int id, boolean fair)
    {
        ItemBufferElement element = new ItemBufferElement(this.id, this.inventory, this.slot, this.setting, this.whitelist);
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
    public Key<ItemStack> getKey()
    {
        return new Key.ItemKey(content);
    }
}
