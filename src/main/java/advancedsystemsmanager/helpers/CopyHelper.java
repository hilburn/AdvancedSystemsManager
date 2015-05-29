package advancedsystemsmanager.helpers;

import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.MenuGroup;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import com.google.common.collect.Multimap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.*;

public class CopyHelper
{
    public static Collection<FlowComponent> copyCommandsWithChildren(TileEntityManager manager, FlowComponent toCopy, boolean limitless, boolean connected)
    {
        Map<FlowComponent, FlowComponent> added = new LinkedHashMap<FlowComponent, FlowComponent>();
        Multimap<FlowComponent, FlowComponent> existingParents = manager.getParentHierarchy();
        if (!connected)
        {
            copyCommandsWithChildren(manager, added, toCopy, toCopy.getParent(), existingParents, true);
        } else
        {
            List<FlowComponent> cluster = new ArrayList<FlowComponent>();
            MenuGroup.findCluster(cluster, toCopy, null);
            for (FlowComponent component : cluster)
            {
                copyCommandsWithChildren(manager, added, component, component.getParent(), existingParents, false);
            }
        }
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

    private static void copyCommandsWithChildren(TileEntityManager manager, Map<FlowComponent, FlowComponent> added, FlowComponent toCopy, FlowComponent newParent,
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
            copyCommandsWithChildren(manager, added, component, newComponent, existingParents, false);
        }
    }

    private static void reconnect(Map<FlowComponent, FlowComponent> added, TIntObjectHashMap<FlowComponent> oldComponents)
    {
        for (FlowComponent component : added.keySet())
        {
            for (Connection entry : component.getConnections())
            {
                if (entry != null && entry.getInputId() == component.getId())
                {
                    FlowComponent connectTo = added.get(oldComponents.get(entry.getOutputId()));
                    FlowComponent connectFrom = added.get(component);
                    if (connectTo != null)
                    {
                        Connection newConnection = new Connection(connectFrom.getId(), connectTo.getId(), entry);
                        connectFrom.setConnection(entry.getInputConnection(), newConnection);
                        connectTo.setConnection(entry.getOutputConnection(), newConnection);
                    }
                }
            }
        }
    }
}
