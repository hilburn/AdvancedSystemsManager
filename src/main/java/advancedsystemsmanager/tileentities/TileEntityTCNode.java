package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.util.ClusterMethodRegistration;

import java.util.EnumSet;

public class TileEntityTCNode extends TileEntityClusterElement
{
    @Override
    public EnumSet<ClusterMethodRegistration> getRegistrations()
    {
        return EnumSet.noneOf(ClusterMethodRegistration.class);
    }
}
