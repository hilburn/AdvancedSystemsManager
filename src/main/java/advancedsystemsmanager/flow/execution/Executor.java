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
    public List<Integer> usedCommands;
    private Map<String, IBuffer> buffers;

    public Executor(TileEntityManager manager)
    {
        this.manager = manager;
        this.buffers = new HashMap<String, IBuffer>();
        this.usedCommands = new ArrayList<Integer>();
    }

    public Executor(TileEntityManager manager, Map<String, IBuffer> buffers, List<Integer> usedCommands)
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

    public List<Integer> usedCommands()
    {
        return usedCommands;
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
        if (command != null && !usedCommands.contains(command.getId()))
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
        for (Variable variable : this.manager.getVariableArray())
        {
            if (variable.isValid() && (!variable.hasBeenExecuted() || ((MenuVariable)variable.getDeclaration().getMenus().get(0)).getVariableMode() == MenuVariable.VariableMode.LOCAL))
            {
                this.executeCommand(variable.getDeclaration(), 0);
                variable.setExecuted(true);
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
                this.executeCommand(this.manager.getFlowItem(connection.getOutputId()), connection.getOutputConnection());
            }
        }
    }
}
