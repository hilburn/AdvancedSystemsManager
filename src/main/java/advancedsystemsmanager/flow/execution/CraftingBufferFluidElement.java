package advancedsystemsmanager.flow.execution;

import advancedsystemsmanager.api.execution.IItemBufferElement;
import advancedsystemsmanager.api.execution.IItemBufferSubElement;
import advancedsystemsmanager.flow.menus.MenuContainerScrap;
import advancedsystemsmanager.flow.menus.MenuCrafting;
import advancedsystemsmanager.flow.setting.CraftingSetting;
import advancedsystemsmanager.flow.setting.FuzzyMode;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CraftingBufferFluidElement implements IItemBufferElement, IItemBufferSubElement
{
    public static final ItemStack DUMMY_ITEM = new ItemStack(Item.getItemById(1), 0, 0);
    public static final double SPEED_MULTIPLIER = 0.05000000074505806D;
    public static final Random rand = new Random();
    public CommandExecutor executor;
    public MenuCrafting craftingMenu;
    public MenuContainerScrap scrapMenu;
    public IRecipe recipe;
    public ItemStack result;
    public boolean isCrafting;
    public boolean justRemoved;
    public int overflowBuffer;
    public List<ItemStack> containerItems;
    public List<IInventory> inventories = new ArrayList<IInventory>();

    public CraftingBufferFluidElement(CommandExecutor executor, MenuCrafting craftingMenu, MenuContainerScrap scrapMenu)
    {
        this.executor = executor;
        this.craftingMenu = craftingMenu;
        this.scrapMenu = scrapMenu;
        this.recipe = craftingMenu.getDummy().getRecipe();
        this.result = this.recipe == null ? null : this.recipe.getCraftingResult(craftingMenu.getDummy());
        this.containerItems = new ArrayList<ItemStack>();
    }

    @Override
    public void prepareSubElements()
    {
        this.isCrafting = true;
        this.justRemoved = false;
    }

    @Override
    public IItemBufferSubElement getSubElement()
    {
        if (this.isCrafting && this.result != null)
        {
            this.isCrafting = false;
            return this;
        } else
        {
            return null;
        }
    }

    @Override
    public void removeSubElement()
    {
    }

    @Override
    public int retrieveItemCount(int moveCount)
    {
        return moveCount;
    }

    @Override
    public void decreaseStackSize(int moveCount)
    {
    }

    @Override
    public void releaseSubElements()
    {
        if (this.result != null)
        {
            if (this.overflowBuffer > 0)
            {
                ItemStack stack = this.result.copy();
                stack.stackSize = this.overflowBuffer;
                this.disposeOfExtraItem(stack);
                this.overflowBuffer = 0;
            }

            for (ItemStack containerItem : this.containerItems)
            {
                this.disposeOfExtraItem(containerItem);
            }

            this.containerItems.clear();
        }

    }

    public void disposeOfExtraItem(ItemStack itemStack)
    {
        TileEntityManager manager = this.craftingMenu.getParent().getManager();
        List<SlotInventoryHolder> inventories = CommandExecutor.getContainers(manager, this.scrapMenu, SystemTypeRegistry.INVENTORY);

        for (SlotInventoryHolder inventoryHolder : inventories)
        {
            IInventory inventory = inventoryHolder.getInventory();

            for (int i = 0; i < inventory.getSizeInventory(); ++i)
            {
                if (inventory.isItemValidForSlot(i, itemStack))
                {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (stack == null || stack.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(itemStack, stack) && itemStack.isStackable())
                    {
                        int itemCountInSlot = stack == null ? 0 : stack.stackSize;
                        int stackSize = Math.min(itemStack.stackSize, Math.min(inventory.getInventoryStackLimit(), itemStack.getMaxStackSize()) - itemCountInSlot);
                        if (stackSize > 0)
                        {
                            if (stack == null)
                            {
                                stack = itemStack.copy();
                                stack.stackSize = 0;
                                inventory.setInventorySlotContents(i, stack);
                            }

                            stack.stackSize += stackSize;
                            itemStack.stackSize -= stackSize;
                            inventory.markDirty();
                            if (itemStack.stackSize == 0)
                            {
                                return;
                            }
                        }
                    }
                }
            }
        }

        double x = (double)manager.xCoord + rand.nextDouble() * 0.8D + 0.1D;
        double y = (double)manager.xCoord + rand.nextDouble() * 0.3D + 1.1D;
        double z = (double)manager.xCoord + rand.nextDouble() * 0.8D + 0.1D;
        EntityItem item = new EntityItem(manager.getWorldObj(), x, y, z, itemStack);
        item.motionX = rand.nextGaussian() * SPEED_MULTIPLIER;
        item.motionY = (rand.nextGaussian() + 4) * SPEED_MULTIPLIER;
        item.motionZ = rand.nextGaussian() * SPEED_MULTIPLIER;
        manager.getWorldObj().spawnEntityInWorld(item);
    }

    @Override
    public void remove()
    {
    }

    @Override
    public void onUpdate()
    {
        for (IInventory inventory : this.inventories)
        {
            inventory.markDirty();
        }
        this.inventories.clear();
    }

    @Override
    public int getSizeLeft()
    {
        if (!this.justRemoved)
        {
            return this.overflowBuffer > 0 ? this.overflowBuffer : (this.findItems(false) ? this.result.stackSize : 0);
        } else
        {
            this.justRemoved = false;
            return 0;
        }
    }

    @Override
    public int reduceBufferAmount(int amount)
    {
        this.justRemoved = true;
        if (this.overflowBuffer > 0)
        {
            this.overflowBuffer -= amount;
        } else
        {
            this.findItems(true);
            this.overflowBuffer = this.result.stackSize - amount;
        }

        this.isCrafting = true;
        return amount;
    }

    @Override
    public ItemStack getKey()
    {
        if (this.useAdvancedDetection())
        {
            this.findItems(false);
        }

        return this.result;
    }

    @Override
    public IInventory getContainer()
    {
        return null;
    }

    @Override
    public IItemBufferSubElement getSplitElement(int elementAmount, int id, boolean fair)
    {
        return null;
    }

    @Override
    public int getSlot()
    {
        return 0;
    }

    public boolean findItems(boolean remove)
    {
        List<CraftingSetting> settings = new ArrayList<CraftingSetting>();
        for (Setting setting : this.craftingMenu.getSettings()) settings.add((CraftingSetting)setting);

        HashMap<Integer, ItemStack> foundItems = new HashMap<Integer, ItemStack>();

//        for (ItemBufferElement itemBufferElement : this.executor.itemBuffer)
//        {
//            int count = itemBufferElement.retrieveItemCount(9);
//            Iterator iterator = itemBufferElement.getSubElements().iterator();
//
//            while (iterator.hasNext())
//            {
//                IItemBufferSubElement itemBufferSubElement = (IItemBufferSubElement)iterator.next();
//                ItemStack itemstack = itemBufferSubElement.getKey();
//                int subCount = Math.min(count, itemBufferSubElement.getSizeLeft());
//
//                for (int i = 0; i < 9; ++i)
//                {
//                    CraftingSetting setting = settings.get(i);
//                    if (foundItems.get(i) == null)
//                    {
//                        if (!setting.isValid())
//                        {
//                            foundItems.put(i, DUMMY_ITEM);
//                            break;
//                        }
//                        if (subCount > 0 && setting.isEqualForCommandExecutor(itemstack))
//                        {
//                            foundItems.put(i, itemstack.copy());
//                            if (this.craftingMenu.getDummy().isItemValidForRecipe(this.recipe, this.craftingMenu.getResultItem(), foundItems, this.useAdvancedDetection()))
//                            {
//                                --subCount;
//                                --count;
//                                if (remove)
//                                {
//                                    if (itemstack.getItem().hasContainerItem(itemstack))
//                                    {
//                                        this.containerItems.add(itemstack.getItem().getContainerItem(itemstack));
//                                    }
//
//                                    itemBufferElement.decreaseStackSize(1);
//                                    itemBufferSubElement.reduceBufferAmount(1);
//                                    if (itemBufferSubElement.getSizeLeft() == 0)
//                                    {
//                                        itemBufferSubElement.remove();
//                                        iterator.remove();
//                                    }
//
//                                    this.inventories.add(((SlotStackInventoryHolder)itemBufferSubElement).getInventory());
//                                }
//                            } else
//                            {
//                                foundItems.remove(Integer.valueOf(i));
//                            }
//                        }
//                    }
//                }
//            }
//        }

        if (foundItems.size() < 9)
        {
            List<FluidElement> fluids = new ArrayList<FluidElement>();
            for (int i = 0; i < 9; i++)
            {
                CraftingSetting setting = settings.get(i);
                if (foundItems.get(i) == null && isBucket(setting))
                {
                    boolean newFluid = true;
                    for (FluidElement fluidElement : fluids)
                    {
                        if (fluidElement.bucket.isItemEqual(setting.getItem()))
                        {
                            fluidElement.amountToFind += fluidElement.fluid.amount;
                            fluidElement.slots.add(i);
                            newFluid = false;
                        }
                    }
                    if (newFluid) fluids.add(new FluidElement(setting.getItem().copy(), i));
                }
            }

            if (fluids.size() > 0)
            {
//                for (LiquidBufferElement liquidBufferElement : this.executor.liquidBuffer)
//                {
//                    if (fluids.isEmpty()) break;
//                    Iterator<StackTankHolder> itr = liquidBufferElement.getHolders().iterator();
//                    while (itr.hasNext())
//                    {
//                        StackTankHolder tank = itr.next();
//                        for (Iterator<FluidElement> fluidItr = fluids.iterator(); fluidItr.hasNext(); )
//                        {
//                            FluidElement fluidElement = fluidItr.next();
//                            int maxAmount = liquidBufferElement.retrieveItemCount(fluidElement.amountToFind);
//                            if (tank.getFluidStack().isFluidEqual(fluidElement.fluid))
//                            {
//                                maxAmount = Math.min(maxAmount, tank.getSizeLeft());
//                                fluidElement.amountToFind -= maxAmount;
//                                if (remove)
//                                {
//                                    tank.reduceAmount(maxAmount);
//                                    FluidStack toRemove = fluidElement.fluid;
//                                    toRemove.amount = maxAmount;
//                                    tank.getTank().drain(tank.getSide(), maxAmount, true);
//                                }
//                                if (fluidElement.amountToFind == 0)
//                                {
//                                    fluidItr.remove();
//                                    for (int i : fluidElement.slots)
//                                        foundItems.put(i, fluidElement.bucket);
//                                    break;
//                                }
//                            }
//                        }
//                        if (tank.getSizeLeft() == 0) itr.remove();
//                    }
//                }
            }
        }

        if (foundItems.size() == 9)
        {
            this.result = this.craftingMenu.getDummy().getResult(foundItems);
            this.result = this.result != null ? this.result.copy() : null;
            return true;
        } else
        {
            return false;
        }
    }

    public boolean useAdvancedDetection()
    {
        return this.craftingMenu.getResultItem().getFuzzyMode() != FuzzyMode.PRECISE;
    }

    public static boolean isBucket(CraftingSetting setting)
    {
        return FluidContainerRegistry.isBucket(setting.getItem());
    }

    public static class FluidElement
    {
        public FluidStack fluid;
        public ItemStack bucket;
        public int amountToFind;
        List<Integer> slots = new ArrayList<Integer>();

        public FluidElement(ItemStack bucket, int i)
        {
            this.bucket = bucket;
            this.fluid = FluidContainerRegistry.getFluidForFilledItem(bucket);
            amountToFind = this.fluid.amount;
            slots.add(i);
        }
    }
}
