package advancedfactorymanager.proxy;


import advancedfactorymanager.blocks.RenderCamouflage;
import advancedfactorymanager.registry.ModBlocks;
import advancedfactorymanager.settings.Settings;
import advancedfactorymanager.threading.ThreadSafeHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
    @Override
    public void init()
    {
        RenderCamouflage camouflage = new RenderCamouflage();
        ModBlocks.CAMOUFLAGE_RENDER_ID = camouflage.getRenderId();
        RenderingRegistry.registerBlockHandler(camouflage);
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
