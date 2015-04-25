package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferProvider;
import advancedsystemsmanager.api.execution.IBufferSubElement;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import advancedsystemsmanager.util.ConnectionBlock;

import java.util.List;

public abstract class CommandInput<Type> extends CommandBase
{
    protected String bufferKey;

    public CommandInput(int id, String name, String longName, String key)
    {
        super(id, name, longName, CommandType.INPUT, ConnectionSet.STANDARD);
        this.bufferKey = key;
    }

    @Override
    public void execute(FlowComponent command, int connectionId, IBufferProvider bufferProvider)
    {
        if (!bufferProvider.containsBuffer(bufferKey))
            bufferProvider.setBuffer(bufferKey, getNewBuffer());
        addToBuffer(getBufferSubElements(command.id, getContainers(command.manager, (MenuContainer)command.menus.get(0)), command.menus), bufferProvider.getBuffer(bufferKey));
    }

    protected abstract IBuffer getNewBuffer();

    protected abstract List<IBufferSubElement<Type>> getBufferSubElements(int id, List<ConnectionBlock> blocks, List<Menu> menus);

    protected void addToBuffer(List<IBufferSubElement<Type>> subElements, IBuffer<Type> buffer)
    {
        for (IBufferSubElement<Type> subElement : subElements) buffer.add(subElement);
    }
}
