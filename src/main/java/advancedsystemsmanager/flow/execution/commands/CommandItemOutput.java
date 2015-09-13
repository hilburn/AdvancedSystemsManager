package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.execution.Key;
import advancedsystemsmanager.api.tileentities.IInternalInventory;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.Executor;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.flow.menus.MenuItem;
import advancedsystemsmanager.flow.menus.MenuTargetInventory;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Null;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

import java.util.*;

public class CommandItemOutput extends CommandOutput<ItemStack>
{
    public CommandItemOutput()
    {
        super(ITEM_OUTPUT, Names.ITEM_OUTPUT, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(FlowComponent command, int connectionId, Executor executor)
    {
        if (executor.containsBuffer(IBuffer.CRAFT_HIGH))
            outputFromBuffer(command, executor.getBuffer(IBuffer.CRAFT_HIGH));

        if (executor.containsBuffer(IBuffer.ITEM))
            outputFromBuffer(command, executor.getBuffer(IBuffer.ITEM));

        if (executor.containsBuffer(IBuffer.CRAFT_LOW))
            outputFromBuffer(command, executor.getBuffer(IBuffer.CRAFT_LOW));
    }

    @Override
    protected void outputFromBuffer(FlowComponent component, IBuffer<ItemStack> buffer)
    {
        MenuItem menuItem = (MenuItem)component.menus.get(2);
        List<Setting<ItemStack>> validSettings = getValidSettings(menuItem.getSettings());
        List<Integer> validSides = new ArrayList<Integer>();
        MenuTargetInventory target = ((MenuTargetInventory)component.getMenus().get(1));
        Map<IInventory, Set<Integer>> cachedSlots = new HashMap<IInventory, Set<Integer>>();
        for (int i = 0; i < target.activatedDirections.length; i++)
            if (target.activatedDirections[i]) validSides.add(i);
        List<SystemCoord> blocks = getContainers(component.manager, (MenuContainer)component.menus.get(0));
        CommandRegistry.CONDITION.searchForStuff(blocks, validSettings, target, null);
        for (Iterator<SystemCoord> blockItr = blocks.iterator(); blockItr.hasNext(); )
        {
            SystemCoord block = blockItr.next();
            IInventory inventory = block.getTileEntity() instanceof IInternalInventory ? Null.NULL_INVENTORY : (IInventory)block.getTileEntity();
            Iterator<Map.Entry<Key<ItemStack>, IBufferElement<ItemStack>>> iterator = buffer.getOrderedIterator();
            while (iterator.hasNext())
            {
                IBufferElement<ItemStack> itemBufferElement = iterator.next().getValue();
                Setting<ItemStack> setting = isValid(validSettings, itemBufferElement.getContent());
                boolean whitelist = menuItem.useWhiteList();
                boolean outputCounter = setting != null && setting.isLimitedByAmount();
                if (!isValidSetting(whitelist, setting)) continue;
                ItemStack itemStack = itemBufferElement.getContent();
                if (block.getTileEntity() instanceof IInternalInventory)
                {
                    IInternalInventory internal = (IInternalInventory)block.getTileEntity();
                    int moveCount = Math.min(internal.getAmountToInsert(itemStack), itemStack.stackSize);
                    if (moveCount > 0)
                    {
                        moveCount = Math.min(itemBufferElement.getSizeLeft(), moveCount);
                        moveCount = itemBufferElement.getMaxWithSetting(moveCount);
                        if (moveCount > 0)
                        {
                            itemBufferElement.reduceBufferAmount(moveCount);
                            if (setting != null && setting.isLimitedByAmount())
                            {
                                setting.reduceAmount(moveCount);
                            }
                            ItemStack toInsert = itemStack.copy();
                            toInsert.stackSize = moveCount;
                            internal.insertItemStack(toInsert);

                            if (itemBufferElement.getSizeLeft() == 0)
                            {
                                iterator.remove();
                            }
                            itemBufferElement.onUpdate();
                        }
                    }

                } else
                {
                    Set<Integer> slots;
                    if (!cachedSlots.containsKey(inventory))
                    {
                        slots = new TreeSet<Integer>();
                        for (int side : validSides)
                        {
                            int start = target.advancedDirections[side] ? target.getStart(side) : 0;
                            int end = target.advancedDirections[side] ? target.getEnd(side) : inventory.getSizeInventory() - 1;

                            if (inventory instanceof ISidedInventory)
                            {
                                for (int slot : ((ISidedInventory)inventory).getAccessibleSlotsFromSide(side))
                                    slots.add(slot);
                            } else
                            {
                                for (int slot = start; slot <= end; slot++) slots.add(slot);
                            }
                        }
                        cachedSlots.put(inventory, slots);
                    }
                    slots = cachedSlots.get(inventory);
                    if (slots.isEmpty())
                    {
                        cachedSlots.remove(inventory);
                        blockItr.remove();
                        continue;
                    }

                    for (int slot : slots)
                    {
                        if (inventory.isItemValidForSlot(slot, itemStack))
                        {
                            ItemStack itemInSlot = inventory.getStackInSlot(slot);
                            boolean newItem = itemInSlot == null;
                            if (newItem || itemInSlot.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(itemStack, itemInSlot) && itemStack.isStackable())
                            {
                                int itemCountInSlot = newItem ? 0 : itemInSlot.stackSize;
                                int moveCount = Math.min(itemBufferElement.getSizeLeft(), Math.min(inventory.getInventoryStackLimit(), itemStack.getMaxStackSize()) - itemCountInSlot);
                                moveCount = itemBufferElement.getMaxWithSetting(moveCount);
                                if (outputCounter)
                                {
                                    moveCount = Math.min(moveCount, setting.getAmountLeft());
                                }
                                if (moveCount > 0)
                                {
                                    if (newItem)
                                    {
                                        itemInSlot = itemStack.copy();
                                        itemInSlot.stackSize = 0;
                                    }

                                    itemBufferElement.reduceBufferAmount(moveCount);
                                    if (outputCounter)
                                    {
                                        setting.reduceAmount(moveCount);
                                    }
                                    itemInSlot.stackSize += moveCount;
                                    if (newItem)
                                    {
                                        inventory.setInventorySlotContents(slot, itemInSlot);
                                    }

                                    boolean done = false;
                                    if (itemBufferElement.getSizeLeft() == 0)
                                    {
                                        iterator.remove();
                                        done = true;
                                    }

                                    itemBufferElement.onUpdate();
                                    if (done)
                                    {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuContainer(component, SystemTypeRegistry.INVENTORY));
        menus.add(new MenuTargetInventory(component));
        menus.add(new MenuItem(component, false));
    }
}
