package advancedsystemsmanager.flow.execution;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.flow.menus.MenuVariable;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;

import java.util.*;

public class Executor
{
    public TileEntityManager manager;
    public Set<Integer> usedCommands;
    private Map<String, IBuffer> buffers;

    public Executor(TileEntityManager manager)
    {
        this.manager = manager;
        this.buffers = new HashMap<String, IBuffer>();
        this.usedCommands = new HashSet<Integer>();
    }

    public Executor(TileEntityManager manager, Map<String, IBuffer> buffers, Set<Integer> usedCommands)
    {
        this.manager = manager;
        this.buffers = buffers;
        this.usedCommands = usedCommands;
    }

    public <T extends IBuffer> T getBuffer(String buffer)
    {
        return (T)buffers.get(buffer);
    }

    public Set<String> getBuffers()
    {
        return buffers.keySet();
    }

    public boolean containsBuffer(String key)
    {
        return buffers.containsKey(key);
    }

    public void setBuffer(String key, IBuffer buffer)
    {
        if (!buffers.containsKey(key))
            buffers.put(key, buffer);
    }

    public void executeCommand(FlowComponent command, int connectionId)
    {
        if (command != null)
        {
            this.usedCommands.add(command.getId());
            command.getType().execute(command, connectionId, this);
            this.executeChildCommands(command.getType().getActiveChildren(command, connectionId));
        }
    }

    private void executeChildCommands(List<Connection> connections)
    {
        for (Connection connection : connections)
        {
            this.executeCommand(this.manager.getFlowItem(connection.getOutputId()), connection.getOutputConnection());
        }
    }

    public void executeTriggerCommand(FlowComponent component, EnumSet<ConnectionOption> validTriggerOutputs)
    {
        for (Variable variable : this.manager.getVariables())
        {
            if (variable.isValid())
            {
                this.executeCommand(variable.getDeclaration(), 0);
            }
        }
        this.executeChildCommands(component, validTriggerOutputs);
    }

    public void executeChildCommands(FlowComponent command, EnumSet<ConnectionOption> validTriggerOutputs)
    {
        for (int i = 0; i < command.getConnectionSet().getConnections().length; ++i)
        {
            Connection connection = command.getConnection(i);
            ConnectionOption option = command.getConnectionSet().getConnections()[i];
            if (connection != null && !option.isInput() && validTriggerOutputs.contains(option))
            {
                if (!usedCommands.contains(connection.getOutputId()))
                    this.executeCommand(this.manager.getFlowItem(connection.getOutputId()), connection.getOutputConnection());
            }
        }
    }
}
