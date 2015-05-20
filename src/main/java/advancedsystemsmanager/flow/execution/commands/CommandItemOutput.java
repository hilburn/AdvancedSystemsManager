package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.*;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Null;
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
        super(ITEM_INPUT, Names.ITEM_INPUT, null);
    }

    @Override
    public void execute(FlowComponent command, int connectionId, IBufferProvider bufferProvider)
    {
        if (bufferProvider.containsBuffer(IBuffer.CRAFT_HIGH))
            outputFromBuffer(command, bufferProvider.getBuffer(IBuffer.CRAFT_HIGH));

        if (bufferProvider.containsBuffer(IBuffer.ITEM))
            outputFromBuffer(command, bufferProvider.getBuffer(IBuffer.ITEM));

        if (bufferProvider.containsBuffer(IBuffer.CRAFT_LOW))
            outputFromBuffer(command, bufferProvider.getBuffer(IBuffer.CRAFT_LOW));
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
        for (Iterator<SystemCoord> blockItr = getContainers(component.manager, (MenuContainer)component.menus.get(0)).listIterator(); blockItr.hasNext(); )
        {
            SystemCoord block = blockItr.next();
            IInventory inventory = block.tileEntity instanceof IInternalInventory ? Null.NULL_INVENTORY : (IInventory)block.tileEntity;
            Iterator<Map.Entry<Key<ItemStack>, IBufferElement<ItemStack>>> iterator = buffer.getOrderedIterator();
            while (iterator.hasNext())
            {
                IBufferElement<ItemStack> itemBufferElement = iterator.next().getValue();
                Setting<ItemStack> setting = isValid(validSettings, itemBufferElement.getContent());
                boolean whitelist = menuItem.useWhiteList();
                if (setting == null && whitelist) continue;
                ItemStack itemStack = itemBufferElement.getContent();
                if (block.tileEntity instanceof IInternalInventory)
                {
                    IInternalInventory internal = (IInternalInventory)block.tileEntity;
                    int moveCount = Math.min(internal.getAmountToInsert(itemStack), itemStack.stackSize);
                    if (moveCount > 0)
                    {
                        moveCount = Math.min(itemBufferElement.getSizeLeft(), moveCount);
                        //moveCount = outputItemCounter.retrieveItemCount(moveCount);
                        moveCount = itemBufferElement.getMaxWithSetting(moveCount);
                        if (moveCount > 0)
                        {
                            itemBufferElement.reduceBufferAmount(moveCount);
                            //outputItemCounter.modifyStackSize(moveCount);
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

                }else
                {
                    Set<Integer> slots;
                    if (!cachedSlots.containsKey(inventory))
                    {
                        slots = new TreeSet<Integer>();
                        for (int side : validSides)
                        {
                            int start = target.useRangeForDirections[side] ? target.getStart(side) : 0;
                            int end = target.useRangeForDirections[side] ? target.getEnd(side) : inventory.getSizeInventory();

                            if (inventory instanceof ISidedInventory)
                            {
                                for (int slot : ((ISidedInventory)inventory).getAccessibleSlotsFromSide(side)) slots.add(slot);
                            } else
                            {
                                for (int slot = start; slot < end; slot++ ) slots.add(slot);
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
                        ItemStack itemInSlot = inventory.getStackInSlot(slot);
                        boolean newItem = itemInSlot == null;
                        if (newItem || itemInSlot.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(itemStack, itemInSlot) && itemStack.isStackable())
                        {
                            int itemCountInSlot = newItem ? 0 : itemInSlot.stackSize;
                            int moveCount = Math.min(itemBufferElement.getSizeLeft(), Math.min(inventory.getInventoryStackLimit(), itemStack.getMaxStackSize()) - itemCountInSlot);
                            //moveCount = outputItemCounter.retrieveItemCount(moveCount);
                            moveCount = itemBufferElement.getMaxWithSetting(moveCount);
                            if (moveCount > 0)
                            {
                                if (newItem)
                                {
                                    itemInSlot = itemStack.copy();
                                    itemInSlot.stackSize = 0;
                                }

                                itemBufferElement.reduceBufferAmount(moveCount);
                                //outputItemCounter.modifyStackSize(moveCount);
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

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuContainer(component, SystemTypeRegistry.INVENTORY));
        menus.add(new MenuTargetInventory(component));
        menus.add(new MenuItem(component, false));
    }
}
