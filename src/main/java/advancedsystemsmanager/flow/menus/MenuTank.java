package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.util.ConnectionBlockType;

import java.util.List;


public class MenuTank extends MenuContainer
{
    public MenuTank(FlowComponent parent)
    {
        super(parent, ConnectionBlockType.TANK);
    }

    @Override
    public String getName()
    {
        return Localization.TANK_MENU.toString();
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty())
        {
            errors.add(Localization.NO_TANK_ERROR.toString());
        }
    }
}
