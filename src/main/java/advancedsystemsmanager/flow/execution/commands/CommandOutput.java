package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBufferProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.registry.ConnectionSet;

import java.util.List;

public abstract class CommandOutput extends CommandBase
{
    protected CommandOutput(int id, String name, String longName)
    {
        super(id, name, longName, CommandType.OUTPUT, ConnectionSet.STANDARD);
    }

}
