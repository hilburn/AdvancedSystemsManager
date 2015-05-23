package advancedsystemsmanager.reference;

import advancedsystemsmanager.api.network.IPacketProvider;
import advancedsystemsmanager.api.network.IPacketSync;
import advancedsystemsmanager.network.ASMPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class Null
{
    public static IInventory NULL_INVENTORY = new IInventory()
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

    };
    public static ItemStack NULL_STACK = new ItemStack(Blocks.end_portal, 0);
    public static IPacketProvider NULL_PACKET = new IPacketProvider()
    {
        @Override
        public ASMPacket getSyncPacket()
        {
            return new ASMPacket();
        }

        @Override
        public void registerSyncable(IPacketSync networkSync)
        {
        }

        @Override
        public void sendPacketToServer(ASMPacket packet)
        {
        }
    };

}
