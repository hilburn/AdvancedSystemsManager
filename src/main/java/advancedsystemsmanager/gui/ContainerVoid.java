package advancedsystemsmanager.gui;

import advancedsystemsmanager.tileentities.TileEntityVoid;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerVoid extends ContainerBase<TileEntityVoid>
{
    public ContainerVoid(TileEntityVoid te, InventoryPlayer player)
    {
        super(te, player);
        bindPlayerInventory(8, 102);
    }

    @Override
    public void detectAndSendChanges()
    {
    }
}
