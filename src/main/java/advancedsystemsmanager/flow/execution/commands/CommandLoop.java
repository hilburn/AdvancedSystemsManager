package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.flow.execution.Executor;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuContainerTypes;
import advancedsystemsmanager.flow.menus.MenuListOrder;
import advancedsystemsmanager.flow.menus.MenuVariableLoop;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.util.SystemCoord;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class CommandLoop extends CommandBase
{
    public CommandLoop()
    {
        super(FOR_EACH, Names.FOR_EACH_LOOP, CommandType.COMMAND_CONTROL, ConnectionSet.FOR_EACH);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuVariableLoop(component));
        menus.add(new MenuContainerTypes(component));
        menus.add(new MenuListOrder(component));
    }

    @Override
    public void execute(FlowComponent command, int connectionId, Executor executor)
    {
        Variable list = ((MenuVariableLoop)command.menus.get(0)).getVariable();
        if (list != null && list.isValid())
        {
            list.applyOrder((MenuListOrder) command.getMenus().get(2));
            Set<ISystemType> validTypes = ((MenuContainerTypes)command.getMenus().get(1)).getValidTypes();

            for (int i = 0; i< list.getNumContainers(); i++)
            {
                list.setContainerIndex(i);
                SystemCoord inventory = command.getManager().getInventory(list.getContainer());
                if (inventory != null && inventory.isOfAnyType(validTypes))
                {
                    executor.executeChildCommands(command, EnumSet.of(ConnectionOption.FOR_EACH));
                }
            }
            list.setContainerIndex(-1);
        }
    }
}
