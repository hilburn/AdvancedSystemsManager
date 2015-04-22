package advancedsystemsmanager;

import advancedsystemsmanager.flow.setting.ModItemHelper;
import advancedsystemsmanager.helpers.Config;
import advancedsystemsmanager.interfaces.GuiHandler;
import advancedsystemsmanager.naming.EventHandler;
import advancedsystemsmanager.naming.NameData;
import advancedsystemsmanager.naming.NameRegistry;
import advancedsystemsmanager.network.FileHelper;
import advancedsystemsmanager.network.MessageHandler;
import advancedsystemsmanager.network.PacketEventHandler;
import advancedsystemsmanager.proxy.CommonProxy;
import advancedsystemsmanager.recipes.ClusterUncraftingRecipe;
import advancedsystemsmanager.reference.Metadata;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.registry.ItemRegistry;
import advancedsystemsmanager.registry.ModBlocks;
import advancedsystemsmanager.util.LogHelper;
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
public class AdvancedSystemsManager
{
    public static final String RESOURCE_LOCATION = "advancedsystemsmanager";
    public static final String UNLOCALIZED_START = "asm.";

    public static FMLEventChannel packetHandler;

    @SidedProxy(clientSide = "advancedsystemsmanager.proxy.ClientProxy", serverSide = "advancedsystemsmanager.proxy.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.Instance(value = Reference.ID)
    public static AdvancedSystemsManager INSTANCE;

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
        NetworkRegistry.INSTANCE.registerGuiHandler(AdvancedSystemsManager.INSTANCE, guiHandler);

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
