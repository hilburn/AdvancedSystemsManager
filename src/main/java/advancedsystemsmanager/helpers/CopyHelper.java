package advancedsystemsmanager.helpers;

import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;

import java.util.*;

public class CopyHelper
{
    public static Collection<FlowComponent> copyConnectionsWithChildren(TileEntityManager manager, Collection<FlowComponent> existing, FlowComponent toCopy, boolean limitless)
    {
        Map<FlowComponent, FlowComponent> added = new LinkedHashMap<FlowComponent, FlowComponent>();
        copyConnectionsWithChildren(manager, added, existing, toCopy, toCopy.getParent(), true);
        if (added.size() + existing.size() >= 511 && !limitless)
        {
            Iterator<Map.Entry<FlowComponent, FlowComponent>> itr = added.entrySet().iterator();
            for (int index = 0; itr.hasNext(); index++)
            {
                itr.next();
                if (index >= 511 - existing.size()) itr.remove();
            }
        }
        reconnect(added);
        return added.values();
    }

    private static void copyConnectionsWithChildren(TileEntityManager manager, Map<FlowComponent, FlowComponent> added, Collection<FlowComponent> existing, FlowComponent toCopy, FlowComponent newParent, boolean reset)
    {
        FlowComponent newComponent = toCopy.copy();
        newComponent.clearConnections();
        newComponent.setParent(newParent);
        if (reset)
        {
            newComponent.resetPosition();
            newComponent.setX(50);
            newComponent.setY(50);
        }
        newComponent.setId(manager.getNextFreeID());
        added.put(toCopy, newComponent);
        for (FlowComponent component : existing)
        {
            if (component.getParent() == toCopy)
            {
                copyConnectionsWithChildren(manager, added, existing, component, newComponent, false);
            }
        }
    }

    private static void reconnect(Map<FlowComponent, FlowComponent> added)
    {
        Map<Integer, FlowComponent> oldComponents = new HashMap<Integer, FlowComponent>();
        for (FlowComponent component : added.keySet())
        {
            oldComponents.put(component.getId(), component);
        }

        for (FlowComponent component : added.keySet())
        {
            int i = 0;
            for (Connection entry : component.getConnections())
            {
                try
                {
                    FlowComponent connectTo = added.get(oldComponents.get(entry.getComponentId()));
                    if (connectTo != null)
                    {
                        Connection newConnection = new Connection(connectTo.getId(), entry.getConnectionId());
                        added.get(component).setConnection(i, newConnection);
                    }
                    i++;
                } catch (NullPointerException ignored)
                {
                    break;
                }
            }
        }
    }
}
