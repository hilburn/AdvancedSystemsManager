package advancedsystemsmanager.network;

import advancedsystemsmanager.network.message.*;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class MessageHandler
{
    public static SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper("ASM-Simple");
    public static int ID;

    public static void init()
    {
        ID = 0;
        register(LabelSyncMessage.class, Side.SERVER);
        register(SecretMessage.class, Side.SERVER);
    }

    private static void register(Class message, Side side)
    {
        INSTANCE.registerMessage(message, message, ID++, side);
    }
}