package advancedsystemsmanager.gui;

import advancedsystemsmanager.tileentities.TileEntityVoid;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerVoid extends ContainerBase<TileEntityVoid>
{
    public ContainerVoid(TileEntityVoid te, InventoryPlayer player)
    {
        super(te, player);
        bindPlayerInventory(8, 84);
        addSlotToContainer(new Slot(te, 1, 80, 23)
        {
            @Override
            public void onSlotChange(ItemStack stack, ItemStack newStack)
            {
            }

            @Override
            public void onSlotChanged()
            {
            }
        });
    }

    @Override
    public void detectAndSendChanges()
    {
    }
}
