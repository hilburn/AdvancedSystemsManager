package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.registry.ConnectionSet;

import java.util.List;

public abstract class CommandOutput<Type> extends CommandBase<Type>
{
    private String bufferKey;

    public CommandOutput(int id, String name, String longName, String bufferKey)
    {
        super(id, name, longName, CommandType.OUTPUT, ConnectionSet.STANDARD);
        this.bufferKey = bufferKey;
    }

    @Override
    public void execute(FlowComponent command, int connectionId, IBufferProvider bufferProvider)
    {
        if (bufferProvider.containsBuffer(bufferKey))
            outputFromBuffer(command, bufferProvider.getBuffer(bufferKey));
    }

    protected abstract void outputFromBuffer(FlowComponent component, IBuffer<Type> buffer);

}
