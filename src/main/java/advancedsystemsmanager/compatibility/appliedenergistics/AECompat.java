package advancedsystemsmanager.compatibility.appliedenergistics;

import advancedsystemsmanager.blocks.TileFactory;
import advancedsystemsmanager.compatibility.CompatBase;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ClusterRegistry;

public class AECompat extends CompatBase
{
    public static TileFactory AE;
    @Override
    protected void init()
    {
        ClusterRegistry.register(AE = new TileFactory.Cluster(TileEntityAENode.class, new String[]{Names.CABLE_AE}));
    }
}
