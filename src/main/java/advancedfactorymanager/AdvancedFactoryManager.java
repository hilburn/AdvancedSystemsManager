package advancedfactorymanager;

import advancedfactorymanager.blocks.ModBlocks;
import advancedfactorymanager.components.ModItemHelper;
import advancedfactorymanager.helpers.Config;
import advancedfactorymanager.interfaces.GuiHandler;
import advancedfactorymanager.naming.EventHandler;
import advancedfactorymanager.naming.NameData;
import advancedfactorymanager.naming.NameRegistry;
import advancedfactorymanager.network.FileHelper;
import advancedfactorymanager.network.MessageHandler;
import advancedfactorymanager.network.PacketEventHandler;
import advancedfactorymanager.proxy.CommonProxy;
import advancedfactorymanager.recipes.ClusterUncraftingRecipe;
import advancedfactorymanager.reference.Metadata;
import advancedfactorymanager.reference.Reference;
import advancedfactorymanager.registry.BlockRegistry;
import advancedfactorymanager.registry.CommandRegistry;
import advancedfactorymanager.registry.ItemRegistry;
import advancedfactorymanager.tileentities.TileEntityManager;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

@Mod(modid = Reference.ID, name = Reference.NAME, version = Reference.VERSION_FULL)
public class AdvancedFactoryManager
{


    public static final String RESOURCE_LOCATION = "stevesfactory";
    public static final String CHANNEL = "FactoryManager";
    public static final String UNLOCALIZED_START = "sfm.";

    public static FMLEventChannel packetHandler;

    @SidedProxy(clientSide = "advancedfactorymanager.proxy.ClientProxy", serverSide = "advancedfactorymanager.proxy.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.Instance(value = Reference.ID)
    public static AdvancedFactoryManager INSTANCE;

    @Mod.Metadata(Reference.ID)
    public static ModMetadata metadata;

    public static GuiHandler guiHandler = new GuiHandler();
    public static Logger log = LogManager.getLogger(Reference.ID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {

        metadata = Metadata.init(metadata);
        Config.init(event.getSuggestedConfigurationFile());
        ItemRegistry.registerItems();
        BlockRegistry.registerBlocks();
        MessageHandler.init();


        packetHandler = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNEL);

        FileHelper.setConfigDir(event.getModConfigurationDirectory());

        ModBlocks.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        PROXY.init();

        packetHandler.register(new PacketEventHandler());

        ModBlocks.addRecipes();
        //new ChatListener();
        NetworkRegistry.INSTANCE.registerGuiHandler(AdvancedFactoryManager.INSTANCE, guiHandler);

        ItemRegistry.registerRecipes();
        BlockRegistry.registerRecipes();
        ClusterUncraftingRecipe uncrafting = new ClusterUncraftingRecipe();
        GameRegistry.addRecipe(uncrafting);
        FMLCommonHandler.instance().bus().register(uncrafting);
        EventHandler handler = new EventHandler();
        FMLCommonHandler.instance().bus().register(handler);
        MinecraftForge.EVENT_BUS.register(handler);
        PROXY.initHandlers();
        if (Config.wailaIntegration)
        {
            FMLInterModComms.sendMessage("Waila", "register", "advancedfactorymanager.waila.WailaManager.callbackRegister");
        }

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (Loader.isModLoaded("JABBA"))
        {
            try
            {
                Class dolly = Class.forName("mcp.mobius.betterbarrels.common.items.dolly.ItemBarrelMover");
                Field classExtensions = dolly.getDeclaredField("classExtensions");
                Field classExtensionsNames = dolly.getDeclaredField("classExtensionsNames");
                Field classMap = dolly.getDeclaredField("classMap");
                classExtensions.setAccessible(true);
                classExtensionsNames.setAccessible(true);
                classMap.setAccessible(true);
                ArrayList<Class> extensions = (ArrayList<Class>)classExtensions.get(null);
                ArrayList<String> extensionsNames = (ArrayList<String>)classExtensionsNames.get(null);
                HashMap<String, Class> map = (HashMap<String, Class>)classMap.get(null);
                extensions.add(TileEntityManager.class);
                extensionsNames.add(TileEntityManager.class.getSimpleName());
                map.put(TileEntityManager.class.getSimpleName(), TileEntityManager.class);
            } catch (Exception ignore)
            {
            }
        }
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event)
    {
        NameRegistry.setNameData(new HashMap<Integer, NameData>());
        ModItemHelper.init();
        event.registerServerCommand(CommandRegistry.instance);
        File file = new File(DimensionManager.getCurrentSaveRootDirectory().getPath() + File.separator + "managers");
        if (!file.exists()) file.mkdirs();
    }


}
