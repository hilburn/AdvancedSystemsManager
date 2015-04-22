package advancedsystemsmanager.reference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class Null
{
    private static class NullInventory implements IInventory
    {
        @Override
        public int getSizeInventory()
        {
            return 0;
        }

        @Override
        public ItemStack getStackInSlot(int slot)
        {
            return null;
        }

        @Override
        public ItemStack decrStackSize(int slot, int amount)
        {
            return null;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int slot)
        {
            return null;
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack)
        {

        }

        @Override
        public String getInventoryName()
        {
            return null;
        }

        @Override
        public boolean hasCustomInventoryName()
        {
            return false;
        }

        @Override
        public int getInventoryStackLimit()
        {
            return Integer.MAX_VALUE;
        }

        @Override
        public void markDirty()
        {
        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer player)
        {
            return false;
        }

        @Override
        public void openInventory()
        {
        }

        @Override
        public void closeInventory()
        {
        }

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack)
        {
            return false;
        }
    }

    public static IInventory NULL_INVENTORY = new NullInventory();
    public static ItemStack NULL_STACK = new ItemStack(Blocks.end_portal, 0);
}
