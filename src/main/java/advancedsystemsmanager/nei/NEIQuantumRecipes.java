package advancedsystemsmanager.nei;

import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.tileentities.TileEntityQuantumCable;
import codechicken.nei.recipe.ShapelessRecipeHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NEIQuantumRecipes extends ShapelessRecipeHandler
{
    public static final Random rand = new Random(System.currentTimeMillis());
    private static final ItemStack[] INTERDIMENSIONAL = new ItemStack[9];

    static
    {
        INTERDIMENSIONAL[0] = ClusterRegistry.CABLE.getItemStack();
        for (int i = 1; i < 9; i++)
        {
            INTERDIMENSIONAL[i] = new ItemStack(Items.ender_eye);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        super.loadCraftingRecipes(result);
        if (ClusterRegistry.QUANTUM.getItemStack().isItemEqual(result))
        {
            if (!result.getTagCompound().hasKey(TileEntityQuantumCable.NBT_QUANTUM_KEY))
            {
                if (result.getTagCompound().getInteger(TileEntityQuantumCable.NBT_QUANTUM_RANGE) < 9)
                {
                    CachedQuantumRecipe recipe = new CachedQuantumRecipe(result);
                    recipe.cycleIngredients();
                    arecipes.add(recipe);
                } else
                {
                    arecipes.add(new CachedShapelessRecipe(INTERDIMENSIONAL, result));
                }
            }
            else
            {
                ItemStack input = result.copy();
                ItemStack clone = result.copy();
                input.getTagCompound().removeTag(TileEntityQuantumCable.NBT_QUANTUM_KEY);
                clone.getTagCompound().setInteger(TileEntityQuantumCable.NBT_QUANTUM_KEY, TileEntityQuantumCable.peekNextQuantumKey());
                clone.stackSize = 2;
                arecipes.add(new CachedShapelessRecipe(new ItemStack[]{input, input}, clone));
            }
        }
    }

    @Override
    public void drawExtras(int recipe)
    {
        CachedRecipe cachedRecipe = arecipes.get(recipe);
        if (cachedRecipe instanceof CachedQuantumRecipe) ((CachedQuantumRecipe) cachedRecipe).doCycle(cycleticks);
    }

    private class CachedQuantumRecipe extends CachedShapelessRecipe
    {
        private long cycleAtTick;
        private ItemStack output;

        public CachedQuantumRecipe(ItemStack output)
        {
            cycleAtTick = -1;
            this.output = output.copy();
            this.output.stackSize = 2;
            setResult(this.output);
        }

        private void cycleIngredients()
        {
            List<ItemStack> ingredients = new ArrayList<ItemStack>();
            ingredients.add(ClusterRegistry.CABLE.getItemStack());
            for (int i = 0; i < output.getTagCompound().getInteger(TileEntityQuantumCable.NBT_QUANTUM_RANGE); i++)
            {
                if (rand.nextBoolean())
                {
                    ingredients.add(new ItemStack(Items.ender_eye));
                } else
                {
                    ingredients.add(new ItemStack(Items.ender_pearl));
                }
            }
            setIngredients(ingredients);
        }

        public void doCycle(long tick)
        {
            if (tick >= cycleAtTick)
            {
                cycleAtTick = tick + 20;
                cycleIngredients();
            }
        }
    }
}
