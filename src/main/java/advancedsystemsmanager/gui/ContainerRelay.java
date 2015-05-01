package advancedsystemsmanager.gui;

import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.tileentities.TileEntityRelay;
import advancedsystemsmanager.util.UserPermission;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import java.util.ArrayList;
import java.util.List;


public class ContainerRelay extends ContainerBase
{
    public List<UserPermission> oldPermissions;
    public boolean oldCreativeMode;
    public boolean oldOpList;
    private TileEntityRelay relay;

    public ContainerRelay(TileEntityRelay relay, InventoryPlayer player)
    {
        super(relay, player);
        this.relay = relay;
    }

    @Override
    public void addCraftingToCrafters(ICrafting player)
    {
        super.addCraftingToCrafters(player);
        PacketHandler.sendAllData(this, player, relay);
        oldPermissions = new ArrayList<UserPermission>();
        for (UserPermission permission : relay.getPermissions())
        {
            oldPermissions.add(permission.copy());
        }
        oldCreativeMode = relay.isCreativeMode();
        oldOpList = relay.doesListRequireOp();
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if (oldPermissions != null)
        {
            relay.updateData(this);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return entityplayer.getDistanceSq(relay.xCoord, relay.yCoord, relay.zCoord) <= 64;
    }
}
