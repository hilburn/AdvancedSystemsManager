package advancedsystemsmanager.api.execution;

import cofh.api.energy.IEnergyProvider;
import net.minecraft.item.ItemStack;

public class Key<Type>
{
    int hashCode;
    Type key;

    public Key(Type key)
    {
        setHashCode(key);
        this.key = key;
    }

    void setHashCode(Type key)
    {
        this.hashCode = key.hashCode();
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Key && key == ((Key)obj).key;
    }

    @Override
    public String toString()
    {
        return "[Key :" + key.toString() + "]";
    }

    public static class ItemKey extends Key<ItemStack>
    {
        public ItemKey(ItemStack key)
        {
            super(key);
        }

        @Override
        void setHashCode(ItemStack stack)
        {
            hashCode = stack.getItem().hashCode() ^ stack.getItemDamage() ^ (stack.hasTagCompound() ? stack.stackTagCompound.hashCode() : 0);
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof ItemKey && key.isItemEqual(((ItemKey)obj).key);
        }
    }

    public static class RFKey extends Key<Integer>
    {
        IEnergyProvider source;

        public RFKey(IEnergyProvider source, int id)
        {
            super(id);
            this.source = source;
            hashCode |= source.hashCode() << 5;
        }

        @Override
        void setHashCode(Integer key)
        {
            hashCode = key;
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof RFKey && source == ((RFKey) obj).source;
        }
    }
}
