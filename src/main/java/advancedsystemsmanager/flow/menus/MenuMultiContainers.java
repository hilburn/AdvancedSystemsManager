package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.registry.SystemTypeRegistry;

import java.util.Set;

public class MenuMultiContainers extends MenuContainer
{
    public MenuMultiContainers(FlowComponent parent)
    {
        super(parent, SystemTypeRegistry.VARIABLE);
    }

    @Override
    public Set<ISystemType> getValidTypes()
    {
        MenuContainerTypes componentMenuContainerTypes = ((MenuContainerTypes)getParent().getMenus().get(0));
        return componentMenuContainerTypes.getValidTypes();
    }
}
