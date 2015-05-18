package advancedsystemsmanager.network;

import advancedsystemsmanager.network.message.*;
import advancedsystemsmanager.reference.Reference;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class MessageHandler
{
    public static SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(Reference.ID + "-Simple");
    public static int ID;

    public static void init()
    {
        ID = 0;
        register(RFNodeUpdateMessage.class, Side.CLIENT);
        register(NameDataUpdateMessage.class, Side.CLIENT);
        register(NameDataUpdateMessage.class, Side.SERVER);
        register(FullDataSyncMessage.class, Side.CLIENT);
        register(WorldDataSyncMessage.class, Side.CLIENT);
        register(LabelSyncMessage.class, Side.SERVER);
        register(SearchRegistryGenerateMessage.class, Side.CLIENT);
        register(SyncMessage.class, Side.CLIENT);
        register(SyncMessage.class, Side.SERVER);
        register(FinalSyncMessage.class, Side.SERVER);
        register(ContainerMessage.class, Side.CLIENT);
        register(ButtonMessage.class, Side.SERVER);
        register(ButtonMessage.class, Side.CLIENT);
        register(SecretMessage.class, Side.SERVER);
    }

    private static void register(Class message, Side side)
    {
        INSTANCE.registerMessage(message, message, ID++, side);
    }
}