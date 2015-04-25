package advancedsystemsmanager.api.execution;

import advancedsystemsmanager.flow.execution.ConditionSettingChecker;
import advancedsystemsmanager.flow.menus.MenuItem;
import advancedsystemsmanager.flow.setting.Setting;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IInternalInventory
{
    int getInsertable(ItemStack stack);

    void insertItemStack(ItemStack stack);

    <Type> List<IBufferSubElement<Type>> getSubElements(int id, MenuItem menuItem);

    void isItemValid(Collection<Setting> settings, Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap);
}
