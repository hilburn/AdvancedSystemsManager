package advancedsystemsmanager.api.execution;


import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IItemBufferSubElement extends IBufferSubElement<IItemBufferSubElement, ItemStack, IInventory>
{
    int getSlot();
}
