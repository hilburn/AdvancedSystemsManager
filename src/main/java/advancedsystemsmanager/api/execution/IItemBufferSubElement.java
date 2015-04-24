package advancedsystemsmanager.api.execution;


import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IItemBufferSubElement
{
    void remove();

    void onUpdate();

    int getSizeLeft();

    int reduceBufferAmount(int amount);

    ItemStack getKey();

    IInventory getContainer();

    IItemBufferSubElement getSplitElement(int elementAmount, int id, boolean fair);

    int getSlot();
}
