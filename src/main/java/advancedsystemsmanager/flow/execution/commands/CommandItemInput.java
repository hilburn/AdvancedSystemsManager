package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.tileentities.IInternalInventory;
import advancedsystemsmanager.api.execution.Key;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.buffers.Buffer;
import advancedsystemsmanager.flow.execution.buffers.elements.ItemBufferElement;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.flow.menus.MenuItem;
import advancedsystemsmanager.flow.menus.MenuTargetInventory;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

public class CommandItemInput extends CommandInput<ItemStack>
{
    public CommandItemInput()
    {
        super(ITEM_INPUT, Names.ITEM_INPUT, IBuffer.ITEM);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuContainer(component, SystemTypeRegistry.INVENTORY));
        menus.add(new MenuTargetInventory(component));
        menus.add(new MenuItem(component));
    }

    @Override
    protected IBuffer getNewBuffer()
    {
        return new Buffer<ItemStack>()
        {
            @Override
            public Key<ItemStack> getKey(ItemStack key)
            {
                return new Key.ItemKey(key);
            }
        };
    }

    @Override
    protected List<IBufferElement<ItemStack>> getBufferSubElements(int id, List<SystemCoord> blocks, List<Menu> menus)
    {
        MenuTargetInventory target = (MenuTargetInventory)menus.get(1);
        MenuItem settings = (MenuItem)menus.get(2);
        List<Setting<ItemStack>> validSettings = getValidSettings(settings.settings);
        List<IBufferElement<ItemStack>> subElements = new ArrayList<IBufferElement<ItemStack>>();
        for (SystemCoord block : blocks)
        {
            TileEntity entity = block.tileEntity;
            if (entity instanceof IInternalInventory)
            {
                subElements.addAll(((IInternalInventory)entity).getSubElements(id, settings));
            } else
            {
                IInventory inventory = (IInventory)entity;
                List<Integer> checkedSlots = new ArrayList<Integer>();
                int maxSize = inventory.getSizeInventory();
                for (int i = 0; i < 6; i++)
                {
                    if (target.activatedDirections[i])
                    {
                        int start = target.advancedDirections[i] ? target.getStart(i) : 0;
                        int end = target.advancedDirections[i] ? target.getEnd(i) : maxSize;

                        int[] slots;
                        if (inventory instanceof ISidedInventory)
                        {
                            slots = ((ISidedInventory)inventory).getAccessibleSlotsFromSide(i);
                        } else
                        {
                            slots = new int[end - start];
                            for (int j = 0; j < slots.length; ) slots[j + start] = j++;
                        }
                        scanSlots(id, inventory, checkedSlots, slots, validSettings, settings.isFirstRadioButtonSelected(), start, end, subElements);
                    }
                }
            }
        }
        return subElements;
    }

    private void scanSlots(int id, IInventory inventory, List<Integer> checked, int[] toScan, List<Setting<ItemStack>> settings, boolean whitelist, int start, int end, List<IBufferElement<ItemStack>> subElements)
    {
        for (int slot : toScan)
        {
            if (slot < start || checked.contains(slot)) continue;
            if (slot > end) return;
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) continue;
            Setting<ItemStack> setting = isValid(settings, stack);
            if (!(setting == null && whitelist))
                subElements.add(new ItemBufferElement(id, inventory, slot, setting, whitelist));
            checked.add(slot);
        }
    }
}
