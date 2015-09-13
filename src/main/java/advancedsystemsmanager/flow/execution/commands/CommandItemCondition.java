package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.tileentities.IInternalInventory;
import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuInventoryCondition;
import advancedsystemsmanager.flow.menus.MenuItemCondition;
import advancedsystemsmanager.flow.menus.MenuTargetInventory;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandItemCondition extends CommandCondition<ItemStack, MenuTargetInventory>
{
    public CommandItemCondition()
    {
        super(ITEM_CONDITION, Names.ITEM_CONDITION);
    }

    @Override
    public void searchForStuff(SystemCoord block, List<Setting<ItemStack>> settings, MenuTargetInventory target, Set<Setting<ItemStack>> found)
    {
        TileEntity entity = block.getTileEntity();
        if (entity instanceof IInternalInventory)
        {

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
                    int end = target.advancedDirections[i] ? target.getEnd(i) : maxSize - 1;

                    int[] slots;
                    if (inventory instanceof ISidedInventory)
                    {
                        slots = ((ISidedInventory)inventory).getAccessibleSlotsFromSide(i);
                    } else
                    {
                        slots = new int[end - start + 1];
                        for (int j = 0; j < slots.length; ) slots[j + start] = j++;
                    }
                    scanSlots(inventory, checkedSlots, slots, settings, start, end, found);
                }
            }
        }
    }

    private void scanSlots(IInventory inventory, List<Integer> checked, int[] toScan, List<Setting<ItemStack>> settings, int start, int end, Set<Setting<ItemStack>> found)
    {
        for (int slot : toScan)
        {
            if (slot < start || checked.contains(slot)) continue;
            if (slot > end) return;
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) continue;
            Setting<ItemStack> setting = isValid(settings, stack);
            if (setting != null)
            {
                if (found != null)
                {
                    found.add(setting);
                }
                setting.reduceAmount(stack.stackSize);
            }
            checked.add(slot);
        }
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuInventoryCondition(component));
        menus.add(new MenuTargetInventory(component));
        menus.add(new MenuItemCondition(component));
    }
}
