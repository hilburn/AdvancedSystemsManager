package advancedsystemsmanager.compatibility.thaumcraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.tiles.TileMagicWorkbench;

/**
 * Inspired by
 * https://github.com/Nividica/ThaumicEnergistics/blob/master/src/main/java/thaumicenergistics/integration/tc/ArcaneRecipeHelper.java
 */
public class ArcaneRecipeHelper
{
    private int firstSlot, gridSize;
    private IInventory inv;

    public ArcaneRecipeHelper(IInventory inv, int firstSlot, int gridSize)
    {
        this.inv = inv;
        this.firstSlot = firstSlot;
        this.gridSize = gridSize;
    }

    private TileMagicWorkbench getProxyWorkbench()
    {
        TileMagicWorkbench workbench = new TileMagicWorkbench();
        for (int slot = 0; slot < gridSize; slot++)
            workbench.setInventorySlotContentsSoftly(slot, inv.getStackInSlot(firstSlot+slot));
        return workbench;
    }

    public IArcaneRecipe findArcaneRecipe(EntityPlayer player)
    {
        TileMagicWorkbench workbench = getProxyWorkbench();
        IArcaneRecipe arcaneRecipe = null;
        for (Object o : ThaumcraftApi.getCraftingRecipes())
        {
            if (o instanceof IArcaneRecipe)
            {
                IArcaneRecipe thisArcaneRecipe = (IArcaneRecipe) o;
                if (((IArcaneRecipe) o).matches(workbench, player.worldObj, player))
                {
                    arcaneRecipe = thisArcaneRecipe;
                    break;
                }
            }
        }
        return arcaneRecipe;
    }

    public AspectList getRecipeCost(IArcaneRecipe recipe)
    {
        AspectList aspectList = new AspectList();
        if (recipe != null)
            aspectList = recipe.getAspects(getProxyWorkbench());
        return aspectList;
    }

    public ItemStack getResult(IArcaneRecipe recipe)
    {
        ItemStack result = null;
        if (recipe != null)
            result = recipe.getCraftingResult(getProxyWorkbench());
        return result;
    }
}
