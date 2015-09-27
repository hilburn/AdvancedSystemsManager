package advancedsystemsmanager.recipes;

import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.tileentities.TileEntityQuantumCable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class QuantumPairingRecipe implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world)
    {
        int range = -1;
        int found = 0;
        for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++)
        {
            ItemStack item = inventoryCrafting.getStackInSlot(i);

            if (item != null)
            {
                if (ClusterRegistry.QUANTUM.getItemStack().isItemEqual(item))
                {
                    if (item.getTagCompound().hasKey(TileEntityQuantumCable.NBT_QUANTUM_KEY))
                    {
                        return false;
                    }
                    int newRange = item.getTagCompound().getInteger(TileEntityQuantumCable.NBT_QUANTUM_RANGE);
                    if (range != -1 && range != newRange || found > 1)
                    {
                        return false;
                    }
                    range = newRange;
                    found++;
                } else
                {
                    return false;
                }
            }
        }
        return found == 2;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting)
    {
        ItemStack stack = null;
        for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++)
        {
            ItemStack item = inventoryCrafting.getStackInSlot(i);

            if (item != null)
            {
                stack = item.copy();
                break;
            }
        }
        if (stack == null)
        {
            return null;
        }
        stack.stackSize = 2;
        stack.getTagCompound().setInteger(TileEntityQuantumCable.NBT_QUANTUM_KEY, TileEntityQuantumCable.peekNextQuantumKey());
        return stack;
    }

    @Override
    public int getRecipeSize()
    {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
//        return new ItemStack(BlockRegistry.cableQuantum);
        return ClusterRegistry.CABLE.getItemStack();
    }
}
