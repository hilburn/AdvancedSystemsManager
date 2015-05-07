package advancedsystemsmanager.api.tileentities;

import advancedsystemsmanager.util.WorldCoordinate;
import net.minecraft.world.World;

import java.util.List;
import java.util.Queue;

public interface ICable
{
    boolean isCable(int meta);

    void getConnectedCables(World world, WorldCoordinate coordinate, List<WorldCoordinate> visited, Queue<WorldCoordinate> cables);
}
