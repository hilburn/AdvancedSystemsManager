package advancedsystemsmanager.recipes;


import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.List;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;

public class ClusterRecipe implements IRecipe
{
    private ItemStack output;

    static
    {
        RecipeSorter.register("asm:cluster", ClusterRecipe.class, SHAPED, "after:minecraft:shapeless");
    }

    @Override
    public boolean matches(InventoryCrafting inventorycrafting, World world)
    {
        output = null;


        ItemStack cluster = null;
        for (int i = 0; i < inventorycrafting.getSizeInventory(); i++)
        {
            ItemStack item = inventorycrafting.getStackInSlot(i);

//            if (item != null && Block.getBlockFromItem(item.getItem()) == BlockRegistry.cableCluster)
            {
                if (cluster != null)
                {
                    return false; //multiple clusters
                } else
                {
                    cluster = item;
                }
            }
        }

        if (cluster != null)
        {
//            boolean foundClusterComponent = false;
//            if (!cluster.hasTagCompound()) cluster.setTagCompound(new NBTTagCompound());
//            List<IClusterElement> existing = ItemCluster.getTypes(cluster.getTagCompound().getCompoundTag(ItemCluster.NBT_CABLE));
//            List<ItemStack> stacks = ItemCluster.getItemStacks(cluster.getTagCompound().getCompoundTag(ItemCluster.NBT_CABLE));
//
//            for (int i = 0; i < inventorycrafting.getSizeInventory(); i++)
//            {
//                ItemStack item = inventorycrafting.getStackInSlot(i);
//
//                if (item != null && item.getItem() instanceof IClusterItem)
//                {
//                    IClusterElement element = ((IClusterItem)item.getItem()).getClusterElement(item);
//                    if (element != null)
//                    {
//                        if (existing.contains(element)) return false;
//                        existing.add(element);
//                        stacks.add(item);
//                        foundClusterComponent = true;
//                    }
//                }
//            }
//
//            if (!foundClusterComponent)
//            {
//                return false; //nothing added
//            }
//
////            output = new ItemStack(BlockRegistry.cableCluster, 1, cluster.getItemDamage());
//            ItemCluster.setClusterTag(output, stacks);
            return true;
        }

        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventorycrafting)
    {
        return output.copy();
    }

    @Override
    public int getRecipeSize()
    {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return output;
    }
}
