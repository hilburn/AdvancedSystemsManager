package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;

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
        return Names.VALUE_ORDER_MENU;
    }
}
