package advancedsystemsmanager.naming;

import advancedsystemsmanager.api.network.IPacketReader;
import advancedsystemsmanager.api.network.IPacketWriter;
import advancedsystemsmanager.helpers.SavableData;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Reference;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;

import java.util.Collection;

public class SaveNameData extends SavableData implements IPacketReader, IPacketWriter
{
    private static final String KEY = ".nameData";
    private Multimap<Integer, BlockCoord> registry = HashMultimap.create();
    public static final int SYNC_ALL = 0;
    public static final int ADD = 1;
    public static final int REMOVE = 2;
    public static final int CHANGE = 3;

    public SaveNameData(String key)
    {
        super(key);
    }

    public SaveNameData()
    {
        this(Reference.NETWORK_ID + KEY);
    }

    @Override
    protected SavableData getNew()
    {
        return new SaveNameData();
    }

    @Override
    public boolean copyFrom(WorldSavedData worldSavedData)
    {
        if (worldSavedData instanceof SaveNameData)
        {
            registry = ((SaveNameData) worldSavedData).registry;
            return true;
        }
        return false;
    }

    private void put(int dim, BlockCoord blockCoord)
    {
        registry.put(dim, blockCoord);
    }

    public void put(BlockCoord blockCoord, int dim)
    {
        registry.put(dim, blockCoord);
        markDirty();
    }

    public void remove(int dim, int x, int y, int z)
    {
        for (BlockCoord coord : registry.get(dim))
        {
            if (coord.isAt(x, y, z))
            {
                registry.remove(dim, coord);
                markDirty();
                return;
            }
        }
    }

    public boolean contains(int dim, int x, int y, int z)
    {
        for (BlockCoord coord : registry.get(dim))
        {
            if (coord.isAt(x, y, z))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        NBTTagList list = new NBTTagList();
        for (int dim : registry.keySet())
        {
            if (!registry.get(dim).isEmpty())
            {
                NBTTagCompound dimensionTag = new NBTTagCompound();
                dimensionTag.setByte("w", (byte) dim);
                NBTTagList dimList = new NBTTagList();
                for (BlockCoord coord : registry.get(dim))
                {
                    if (coord.hasName())
                    dimList.appendTag(coord.writeToNBT());
                }
                dimensionTag.setTag("l", dimList);
                list.appendTag(dimensionTag);
            }
        }
        tag.setTag("l", list);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        NBTTagList list = tag.getTagList("l", 10);
        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound dimTag = list.getCompoundTagAt(i);
            int id = dimTag.getByte("w");
            NBTTagList dimList = dimTag.getTagList("l", 10);
            for (int j = 0; j < dimList.tagCount(); j++)
            {
                put(id, new BlockCoord(dimList.getCompoundTagAt(j)));
            }
        }
    }

    public String getSavedName(int dim, int x, int y, int z)
    {
        for (BlockCoord coord : registry.get(dim))
        {
            if (coord.isAt(x, y, z)) return coord.getName();
        }
        return null;
    }

    @Override
    public boolean readData(ASMPacket packet)
    {
        switch(packet.readByte())
        {
            case SYNC_ALL:
                registry.clear();
                int dims = packet.readByte();
                for (int dim = 0; dim < dims; dim++)
                {
                    int coords = packet.readVarIntFromBuffer();
                    for (int coord = 0; coord < coords; coord++)
                    {
                        BlockCoord blockCoord = new BlockCoord();
                        blockCoord.readData(packet);
                        put(dim, blockCoord);
                    }
                }
                break;
            case ADD:
            case CHANGE:
                int dim = packet.readByte();
                BlockCoord blockCoord = new BlockCoord();
                blockCoord.readData(packet);
                put(dim, blockCoord);
                break;
            case REMOVE:
                remove(packet.readByte(), packet.readInt(), packet.readUnsignedByte(), packet.readInt());
                break;
        }
        return false;
    }

    @Override
    public boolean writeData(ASMPacket packet)
    {
        packet.writeByte(SYNC_ALL);
        packet.writeByte(registry.keySet().size());
        for (int dim : registry.keySet())
        {
            Collection<BlockCoord> dimList = registry.get(dim);
            packet.writeVarIntToBuffer(dimList.size());
            for (BlockCoord coord : dimList)
            {
                coord.writeData(packet);
            }
        }
        return false;
    }

    public void clear()
    {
        registry.clear();
    }
}
