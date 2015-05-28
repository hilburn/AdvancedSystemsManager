package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBufferProvider;
import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandGroupNode extends CommandBase
{
    public CommandGroupNode()
    {
        super(GROUP_NODE, Names.NODE, CommandType.MISC, ConnectionSet.INPUT_NODE, ConnectionSet.OUTPUT_NODE);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
    }

    @Override
    public void execute(FlowComponent command, int connectionId, IBufferProvider bufferProvider)
    {

    }

    @Override
    public List<Connection> getActiveChildren(FlowComponent command, int connectionId)
    {
        List<Connection> connections = new ArrayList<Connection>();
        FlowComponent parent = command.getParent();
        if (parent != null)
        {
            for (int i = 0; i < parent.getChildrenOutputNodes().size(); ++i)
            {
                if (command.equals(parent.getChildrenOutputNodes().get(i)))
                {
                    Connection output = parent.getConnection(5 + i);
                    if (output != null)
                    {
                        connections.add(output);
                    }
                    break;
                }
            }
        }
        return connections;
    }

    @Override
    public void moveComponent(FlowComponent component, FlowComponent oldParent, FlowComponent newParent)
    {
        boolean isInput = component.getConnectionSet() == ConnectionSet.INPUT_NODE;
        if (oldParent != null)
        {
            if (oldParent.childrenInputNodes.remove(component))
                Collections.sort(oldParent.childrenInputNodes);
            else if (oldParent.childrenOutputNodes.remove(component))
                Collections.sort(oldParent.childrenOutputNodes);
        }
        if (newParent != null)
        {
            if (isInput && !newParent.childrenInputNodes.contains(component))
            {
                newParent.childrenInputNodes.add(component);
                Collections.sort(newParent.childrenInputNodes);
            } else if (!isInput && !newParent.childrenOutputNodes.contains(component))
            {
                newParent.childrenOutputNodes.add(component);
                Collections.sort(newParent.childrenOutputNodes);
            }
        }
    }
}
