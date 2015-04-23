package advancedsystemsmanager.util;


import net.minecraft.tileentity.TileEntity;

public class WorldCoordinate implements Comparable<WorldCoordinate>
{
    private int x, y, z, depth;
    private TileEntity tileEntity;

    public WorldCoordinate(int x, int y, int z)
    {
        this(x, y, z, 0);
    }

    public WorldCoordinate(int x, int y, int z, int depth)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.depth = depth;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof WorldCoordinate)) return false;

        WorldCoordinate that = (WorldCoordinate)o;

        return x == that.x && y == that.y && z == that.z;

    }

    @Override
    public int hashCode()
    {
        int result = 173 + x;
        result = 31 * result + z;
        result = 31 * result + y;
        return result;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public int getDepth()
    {
        return depth;
    }

    @Override
    public int compareTo(WorldCoordinate o)
    {
        return depth == o.depth ? 0 : depth < o.depth ? -1 : 1;
    }

    public void setTileEntity(TileEntity tileEntity)
    {
        this.tileEntity = tileEntity;
    }

    public TileEntity getTileEntity()
    {
        return tileEntity;
    }
}
