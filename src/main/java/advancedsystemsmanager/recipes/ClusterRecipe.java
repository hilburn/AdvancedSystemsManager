package advancedsystemsmanager.recipes;

import advancedsystemsmanager.api.items.IElementItem;
import advancedsystemsmanager.api.tileentities.ITileElement;
import advancedsystemsmanager.api.tileentities.ITileFactory;
import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.tileentities.TileEntityCluster;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.ArrayList;
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

        ItemStack clusterStack = ClusterRegistry.CLUSTER.getItemStack();
        ItemStack advcluster = ClusterRegistry.CLUSTER.getItemStack(1);
        List<ItemStack> stacksToAdd = new ArrayList<ItemStack>();
        for (int i = 0; i < inventorycrafting.getSizeInventory(); i++)
        {
            ItemStack item = inventorycrafting.getStackInSlot(i);

            if (item != null)
            {
                if (item.isItemEqual(clusterStack) || item.isItemEqual(advcluster))
                {
                    if (output != null)
                    {
                        return false; //multiple clusters
                    } else
                    {
                        output = item;
                    }
                } else if (item.getItem() instanceof IElementItem)
                {
                    stacksToAdd.add(item);
                } else
                {
                    return false;
                }
            }
        }

        if (output != null && !stacksToAdd.isEmpty())
        {
            List<ItemStack> existing = TileEntityCluster.getSubblocks(output);
            List<ITileFactory> existingFactories = new ArrayList<ITileFactory>();
            for (ItemStack element : existing)
            {
                ITileFactory factory = ((IElementItem) element.getItem()).getTileFactory(element);
                if (factory != null) existingFactories.add(factory);
            }
            for (ItemStack stack : stacksToAdd)
            {
                ITileFactory factory = ((IElementItem) stack.getItem()).getTileFactory(stack);
                if (factory == null || !factory.canBeAddedToCluster(existingFactories))
                {
                    return false;
                }
                existingFactories.add(factory);
                existing.add(stack);
            }
            TileEntityCluster.saveSubBlocks(output, existingFactories, existing);
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
