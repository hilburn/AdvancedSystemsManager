package advancedsystemsmanager.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PlayerHelper
{
    public static boolean consumeItem(EntityPlayer player)
    {
        return consumeItem(player, player.getCurrentEquippedItem(), player.inventory.currentItem);
    }

    public static boolean consumeItem(EntityPlayer player, ItemStack stack, int slot)
    {
        if (player.capabilities.isCreativeMode) return true;
        if (stack == null) return false;
        if (--stack.stackSize <= 0) player.inventory.setInventorySlotContents(slot, null);
        return true;
    }
}
