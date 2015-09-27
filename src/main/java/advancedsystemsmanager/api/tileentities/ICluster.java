package advancedsystemsmanager.api.tileentities;

import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public interface ICluster
{
    List<ITileElement> getTiles();

    ForgeDirection getFacing();
}
