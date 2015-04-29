package advancedsystemsmanager.flow.menus;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.SystemTypeRegistry;

import java.util.List;


public class MenuTank extends MenuContainer
{
    public MenuTank(FlowComponent parent)
    {
        super(parent, SystemTypeRegistry.TANK);
    }

    @Override
    public String getName()
    {
        return Names.TANK_MENU;
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty())
        {
            errors.add(Names.NO_TANK_ERROR);
        }
    }
}
