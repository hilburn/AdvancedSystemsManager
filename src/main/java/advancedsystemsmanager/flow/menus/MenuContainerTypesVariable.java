package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.flow.FlowComponent;

public class MenuContainerTypesVariable extends MenuContainerTypes
{
    public MenuContainerTypesVariable(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() == ConnectionSet.EMPTY;
    }
}
