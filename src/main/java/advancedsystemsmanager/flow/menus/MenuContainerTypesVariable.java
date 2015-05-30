package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.util.SystemCoord;

import java.util.Iterator;
import java.util.Set;

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

    @Override
    protected void setChecked(int id, boolean value)
    {
        super.setChecked(id, value);
        MenuVariableContainers containers = (MenuVariableContainers)getParent().menus.get(2);
        Set<ISystemType> types = containers.getValidTypes();
        for (Iterator<Long> itr = containers.getSelectedInventories().iterator(); itr.hasNext();)
        {
            long selected = itr.next();
            if (selected < 0)
            {
                Variable variable = getParent().getManager().getVariable((int)selected);
                if (!containers.isVariableAllowed(types, variable)) itr.remove();
            } else
            {
                SystemCoord coord = getParent().getManager().getInventory(selected);
                if (coord == null || !coord.isOfAnyType(types)) itr.remove();
            }
        }
        containers.inventories = null;
        containers.scrollController.updateSearch();
    }
}
