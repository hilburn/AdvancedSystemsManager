package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.Executor;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuSplit;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.registry.ConnectionSet;

import java.util.*;

public class CommandSplit extends CommandBase
{
    public CommandSplit()
    {
        super(FLOW_CONTROL, Names.FLOW_CONTROL, CommandType.COMMAND_CONTROL, ConnectionSet.MULTIPLE_INPUT_2, ConnectionSet.MULTIPLE_INPUT_5, ConnectionSet.MULTIPLE_OUTPUT_2, ConnectionSet.MULTIPLE_OUTPUT_5);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuSplit(component));
    }

    @Override
    public void execute(FlowComponent command, int connectionId, Executor executor)
    {
        MenuSplit menu = (MenuSplit)command.menus.get(0);
        if (MenuSplit.isSplitConnection(command) && menu.useSplit())
        {
            List<Connection> connections = new ArrayList<Connection>();
            ConnectionOption[] connectionOptions = command.connectionSet.getConnections();
            int amount = 0;
            for (int i = 0; i < connectionOptions.length; i++)
            {
                Connection connection = command.getConnection(i);
                if ((connection != null || menu.useEmpty()) && connectionOptions[i].isOutput())
                {
                    amount++;
                    if (connection != null)
                    {
                        connections.add(connection);
                    }
                }
            }
            if (connections.size() > 1)
            {
                Set<String> buffers = executor.getBuffers();
                for (int i = 0; i < connections.size(); i++)
                {
                    Map<String, IBuffer> newBuffers = new HashMap<String, IBuffer>();
                    for (String bufferKey : buffers)
                    {
                        IBuffer buffer = executor.getBuffer(bufferKey);
                        newBuffers.put(bufferKey, buffer.split(buffer.shouldSplit() ? amount : 1, i, menu.useFair()));
                    }
                    FlowComponent output = command.getManager().getFlowItem(connections.get(i).getOutputId());
                    if (output != null)
                    {
                        new Executor(command.manager, newBuffers, new HashSet<Integer>(executor.usedCommands)).executeCommand(output, connections.get(i).getOutputConnection());
                    }
                }
            } else if (connections.size() == 1)
            {
                FlowComponent output = command.getManager().getFlowItem(connections.get(0).getOutputId());
                if (output != null)
                {
                    executor.executeCommand(output, connections.get(0).getOutputConnection());
                }
            }
        }
    }

    @Override
    public List<Connection> getActiveChildren(FlowComponent command, int connectionId)
    {
        MenuSplit menu = (MenuSplit)command.menus.get(0);
        if (MenuSplit.isSplitConnection(command) && menu.useSplit())
        {
            return new ArrayList<Connection>();
        }
        return super.getActiveChildren(command, connectionId);
    }
}
