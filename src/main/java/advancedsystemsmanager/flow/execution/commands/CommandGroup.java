package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBufferProvider;
import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuGroup;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;

import java.util.ArrayList;
import java.util.List;

public class CommandGroup extends CommandBase
{
    public CommandGroup()
    {
        super(GROUP, Names.GROUP, CommandType.MISC, ConnectionSet.DYNAMIC);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuGroup(component));
    }

    @Override
    public void execute(FlowComponent command, int connectionId, IBufferProvider bufferProvider)
    {
    }

    @Override
    public List<Connection> getActiveChildren(FlowComponent command, int connectionId)
    {
        List<Connection> connections = new ArrayList<Connection>();
        if (connectionId < command.getChildrenInputNodes().size())
        {
            FlowComponent child = command.getChildrenInputNodes().get(connectionId);
            Connection connection = child.getConnection(0);
            if (connection != null)
            {
                connections.add(connection);
            }
        }
        return connections;
    }
}
