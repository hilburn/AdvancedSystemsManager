package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.flow.menus.MenuRedstoneSidesNodes;
import advancedsystemsmanager.flow.menus.MenuRedstoneStrengthNodes;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;

import java.util.ArrayList;
import java.util.List;

public class CommandRedstoneCondition extends CommandBase
{
    public CommandRedstoneCondition()
    {
        super(REDSTONE_CONDITION, Names.REDSTONE_CONDITION, CommandType.COMMAND_CONTROL, ConnectionSet.STANDARD_CONDITION);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuContainer(component, SystemTypeRegistry.NODE));
        menus.add(new MenuRedstoneSidesNodes(component));
        menus.add(new MenuRedstoneStrengthNodes(component));
    }

    @Override
    public List<Connection> getActiveChildren(FlowComponent command, int connectionId)
    {
        List<Connection> connections = new ArrayList<Connection>();
        int connection = TileEntityManager.redstoneCondition.isTriggerPowered(command, true) ? 1 : 2;
        if (command.getConnection(connection) != null) connections.add(command.getConnection(connection));
        return connections;
    }
}
