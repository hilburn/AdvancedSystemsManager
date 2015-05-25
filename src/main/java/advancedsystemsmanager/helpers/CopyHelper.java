package advancedsystemsmanager.helpers;

import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.*;

public class CopyHelper
{
    public static Collection<FlowComponent> copyConnectionsWithChildren(TileEntityManager manager, FlowComponent toCopy, boolean limitless)
    {
        Map<FlowComponent, FlowComponent> added = new LinkedHashMap<FlowComponent, FlowComponent>();
        Multimap<FlowComponent, FlowComponent> existingParents = manager.getParentHierarchy();
        copyConnectionsWithChildren(manager, added, toCopy, toCopy.getParent(), existingParents, true);
        int maxSize = 511 - manager.components.size();
        if (added.size() > maxSize && !limitless)
        {
            Iterator<Map.Entry<FlowComponent, FlowComponent>> itr = added.entrySet().iterator();
            for (int index = 0; itr.hasNext(); index++)
            {
                itr.next();
                if (index >= maxSize) itr.remove();
            }
        }
        reconnect(added, manager.components);
        return added.values();
    }

    private static void copyConnectionsWithChildren(TileEntityManager manager, Map<FlowComponent, FlowComponent> added, FlowComponent toCopy, FlowComponent newParent,
                                                    Multimap<FlowComponent, FlowComponent> existingParents, boolean reset)
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
        for (FlowComponent component : existingParents.get(toCopy))
        {
            copyConnectionsWithChildren(manager, added, component, newComponent, existingParents, false);
        }
    }

    private static void reconnect(Map<FlowComponent, FlowComponent> added, TIntObjectHashMap<FlowComponent> oldComponents)
    {
        for (FlowComponent component : added.keySet())
        {
            int i = 0;
            for (Connection entry : component.getConnections())
            {
                if (entry != null)
                {
                    FlowComponent connectTo = added.get(oldComponents.get(entry.getComponentId()));
                    if (connectTo != null)
                    {
                        Connection newConnection = new Connection(connectTo.getId(), entry.getConnectionId());
                        added.get(component).setConnection(i, newConnection);
                    }
                    i++;
                }
            }
        }
    }
}
