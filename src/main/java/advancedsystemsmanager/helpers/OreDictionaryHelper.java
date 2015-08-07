package advancedsystemsmanager.helpers;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;

public class OreDictionaryHelper
{

    public static void registerUsefulThings()
    {
        for (int i = 0; i < 15; i++)
        {
            safeRegister("wool", new ItemStack(Blocks.wool, 1, i));
        }
    }

    public static void safeRegister(String name, Block block)
    {
        safeRegister(name, Item.getItemFromBlock(block));
    }

    public static void safeRegister(String name, Item item)
    {
        safeRegister(name, new ItemStack(item));
    }

    public static void safeRegister(String name, ItemStack stack)
    {
        if (!isRegistered(stack, OreDictionary.getOres(name)))
            OreDictionary.registerOre(name, stack);
    }

    private static boolean isRegistered(ItemStack stack, ArrayList<ItemStack> toCheck)
    {
        for (ItemStack check : toCheck)
        {
            if (stack != null && stack.getItem() == check.getItem()
                    && (stack.getItemDamage() == check.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE))
            {
                return true;
            }
        }
        return false;
    }
}
