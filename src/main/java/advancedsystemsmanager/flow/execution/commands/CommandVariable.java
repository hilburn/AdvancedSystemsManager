package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.flow.execution.Executor;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.util.SystemCoord;

import java.util.*;

public class CommandVariable extends CommandBase
{
    public CommandVariable()
    {
        super(VARIABLE, Names.CONTAINER_VARIABLE, CommandType.MISC, ConnectionSet.EMPTY); //, ConnectionSet.STANDARD
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuVariable(component));
        menus.add(new MenuContainerTypesVariable(component));
        menus.add(new MenuVariableContainers(component));
        menus.add(new MenuListOrderVariable(component));
    }

    @Override
    public void execute(FlowComponent command, int connectionId, Executor executor)
    {
        MenuVariable menuVariable = (MenuVariable)command.getMenu(0);
        MenuVariableContainers menuVariableContainers = (MenuVariableContainers)command.getMenu(2);
        Variable variable = command.getManager().getVariable(menuVariable.getSelectedVariable());

        if (variable.isValid())
        {
            if (menuVariable.isDeclaration())
            {
                variable.setContainers(new ArrayList<Long>());
                for (Iterator<Long> itr =  menuVariableContainers.getSelectedInventories().iterator(); itr.hasNext();)
                {
                    long coord = itr.next();
                    SystemCoord systemCoord = command.getManager().getInventory(coord);
                    if (systemCoord != null && systemCoord.isOfAnyType(menuVariableContainers.getValidTypes()))
                    {
                        variable.add(coord);
                    } else
                    {
                        itr.remove();
                    }
                }
            } else
            {
                MenuVariable.VariableMode mode = menuVariable.getVariableMode();
                boolean remove = mode == MenuVariable.VariableMode.REMOVE;
                if (!remove && mode != MenuVariable.VariableMode.ADD)
                {
                    variable.clearContainers();
                }

                List<Long> idList = menuVariableContainers.getSelectedInventories();
                idList = ((MenuListOrderVariable)command.menus.get(4)).applyOrder(idList);

                Set<ISystemType> validTypes = ((MenuContainerTypes)variable.getDeclaration().getMenus().get(1)).getValidTypes();
                for (long id : idList)
                {
                    if (remove)
                    {
                        variable.remove(id);
                        continue;
                    }
                    SystemCoord coord = command.getManager().getInventory(id);
                    if (coord != null && coord.isOfAnyType(validTypes))
                    {
                        variable.add(id);
                    }
                }
            }
        }
    }
}
