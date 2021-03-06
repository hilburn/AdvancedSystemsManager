package advancedsystemsmanager.recipes;

import advancedsystemsmanager.registry.ClusterRegistry;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.ArrayList;
import java.util.List;


public class ClusterUpgradeRecipe extends ShapelessRecipes
{

    private static final ItemStack RESULT;
    private static final List<ItemStack> RECIPE;

    static
    {
        RESULT = ClusterRegistry.CLUSTER.getItemStack(1);
        RECIPE = new ArrayList<ItemStack>();
        RECIPE.add(ClusterRegistry.CLUSTER.getItemStack());
        for (int i = 0; i < 8; i++)
        {
            RECIPE.add(ClusterRegistry.CABLE.getItemStack());
        }
    }

    public ClusterUpgradeRecipe()
    {
        super(RESULT, RECIPE);
        RecipeSorter.register("asm:cluster", ClusterUpgradeRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack itemStack = inv.getStackInSlot(i);

            if (itemStack != null && ClusterRegistry.CLUSTER.getItemStack().isItemEqual(itemStack))
            {
                ItemStack copy = itemStack.copy();
                copy.setItemDamage(8);
                return copy;
            }
        }

        return super.getCraftingResult(inv);
    }
}
