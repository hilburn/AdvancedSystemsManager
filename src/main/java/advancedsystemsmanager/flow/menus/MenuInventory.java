package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.SystemTypeRegistry;

import java.util.List;

public class MenuInventory extends MenuContainer
{
    public MenuInventory(FlowComponent parent)
    {
        super(parent, SystemTypeRegistry.INVENTORY);
    }

    @Override
    public String getName()
    {
        return Names.INVENTORY_MENU;
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty())
        {
            errors.add(Names.NO_INVENTORY_ERROR);
        }
    }
}
