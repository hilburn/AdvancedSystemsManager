package advancedsystemsmanager.flow.execution;

import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.execution.IBufferProvider;
import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.buffers.MultiBufferElementMap;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class Executor implements IBufferProvider
{
    private MultiBufferElementMap buffers;
    public TileEntityManager manager;
    public List<Integer> usedCommands;

    @Override
    public Set<IBufferElement> getBuffer(Class<? extends IBufferElement> bufferClass)
    {
        return buffers.get(bufferClass);
    }

    public Executor(TileEntityManager manager)
    {
        this.manager = manager;
        this.buffers = new MultiBufferElementMap();
        this.usedCommands = new ArrayList<Integer>();
    }

    public Executor(TileEntityManager manager, MultiBufferElementMap buffers, List<Integer> usedCommands)
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
