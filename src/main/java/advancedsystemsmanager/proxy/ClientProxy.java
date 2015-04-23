package advancedsystemsmanager.proxy;

import advancedsystemsmanager.settings.Settings;
import advancedsystemsmanager.threading.ThreadSafeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
    @Override
    public void init()
    {
        Settings.load();
    }

    @Override
    public World getClientWorld()
    {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public void initRenderers()
    {
    }

    @Override
    public void initHandlers()
    {
        MinecraftForge.EVENT_BUS.register(new ThreadSafeHandler());
    }
}
