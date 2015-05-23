package advancedsystemsmanager.api.network;

import advancedsystemsmanager.network.ASMPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IPacketProvider
{
    @SideOnly(Side.CLIENT)
    ASMPacket getSyncPacket();

    void registerSyncable(IPacketSync networkSync);

    @SideOnly(Side.CLIENT)
    void sendPacketToServer(ASMPacket packet);
}
