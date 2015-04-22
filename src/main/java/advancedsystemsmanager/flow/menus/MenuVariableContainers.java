package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.util.ConnectionBlockType;

import java.util.EnumSet;


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
        return Localization.VARIABLE_CONTAINERS_MENU.toString();
    }

    @Override
    public EnumSet<ConnectionBlockType> getValidTypes()
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
                return EnumSet.noneOf(ConnectionBlockType.class);
            }
        }
    }

    @Override
    public boolean isVariableAllowed(EnumSet<ConnectionBlockType> validTypes, int i)
    {
        return super.isVariableAllowed(validTypes, i) && i != ((MenuVariable)getParent().getMenus().get(0)).getSelectedVariable();
    }
}
