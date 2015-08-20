package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.*;

import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.util.SystemCoord;

import java.util.List;

//TODO: WIP command that treats the comparator input of any block as a redstone condition.
public class CommandComparator extends CommandBase
{
    public CommandComparator()
    {
        super(165467, "Test", CommandType.COMMAND_CONTROL, ConnectionSet.STANDARD_CONDITION);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuContainerTypes(component));
        menus.add(new MenuMultiContainers(component));
        menus.add(new MenuRedstoneSidesNodes(component));
        menus.add(new MenuRedstoneStrengthNodes(component));
    }

    @Override
    public List<Connection> getActiveChildren(FlowComponent command, int connectionId)
    {
        List<SystemCoord> blocks = getContainers(command.getManager(), (MenuContainer)command.getMenus().get(1));

        return super.getActiveChildren(command, connectionId);
    }
}
