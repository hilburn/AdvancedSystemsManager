package advancedsystemsmanager.naming;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class NameRegistry
{
    private static final SaveNameData nameData = new SaveNameData();

    static
    {
        AdvancedSystemsManager.worldSave.save(nameData);
    }

    public static String getSavedName(World world, int x, int y, int z)
    {
        return getSavedName(world.provider.dimensionId, x, y, z);
    }

    public static String getSavedName(int dim, int x, int y, int z)
    {
        return nameData.getSavedName(dim, x, y, z);
    }

    public static void saveName(World world, int x, int y, int z, String name)
    {
        BlockCoord coord = new BlockCoord(x, y, z, name);
        ASMPacket packet = getSavePacket(world.provider.dimensionId, coord);
        if (world.isRemote)
        {
            packet.sendServerPacket();
        } else
        {
            nameData.put(coord, world.provider.dimensionId);
            packet.sendToAll();
        }
    }

    private static ASMPacket getSavePacket(int dim, BlockCoord coord)
    {
        ASMPacket packet = PacketHandler.getNamePacket();
        packet.writeByte(SaveNameData.ADD);
        packet.writeByte(dim);
        coord.writeData(packet);
        return packet;
    }

    public static boolean removeName(World world, int x, int y, int z)
    {
        if (!nameData.contains(world.provider.dimensionId, x, y, z)) return false;
        ASMPacket packet = getRemovePacket(world.provider.dimensionId, x, y, z);
        if (world.isRemote)
        {
            packet.sendServerPacket();
        } else
        {
            nameData.remove(world.provider.dimensionId, x, y, z);
            packet.sendToAll();
        }
        return true;
    }

    private static ASMPacket getRemovePacket(int dim, int x, int y, int z)
    {
        ASMPacket packet = PacketHandler.getNamePacket();
        packet.writeByte(SaveNameData.REMOVE);
        packet.writeByte(dim);
        packet.writeInt(x);
        packet.writeByte(y);
        packet.writeInt(z);
        return packet;
    }

    public static void syncNameData(EntityPlayerMP player)
    {
        ASMPacket packet = PacketHandler.getNamePacket();
        nameData.writeData(packet);
        packet.sendPlayerPacket(player);
    }

    public static void clear()
    {
        nameData.clear();
    }

    public static void updateClient(ASMPacket packet)
    {
        nameData.readData(packet);
    }

    public static void updateServer(ASMPacket packet)
    {
        updateClient(packet);
        packet.sendToAll();
    }
}
