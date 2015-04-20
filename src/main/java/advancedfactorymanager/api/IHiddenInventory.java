package advancedfactorymanager.api;

import net.minecraft.item.ItemStack;
import advancedfactorymanager.components.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IHiddenInventory
{
    int getInsertable(ItemStack stack);

    void insertItemStack(ItemStack stack);

    void addItemsToBuffer(ComponentMenuStuff menuItem, SlotInventoryHolder inventory, List<ItemBufferElement> itemBuffer, CommandExecutorRF commandExecutorRF);

    void isItemValid(Collection<Setting> settings, Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap);
}
