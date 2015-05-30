package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import net.minecraft.tileentity.TileEntity;

import java.util.HashSet;
import java.util.Set;


public class MenuVariableContainers extends MenuContainer
{
    public MenuVariableContainers(FlowComponent parent)
    {
        super(parent, SystemTypeRegistry.VARIABLE);
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
            Variable variable = ((MenuVariable)getParent().getMenus().get(0)).getVariable();
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
    public boolean isVariableAllowed(Set<ISystemType> validTypes, Variable variable)
    {
        return super.isVariableAllowed(validTypes, variable) && variable != ((MenuVariable)getParent().getMenus().get(0)).getVariable();
    }
}
