package advancedsystemsmanager.naming;

import advancedsystemsmanager.api.network.IPacketReader;
import advancedsystemsmanager.api.network.IPacketWriter;
import advancedsystemsmanager.network.ASMPacket;
import net.minecraft.nbt.NBTTagCompound;

public class BlockCoord implements IPacketReader, IPacketWriter
{
    private int x, y, z;
    private String name;

    public BlockCoord()
    {}

    public BlockCoord(int x, int y, int z, String name)
    {
        this(x, y, z);
        this.name = name;
    }

    public BlockCoord(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
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
        result.setShort("y", (short) y);
        if (this.name != null && !name.isEmpty()) result.setString("n", name);
        return result;
    }

    public boolean isAt(int x, int y, int z)
    {
        return x == this.x && y == this.y && z == this.z;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        int result = 173 + x;
        result = 31 * result + z;
        result = 31 * result + y;
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
        return false;
    }

    @Override
    public String toString()
    {
        return "[x: " + x + ", y: " + y + ", z: " + z + "]" + (name != null ? name : "");
    }

    @Override
    public boolean readData(ASMPacket packet)
    {
        this.x = packet.readInt();
        this.y = packet.readUnsignedByte();
        this.z = packet.readInt();
        this.name = packet.readStringFromBuffer();
        return false;
    }

    @Override
    public boolean writeData(ASMPacket packet)
    {
        packet.writeInt(x);
        packet.writeByte(y);
        packet.writeInt(z);
        packet.writeStringToBuffer(name == null ? "" : name);
        return false;
    }

    public boolean hasName()
    {
        return name != null && !name.isEmpty();
    }
}
