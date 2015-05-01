package advancedsystemsmanager.recipes;

import advancedsystemsmanager.items.blocks.ItemCluster;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.registry.ClusterRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

public class ClusterUncraftingRecipe implements IRecipe
{
    private static ItemStack cluster = new ItemStack(BlockRegistry.blockCableCluster);
    private static ItemStack advcluster = new ItemStack(BlockRegistry.blockCableCluster, 1, 8);

    static
    {
        RecipeSorter.register("asm:clusterUncrafting", ClusterUncraftingRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
    }

    @Override
    public boolean matches(InventoryCrafting crafting, World world)
    {
        return matches(crafting);
    }

    public boolean matches(IInventory crafting)
    {
        boolean hasCluster = false;
        for (int i = 0; i < crafting.getSizeInventory(); i++)
        {
            ItemStack stack = crafting.getStackInSlot(i);
            if (stack == null) continue;
            if (!hasCluster && (stack.isItemEqual(cluster) || stack.isItemEqual(advcluster)))
            {
                hasCluster = true;
                continue;
            }
            return false;
        }
        return hasCluster;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting crafting)
    {
        ItemStack result = cluster.copy();
        result.setItemDamage(getCluster(crafting).getItemDamage());
        return result;
    }

    public ItemStack getCluster(IInventory crafting)
    {
        for (int i = 0; i < crafting.getSizeInventory(); i++)
        {
            ItemStack stack = crafting.getStackInSlot(i);
            if (stack == null) continue;
            if (stack.isItemEqual(cluster) || stack.isItemEqual(advcluster)) return stack;
        }
        return null;
    }

    @Override
    public int getRecipeSize()
    {
        return 1;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return null;
    }

    @SubscribeEvent
    public void onCrafting(PlayerEvent.ItemCraftedEvent e)
    {
        if (matches(e.craftMatrix))
        {
            ItemStack stack = getCluster(e.craftMatrix);
            if (stack != null)
            {
                if (stack.hasTagCompound())
                {
                    byte[] types = stack.getTagCompound().getCompoundTag(ItemCluster.NBT_CABLE).getByteArray(ItemCluster.NBT_TYPES);
                    int stackSize = e.crafting.stackSize;
                    stackSize = stackSize == 0 ? 1 : stackSize;
                    for (byte type : types)
                    {
                        ItemStack component = ClusterRegistry.getRegistryList().get(type).getItemStack().copy();
                        component.stackSize = stackSize;
                        if (!e.player.inventory.addItemStackToInventory(component))
                            e.player.dropPlayerItemWithRandomChoice(component, false);
                    }
                }
            }
        }
    }
}