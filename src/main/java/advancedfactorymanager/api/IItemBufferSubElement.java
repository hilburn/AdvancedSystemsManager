package advancedfactorymanager.api;


import net.minecraft.item.ItemStack;

public interface IItemBufferSubElement
{
    void remove();

    void onUpdate();

    int getSizeLeft();

    void reduceAmount(int amount);

    ItemStack getItemStack();
}
