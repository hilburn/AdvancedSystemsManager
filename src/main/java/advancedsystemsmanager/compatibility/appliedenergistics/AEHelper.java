package advancedsystemsmanager.compatibility.appliedenergistics;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.ITickManager;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.GridAccessException;
import appeng.me.cache.P2PCache;
import appeng.util.item.AEFluidStack;
import appeng.util.item.AEItemStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Iterator;

public class AEHelper
{
    IActionHost host;

    public AEHelper(IActionHost host)
    {
        this.host = host;
    }

    /**
     * Get the full {@link IGrid}
     *
     * @return the {@link IGrid} the {@link IGridNode} is in
     * @throws GridAccessException
     */
    public IGrid getGrid() throws GridAccessException
    {
        if (host.getActionableNode() == null)
            throw new GridAccessException();
        IGrid grid = host.getActionableNode().getGrid();
        if (grid == null)
            throw new GridAccessException();
        return grid;
    }

    /**
     * Get the {@link ITickManager}
     *
     * @return the {@link ITickManager} for the {@link IGridNode}
     * @throws GridAccessException
     */
    public ITickManager getTick() throws GridAccessException
    {
        IGrid grid = getGrid();
        if (grid == null)
            throw new GridAccessException();
        ITickManager pg = grid.getCache(ITickManager.class);
        if (pg == null)
            throw new GridAccessException();
        return pg;
    }

    /**
     * Get the {@link IStorageGrid}
     *
     * @return the {@link IStorageGrid} for the {@link IGridNode}
     * @throws GridAccessException
     */
    public IStorageGrid getStorage() throws GridAccessException
    {
        IGrid grid = getGrid();
        if (grid == null)
            throw new GridAccessException();

        IStorageGrid pg = grid.getCache(IStorageGrid.class);

        if (pg == null)
            throw new GridAccessException();

        return pg;
    }

    /**
     * Get the {@link P2PCache}
     *
     * @return the {@link P2PCache} for the {@link IGridNode}
     * @throws GridAccessException
     */
    public P2PCache getP2P() throws GridAccessException
    {
        IGrid grid = getGrid();
        if (grid == null)
            throw new GridAccessException();

        P2PCache pg = grid.getCache(P2PCache.class);

        if (pg == null)
            throw new GridAccessException();

        return pg;
    }

    /**
     * Get the {@link ISecurityGrid}
     *
     * @return the {@link ISecurityGrid} for the {@link IGridNode}
     * @throws GridAccessException
     */
    public ISecurityGrid getSecurity() throws GridAccessException
    {
        IGrid grid = getGrid();
        if (grid == null)
            throw new GridAccessException();

        ISecurityGrid sg = grid.getCache(ISecurityGrid.class);

        if (sg == null)
            throw new GridAccessException();

        return sg;
    }

    /**
     * Get the {@link ICraftingGrid}
     *
     * @return the {@link ICraftingGrid} for the {@link IGridNode}
     * @throws GridAccessException
     */
    public ICraftingGrid getCrafting() throws GridAccessException
    {
        IGrid grid = getGrid();
        if (grid == null)
            throw new GridAccessException();

        ICraftingGrid sg = grid.getCache(ICraftingGrid.class);

        if (sg == null)
            throw new GridAccessException();

        return sg;
    }

    /**
     * Can the given {@link ItemStack} be inserted
     *
     * @param stack the to insert {@link ItemStack}
     * @return true or false whether is can be inserted or not
     */
    public boolean canInsert(ItemStack stack)
    {
        try
        {
            return getStorage().getItemInventory().canAccept(AEItemStack.create(stack));
        } catch (GridAccessException e)
        {
            return false;
        }
    }

    /**
     * Insert given {@link ItemStack}
     *
     * @param stack the to insert {@link ItemStack}
     * @return true if inserted
     */
    public IAEItemStack insert(ItemStack stack, boolean simulate)
    {
        if (canInsert(stack))
        {
            try
            {
                return getStorage().getItemInventory().injectItems(AEItemStack.create(stack), simulate ? Actionable.SIMULATE : Actionable.MODULATE, new MachineSource(host));
            } catch (GridAccessException ignored)
            {
            }
        }
        return null;
    }

    public ItemStack getInsertable(ItemStack stack)
    {
        IAEItemStack aeStack = insert(stack, true);
        if (aeStack == null) return stack;
        ItemStack result = stack.copy();
        result.stackSize -= aeStack.getStackSize();
        if (result.stackSize <= 0) return null;
        return result;
    }

    /**
     * Extract given {@link ItemStack}
     *
     * @param stack the to extract {@link ItemStack}
     * @return the extracted {@link ItemStack} can be null
     */
    public ItemStack extract(ItemStack stack)
    {
        try
        {
            return getStorage().getItemInventory().extractItems(AEItemStack.create(stack), Actionable.MODULATE, new MachineSource(host)).getItemStack();
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    /**
     * Extract given {@link appeng.util.item.AEItemStack}
     *
     * @param stack the to extract {@link IAEItemStack}
     * @return the extracted {@link IAEItemStack} can be null
     */
    public IAEItemStack extract(IAEItemStack stack)
    {
        try
        {
            return getStorage().getItemInventory().extractItems(stack, Actionable.MODULATE, new MachineSource(host));
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    /**
     * Can the given {@link FluidStack} be inserted
     *
     * @param stack the to insert {@link FluidStack}
     * @return true or false whether is can be inserted or not
     */
    public boolean canInsert(FluidStack stack)
    {
        try
        {
            return getStorage().getFluidInventory().canAccept(AEFluidStack.create(stack));
        } catch (GridAccessException e)
        {
            return false;
        }
    }

    /**
     * Insert given {@link FluidStack}
     *
     * @param stack the to insert {@link FluidStack}
     * @return true if inserted
     */
    public IAEFluidStack insert(FluidStack stack, boolean simulate)
    {
        if (canInsert(stack))
        {
            try
            {
                return getStorage().getFluidInventory().injectItems(AEFluidStack.create(stack), simulate ? Actionable.SIMULATE : Actionable.MODULATE, new MachineSource(host));
            } catch (GridAccessException ignored)
            {
            }
        }
        return null;
    }

    /**
     * Extract given {@link FluidStack}
     *
     * @param stack the to extract {@link FluidStack}
     * @return the extracted {@link FluidStack} can be null
     */
    public IAEFluidStack extract(FluidStack stack, boolean simulate)
    {
        try
        {
            return getStorage().getFluidInventory().extractItems(AEFluidStack.create(stack), simulate ? Actionable.SIMULATE : Actionable.MODULATE, new MachineSource(host));
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    /**
     * Extract given {@link IAEFluidStack}
     *
     * @param stack the to extract {@link IAEFluidStack}
     * @return the extracted {@link IAEFluidStack} can be null
     */
    public IAEFluidStack extract(IAEFluidStack stack)
    {
        try
        {
            return getStorage().getFluidInventory().extractItems(stack, Actionable.MODULATE, new MachineSource(host));
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    /**
     * Find an {@link ItemStack}
     *
     * @param stack the {@link ItemStack} to find
     * @return
     */
    public ItemStack find(ItemStack stack)
    {
        IAEItemStack aeItemStack = findAEStack(stack);
        if (aeItemStack == null) return null;
        return aeItemStack.getItemStack();
    }

    /**
     * Find an {@link AEItemStack}
     *
     * @param stack the {@link ItemStack} to find
     * @return
     */
    public IAEItemStack findAEStack(ItemStack stack)
    {
        try
        {
            return getStorage().getItemInventory().getStorageList().findPrecise(AEItemStack.create(stack));
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    /**
     * Find a {@link FluidStack}
     *
     * @param stack the {@link FluidStack} to find
     * @return
     */
    public IAEFluidStack find(FluidStack stack)
    {
        try
        {
            return getStorage().getFluidInventory().getStorageList().findPrecise(AEFluidStack.create(stack));
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    /**
     * Get the {@link Iterator<IAEItemStack>}
     *
     * @return the {@link Iterator<IAEItemStack>} with all items for given {@link IGridNode}
     */
    public Iterator<IAEItemStack> getItrItems()
    {
        try
        {
            return getStorage().getItemInventory().getStorageList().iterator();
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    /**
     * Get the {@link Iterator<IAEFluidStack>}
     *
     * @return the {@link Iterator<IAEFluidStack>} with all fluids for given {@link IGridNode}
     */
    public Iterator<IAEFluidStack> getItrFluids()
    {
        try
        {
            return getStorage().getFluidInventory().getStorageList().iterator();
        } catch (GridAccessException e)
        {
            return null;
        }
    }
}
