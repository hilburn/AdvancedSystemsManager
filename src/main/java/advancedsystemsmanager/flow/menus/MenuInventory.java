package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.util.ConnectionBlockType;

import java.util.List;

public class MenuInventory extends MenuContainer
{
    public MenuInventory(FlowComponent parent)
    {
        super(parent, ConnectionBlockType.INVENTORY);
    }

    @Override
    public String getName()
    {
        return Localization.INVENTORY_MENU.toString();
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty())
        {
            errors.add(Localization.NO_INVENTORY_ERROR.toString());
        }
    }
}
