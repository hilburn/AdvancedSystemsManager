package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.reference.Names;

import java.util.HashSet;
import java.util.Set;


public class MenuVariableContainers extends MenuContainer
{
    public MenuVariableContainers(FlowComponent parent)
    {
        super(parent, null);
    }

    @Override
    public void initRadioButtons()
    {
        //nothing
    }

    @Override
    public String getName()
    {
        return Names.VARIABLE_CONTAINERS_MENU;
    }

    @Override
    public Set<ISystemType> getValidTypes()
    {
        MenuContainerTypes componentMenuContainerTypes = ((MenuContainerTypes)getParent().getMenus().get(1));

        if (componentMenuContainerTypes.isVisible())
        {
            return componentMenuContainerTypes.getValidTypes();
        } else
        {
            int variableId = ((MenuVariable)getParent().getMenus().get(0)).getSelectedVariable();
            Variable variable = getParent().getManager().getVariables()[variableId];
            if (variable.isValid())
            {
                return ((MenuContainerTypes)variable.getDeclaration().getMenus().get(1)).getValidTypes();
            } else
            {
                return new HashSet<ISystemType>();
            }
        }
    }

    @Override
    public boolean isVariableAllowed(Set<ISystemType> validTypes, int i)
    {
        return super.isVariableAllowed(validTypes, i) && i != ((MenuVariable)getParent().getMenus().get(0)).getSelectedVariable();
    }
}
