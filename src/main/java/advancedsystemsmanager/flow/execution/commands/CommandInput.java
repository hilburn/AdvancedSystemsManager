package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.Executor;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.util.SystemCoord;

import java.util.List;

public abstract class CommandInput<Type> extends CommandBase<Type>
{
    protected String bufferKey;

    public CommandInput(int id, String name, String key)
    {
        super(id, name, CommandType.INPUT, ConnectionSet.STANDARD);
        this.bufferKey = key;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(FlowComponent command, int connectionId, Executor executor)
    {
        if (!executor.containsBuffer(bufferKey))
            executor.setBuffer(bufferKey, getNewBuffer());
        addToBuffer(getBufferSubElements(command.id, getContainers(command.manager, (MenuContainer)command.menus.get(0)), command.menus), executor.getBuffer(bufferKey));
    }

    protected abstract IBuffer getNewBuffer();

    protected void addToBuffer(List<IBufferElement<Type>> subElements, IBuffer<Type> buffer)
    {
        for (IBufferElement<Type> subElement : subElements) buffer.add(subElement);
    }

    protected abstract List<IBufferElement<Type>> getBufferSubElements(int id, List<SystemCoord> blocks, List<Menu> menus);
}
