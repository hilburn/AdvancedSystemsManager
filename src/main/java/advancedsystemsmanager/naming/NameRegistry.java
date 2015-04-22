package advancedsystemsmanager.naming;

import advancedsystemsmanager.network.MessageHandler;
import advancedsystemsmanager.network.message.FullDataSyncMessage;
import advancedsystemsmanager.network.message.NameDataUpdateMessage;
import advancedsystemsmanager.network.message.WorldDataSyncMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class NameRegistry
{
    public static NameRegistry instance = new NameRegistry();

    private Map<Integer, NameData> nameMapping = new HashMap<Integer, NameData>();

    public static String getSavedName(World world, int x, int y, int z)
    {
        NameData data = instance.nameMapping.get(world.provider.dimensionId);
        if (data == null) return null;
        return data.get(new BlockCoord(x, y, z));
    }

    public static String getSavedName(int dimensionId, BlockCoord coord)
    {
        NameData data = instance.nameMapping.get(dimensionId);
        if (data == null) return null;
        return data.get(coord);
    }

    public static void saveName(World world, int x, int y, int z, String name)
    {
        BlockCoord coord = new BlockCoord(x, y, z, name);
        if (world.isRemote)
        {
            MessageHandler.INSTANCE.sendToServer(new NameDataUpdateMessage(world.provider.dimensionId, coord));
        } else
        {
            if (!instance.nameMapping.containsKey(world.provider.dimensionId))
                instance.nameMapping.put(world.provider.dimensionId, new NameData());
            NameData data = instance.nameMapping.get(world.provider.dimensionId);
            data.markDirty();
            MessageHandler.INSTANCE.sendToAll(new NameDataUpdateMessage(world.provider.dimensionId, coord));
        }
    }

    public static void saveName(NameDataUpdateMessage message)
    {
        if (!instance.nameMapping.containsKey(message.dimId)) instance.nameMapping.put(message.dimId, new NameData());
        NameData data = instance.nameMapping.get(message.dimId);
        data.put(message.blockCoord);
    }

    public static boolean removeName(World world, int x, int y, int z)
    {
        BlockCoord coord = new BlockCoord(x, y, z, "");
        if (!instance.nameMapping.containsKey(world.provider.dimensionId)) return false;
        NameData data = instance.nameMapping.get(world.provider.dimensionId);
        if (!data.names.containsKey(coord)) return false;
        if (world.isRemote)
            MessageHandler.INSTANCE.sendToServer(new NameDataUpdateMessage(world.provider.dimensionId, coord, true));
        else
        {
            data.remove(coord);
            data.markDirty();
            MessageHandler.INSTANCE.sendToAll(new NameDataUpdateMessage(world.provider.dimensionId, coord, true));
        }
        return true;
    }

    public static void removeName(NameDataUpdateMessage message)
    {
        if (!instance.nameMapping.containsKey(message.dimId)) return;
        NameData data = instance.nameMapping.get(message.dimId);
        data.markDirty();
        data.remove(message.blockCoord);
    }

    public static void setWorldData(int dim, NameData data)
    {
        instance.nameMapping.put(dim, data);
        MessageHandler.INSTANCE.sendToAll(new WorldDataSyncMessage(dim, data));
    }

    public static void setWorldData(WorldDataSyncMessage message)
    {
        NameData nameData = new NameData();
        nameData.readFromNBT(message.tagCompound);
        NameRegistry.setWorldData(message.dimId, nameData);
    }

    public static NameData getWorldData(int dim, boolean unload)
    {
        NameData data = instance.nameMapping.get(dim);
        if (unload) instance.nameMapping.remove(dim);
        return data;
    }

    public static void setNameData(Map<Integer, NameData> nameMapping)
    {
        instance.nameMapping.putAll(nameMapping);
    }

    public static void syncNameData(EntityPlayerMP player)
    {
        MessageHandler.INSTANCE.sendTo(new FullDataSyncMessage(instance.nameMapping), player);
    }
}
