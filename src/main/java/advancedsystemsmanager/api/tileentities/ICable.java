package advancedsystemsmanager.api.tileentities;

import advancedsystemsmanager.util.SystemCoord;
import net.minecraft.world.World;

import java.util.List;
import java.util.Queue;

public interface ICable
{
    boolean isCable(int meta);

    void getConnectedCables(World world, SystemCoord coordinate, List<SystemCoord> visited, Queue<SystemCoord> cables);
}
