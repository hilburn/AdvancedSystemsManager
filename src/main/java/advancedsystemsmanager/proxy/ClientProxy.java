package advancedsystemsmanager.proxy;

import advancedsystemsmanager.settings.Settings;
import advancedsystemsmanager.threading.ThreadSafeHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
    @Override
    public void init()
    {
        Settings.loadDefault();
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

    @Override
    public void registerItemRenderer(Item item, IItemRenderer renderer)
    {
        MinecraftForgeClient.registerItemRenderer(item, renderer);
    }

    @Override
    public void registerSimpleBlockRenderer(int id, ISimpleBlockRenderingHandler renderer)
    {
        RenderingRegistry.registerBlockHandler(id, renderer);
    }

    @Override
    public void registerTileEntityRenderer(Class<? extends TileEntity> tileEntity, TileEntitySpecialRenderer renderer)
    {
        ClientRegistry.bindTileEntitySpecialRenderer(tileEntity, renderer);
    }
}
