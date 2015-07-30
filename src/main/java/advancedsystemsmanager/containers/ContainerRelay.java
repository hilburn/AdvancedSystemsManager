package advancedsystemsmanager.containers;

import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.tileentities.TileEntityRelay;
import advancedsystemsmanager.util.UserPermission;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import java.util.ArrayList;
import java.util.List;

public class ContainerRelay extends ContainerBase<TileEntityRelay>
{
    public List<UserPermission> oldPermissions;
    public boolean oldCreativeMode;
    public boolean oldOpList;

    public ContainerRelay(TileEntityRelay relay, InventoryPlayer player)
    {
        super(relay, player);
    }

    @Override
    public void addCraftingToCrafters(ICrafting player)
    {
        super.addCraftingToCrafters(player);
        PacketHandler.sendAllData(this, player, te);
        oldPermissions = new ArrayList<UserPermission>();
        for (UserPermission permission : te.getPermissions())
        {
            oldPermissions.add(permission.copy());
        }
        oldCreativeMode = te.isCreativeMode();
        oldOpList = te.doesListRequireOp();
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if (oldPermissions != null)
        {
            te.updateData(this);
        }
    }
}
