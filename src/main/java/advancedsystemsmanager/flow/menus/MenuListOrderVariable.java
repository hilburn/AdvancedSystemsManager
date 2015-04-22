package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.flow.FlowComponent;

public class MenuListOrderVariable extends MenuListOrder
{
    public MenuListOrderVariable(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() == ConnectionSet.STANDARD;
    }

    @Override
    public String getName()
    {
        return Localization.VALUE_ORDER_MENU.toString();
    }
}
