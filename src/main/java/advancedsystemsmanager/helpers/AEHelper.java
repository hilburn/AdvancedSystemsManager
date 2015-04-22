package advancedsystemsmanager.helpers;

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
    /**
     * Get the full {@link IGrid}
     *
     * @param node the {@link IGridNode} to get the {@link IGrid} from
     * @return the {@link IGrid} the {@link IGridNode} is in
     * @throws GridAccessException
     */
    public static IGrid getGrid(IGridNode node) throws GridAccessException
    {
        if (node == null)
            throw new GridAccessException();
        IGrid grid = node.getGrid();
        if (grid == null)
            throw new GridAccessException();
        return grid;
    }

    /**
     * Get the {@link ITickManager}
     *
     * @param node the {@link IGridNode} to get the {@link ITickManager} from
     * @return the {@link ITickManager} for the {@link IGridNode}
     * @throws GridAccessException
     */
    public static ITickManager getTick(IGridNode node) throws GridAccessException
    {
        IGrid grid = getGrid(node);
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
     * @param node the {@link IGridNode} to get the {@link IStorageGrid} from
     * @return the {@link IStorageGrid} for the {@link IGridNode}
     * @throws GridAccessException
     */
    public static IStorageGrid getStorage(IGridNode node) throws GridAccessException
    {
        IGrid grid = getGrid(node);
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
     * @param node the {@link IGridNode} to get the {@link P2PCache} from
     * @return the {@link P2PCache} for the {@link IGridNode}
     * @throws GridAccessException
     */
    public static P2PCache getP2P(IGridNode node) throws GridAccessException
    {
        IGrid grid = getGrid(node);
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
     * @param node the {@link IGridNode} to get the {@link ISecurityGrid} from
     * @return the {@link ISecurityGrid} for the {@link IGridNode}
     * @throws GridAccessException
     */
    public static ISecurityGrid getSecurity(IGridNode node) throws GridAccessException
    {
        IGrid grid = getGrid(node);
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
     * @param node the {@link IGridNode} to get the {@link ICraftingGrid} from
     * @return the {@link ICraftingGrid} for the {@link IGridNode}
     * @throws GridAccessException
     */
    public static ICraftingGrid getCrafting(IGridNode node) throws GridAccessException
    {
        IGrid grid = getGrid(node);
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
     * @param node  the {@link IGridNode} used to insert the {@link ItemStack}
     * @param stack the to insert {@link ItemStack}
     * @return true or false whether is can be inserted or not
     */
    public static boolean canInsert(IGridNode node, ItemStack stack)
    {
        try
        {
            return getStorage(node).getItemInventory().canAccept(AEItemStack.create(stack));
        } catch (GridAccessException e)
        {
            return false;
        }
    }

    /**
     * Insert given {@link ItemStack}
     *
     * @param node  the {@link IGridNode} used to insert the {@link ItemStack}
     * @param stack the to insert {@link ItemStack}
     * @param host  the {@link IActionHost} to insert from
     * @return true if inserted
     */
    public static IAEItemStack insert(IGridNode node, ItemStack stack, IActionHost host, boolean simulate)
    {
        if (canInsert(node, stack))
        {
            try
            {
                return getStorage(node).getItemInventory().injectItems(AEItemStack.create(stack), simulate ? Actionable.SIMULATE : Actionable.MODULATE, new MachineSource(host));
            } catch (GridAccessException ignored)
            {
            }
        }
        return null;
    }

    public static ItemStack getInsertable(IGridNode node, ItemStack stack, IActionHost host)
    {
        IAEItemStack aeStack = insert(node, stack, host, true);
        if (aeStack == null) return stack;
        ItemStack result = stack.copy();
        result.stackSize -= aeStack.getStackSize();
        if (result.stackSize <= 0) return null;
        return result;
    }

    /**
     * Extract given {@link ItemStack}
     *
     * @param node  the {@link IGridNode} used to extract the {@link ItemStack}
     * @param stack the to extract {@link ItemStack}
     * @param host  the {@link IActionHost} to extract from
     * @return the extracted {@link ItemStack} can be null
     */
    public static ItemStack extract(IGridNode node, ItemStack stack, IActionHost host)
    {
        try
        {
            return getStorage(node).getItemInventory().extractItems(AEItemStack.create(stack), Actionable.MODULATE, new MachineSource(host)).getItemStack();
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    /**
     * Extract given {@link appeng.util.item.AEItemStack}
     *
     * @param node  the {@link IGridNode} used to extract the {@link ItemStack}
     * @param stack the to extract {@link IAEItemStack}
     * @param host  the {@link IActionHost} to extract from
     * @return the extracted {@link IAEItemStack} can be null
     */
    public static IAEItemStack extract(IGridNode node, IAEItemStack stack, IActionHost host)
    {
        try
        {
            return getStorage(node).getItemInventory().extractItems(stack, Actionable.MODULATE, new MachineSource(host));
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    /**
     * Can the given {@link FluidStack} be inserted
     *
     * @param node  the {@link IGridNode} used to insert the {@link FluidStack}
     * @param stack the to insert {@link FluidStack}
     * @return true or false whether is can be inserted or not
     */
    public static boolean canInsert(IGridNode node, FluidStack stack)
    {
        try
        {
            return getStorage(node).getFluidInventory().canAccept(AEFluidStack.create(stack));
        } catch (GridAccessException e)
        {
            return false;
        }
    }

    /**
     * Insert given {@link FluidStack}
     *
     * @param node  the {@link IGridNode} used to insert the {@link FluidStack}
     * @param stack the to insert {@link FluidStack}
     * @param host  the {@link IActionHost} to insert from
     * @return true if inserted
     */
    public static IAEFluidStack insert(IGridNode node, FluidStack stack, IActionHost host, boolean simulate)
    {
        if (canInsert(node, stack))
        {
            try
            {
                return getStorage(node).getFluidInventory().injectItems(AEFluidStack.create(stack), simulate ? Actionable.SIMULATE : Actionable.MODULATE, new MachineSource(host));
            } catch (GridAccessException ignored)
            {
            }
        }
        return null;
    }

    /**
     * Extract given {@link FluidStack}
     *
     * @param node  the {@link IGridNode} used to extract the {@link FluidStack}
     * @param stack the to extract {@link FluidStack}
     * @param host  the {@link IActionHost} to extract from
     * @return the extracted {@link FluidStack} can be null
     */
    public static IAEFluidStack extract(IGridNode node, FluidStack stack, IActionHost host, boolean simulate)
    {
        try
        {
            return getStorage(node).getFluidInventory().extractItems(AEFluidStack.create(stack), simulate ? Actionable.SIMULATE : Actionable.MODULATE, new MachineSource(host));
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    /**
     * Extract given {@link IAEFluidStack}
     *
     * @param node  the {@link IGridNode} used to extract the {@link IAEFluidStack}
     * @param stack the to extract {@link IAEFluidStack}
     * @param host  the {@link IActionHost} to extract from
     * @return the extracted {@link IAEFluidStack} can be null
     */
    public static IAEFluidStack extract(IGridNode node, IAEFluidStack stack, IActionHost host)
    {
        try
        {
            return getStorage(node).getFluidInventory().extractItems(stack, Actionable.MODULATE, new MachineSource(host));
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    /**
     * Find an {@link ItemStack}
     *
     * @param node  the {@link IGridNode} to search for the {@link ItemStack}
     * @param stack the {@link ItemStack} to find
     * @return
     */
    public static ItemStack find(IGridNode node, ItemStack stack)
    {
        IAEItemStack aeItemStack = findAEStack(node, stack);
        if (aeItemStack == null) return null;
        return aeItemStack.getItemStack();
    }

    /**
     * Find an {@link AEItemStack}
     *
     * @param node  the {@link IGridNode} to search for the {@link ItemStack}
     * @param stack the {@link ItemStack} to find
     * @return
     */
    public static IAEItemStack findAEStack(IGridNode node, ItemStack stack)
    {
        try
        {
            return getStorage(node).getItemInventory().getStorageList().findPrecise(AEItemStack.create(stack));
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    /**
     * Find a {@link FluidStack}
     *
     * @param node  the {@link IGridNode} to search for the {@link FluidStack}
     * @param stack the {@link FluidStack} to find
     * @return
     */
    public static IAEFluidStack find(IGridNode node, FluidStack stack)
    {
        try
        {
            return getStorage(node).getFluidInventory().getStorageList().findPrecise(AEFluidStack.create(stack));
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    /**
     * Get the {@link Iterator<IAEItemStack>}
     *
     * @param node the {@link IGridNode} to get the list from
     * @return the {@link Iterator<IAEItemStack>} with all items for given {@link IGridNode}
     */
    public static Iterator<IAEItemStack> getItrItems(IGridNode node)
    {
        try
        {
            return getStorage(node).getItemInventory().getStorageList().iterator();
        } catch (GridAccessException e)
        {
            return null;
        }
    }

    /**
     * Get the {@link Iterator<IAEFluidStack>}
     *
     * @param node the {@link IGridNode} to get the list from
     * @return the {@link Iterator<IAEFluidStack>} with all fluids for given {@link IGridNode}
     */
    public static Iterator<IAEFluidStack> getItrFluids(IGridNode node)
    {
        try
        {
            return getStorage(node).getFluidInventory().getStorageList().iterator();
        } catch (GridAccessException e)
        {
            return null;
        }
    }
}
