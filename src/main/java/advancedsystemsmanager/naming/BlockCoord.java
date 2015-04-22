package advancedsystemsmanager.naming;

import net.minecraft.nbt.NBTTagCompound;

public class BlockCoord
{
    public int x, y, z;
    public String name;

    public BlockCoord(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockCoord(int x, int y, int z, String name)
    {
        this(x, y, z);
        this.name = name;
    }

    public BlockCoord(NBTTagCompound tagCompound)
    {
        this.x = tagCompound.getInteger("x");
        this.y = tagCompound.getShort("y");
        this.z = tagCompound.getInteger("z");
        this.name = tagCompound.getString("n");
    }

    public NBTTagCompound writeToNBT()
    {
        NBTTagCompound result = new NBTTagCompound();
        result.setInteger("x", x);
        result.setInteger("z", z);
        result.setShort("y", (short)y);
        if (this.name != null) result.setString("n", name);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof BlockCoord)
        {
            BlockCoord other = (BlockCoord)obj;
            return other.x == x && other.z == z && other.y == y;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int result = 121 * (x ^ (x >>> 16));
        result = 15 * result + (y ^ (y << 16));
        result = 15 * result + (z ^ (z >>> 16));
        return result;
    }

    @Override
    public String toString()
    {
        return "[x: " + x + ", y: " + y + ", z: " + z + "]" + (name != null ? name : "");
    }
}
