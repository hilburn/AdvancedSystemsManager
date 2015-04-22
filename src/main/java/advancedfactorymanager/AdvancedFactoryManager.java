package advancedfactorymanager;

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
import advancedfactorymanager.registry.ModBlocks;
import advancedfactorymanager.util.LogHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.HashMap;

@Mod(modid = Reference.ID, name = Reference.NAME, version = Reference.VERSION_FULL)
public class AdvancedFactoryManager
{
    public static final String RESOURCE_LOCATION = "advancedfactorymanager";
    public static final String UNLOCALIZED_START = "afm.";

    public static FMLEventChannel packetHandler;

    @SidedProxy(clientSide = "advancedfactorymanager.proxy.ClientProxy", serverSide = "advancedfactorymanager.proxy.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.Instance(value = Reference.ID)
    public static AdvancedFactoryManager INSTANCE;

    @Mod.Metadata(Reference.ID)
    public static ModMetadata metadata;

    public static GuiHandler guiHandler = new GuiHandler();


    public static LogHelper log = new LogHelper(Reference.ID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {

        metadata = Metadata.init(metadata);
        Config.init(event.getSuggestedConfigurationFile());
        ItemRegistry.registerItems();
        BlockRegistry.registerBlocks();
        MessageHandler.init();


        packetHandler = NetworkRegistry.INSTANCE.newEventDrivenChannel(Reference.ID);

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
    }

    @Mod.EventHandler
    @SuppressWarnings(value = "unchecked")
    public void postInit(FMLPostInitializationEvent event)
    {

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
