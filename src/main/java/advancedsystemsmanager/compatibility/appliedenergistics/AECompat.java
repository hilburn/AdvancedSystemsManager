package advancedsystemsmanager.compatibility.appliedenergistics;

import advancedsystemsmanager.compatibility.CompatBase;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.tileentities.TileEntityAENode;

public class AECompat extends CompatBase
{
    @Override
    protected void init()
    {
//        if (BlockRegistry.cableAENode != null)
//            ClusterRegistry.register(TileEntityAENode.class, BlockRegistry.cableAENode);
    }
}
