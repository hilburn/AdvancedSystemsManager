package advancedfactorymanager.naming;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;

import java.util.HashMap;
import java.util.Map;

public class NameData extends WorldSavedData
{
    public static final String KEY = "afm.namedata";

    public Map<BlockCoord, String> names;

    public NameData()
    {
        this(KEY);
    }

    public NameData(String string)
    {
        super(string);
        names = new HashMap<BlockCoord, String>();
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        NBTTagList list = tagCompound.getTagList("l", 10);
        for (int i = 0; i < list.tagCount(); i++)
        {
            put(new BlockCoord(list.getCompoundTagAt(i)));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        NBTTagList list = new NBTTagList();
        for (BlockCoord coord : names.keySet())
        {
            list.appendTag(coord.writeToNBT());
        }
        tagCompound.setTag("l", list);
    }

    public String get(BlockCoord blockCoord)
    {
        return names.get(blockCoord);
    }

    public void put(BlockCoord blockCoord)
    {
        names.put(blockCoord, blockCoord.name);
    }

    public void remove(BlockCoord blockCoord)
    {
        names.remove(blockCoord);
    }
}
