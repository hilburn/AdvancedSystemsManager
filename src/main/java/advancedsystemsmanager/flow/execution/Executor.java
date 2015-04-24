package advancedsystemsmanager.flow.execution;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferProvider;
import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;

import java.util.*;

public class Executor implements IBufferProvider
{
    private Map<String, IBuffer> buffers;
    public TileEntityManager manager;
    public List<Integer> usedCommands;

    @Override
    public <T extends IBuffer> T getBuffer(String buffer)
    {
        return (T)buffers.get(buffer);
    }

    @Override
    public boolean containsBuffer(String key)
    {
        return buffers.containsKey(key);
    }

    @Override
    public void setBuffer(String key, IBuffer buffer)
    {
        if (!buffers.containsKey(key))
            buffers.put(key, buffer);
    }


    public Executor(TileEntityManager manager)
    {
        this.manager = manager;
        this.buffers = new HashMap<String, IBuffer>();
        this.usedCommands = new ArrayList<Integer>();
    }

    public Executor(TileEntityManager manager, HashMap<String, IBuffer> buffers, List<Integer> usedCommands)
    {
        this.manager = manager;
        this.buffers = buffers;
        this.usedCommands = usedCommands;
    }

    public void executeCommand(FlowComponent command, int connectionId)
    {
        if (!usedCommands.contains(command.getId()))
        {
            this.usedCommands.add(command.getId());
            command.getType().execute(command, connectionId, this);
            this.executeChildCommands(command, command.getType().getActiveChildren(command));
        }
    }

    private void executeChildCommands(FlowComponent command, Set<ConnectionOption> activeChildren)
    {
        for (int i = 0; i < command.getConnectionSet().getConnections().length; ++i)
        {
            Connection connection = command.getConnection(i);
            ConnectionOption option = command.getConnectionSet().getConnections()[i];
            if (connection != null && !option.isInput() && activeChildren.contains(option))
            {
                this.executeCommand(this.manager.getFlowItems().get(connection.getComponentId()), connection.getConnectionId());
            }
        }
    }
}
