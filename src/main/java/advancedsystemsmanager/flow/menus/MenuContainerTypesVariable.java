package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.registry.ConnectionSet;

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
