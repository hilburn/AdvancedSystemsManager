package advancedsystemsmanager.api.execution;

import advancedsystemsmanager.flow.execution.CommandExecutor;
import advancedsystemsmanager.flow.execution.ConditionSettingChecker;
import advancedsystemsmanager.flow.execution.ItemBufferElement;
import advancedsystemsmanager.flow.execution.SlotInventoryHolder;
import advancedsystemsmanager.flow.menus.MenuStuff;
import advancedsystemsmanager.flow.setting.Setting;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IHiddenInventory
{
    int getAmountToInsert(ItemStack stack);

    void insertItemStack(ItemStack stack);

    void addItemsToBuffer(MenuStuff menuItem, SlotInventoryHolder inventory, List<ItemBufferElement> itemBuffer, CommandExecutor commandExecutor);

    void isItemValid(Collection<Setting> settings, Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap);
}
