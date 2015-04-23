package advancedsystemsmanager;

import advancedsystemsmanager.compatibility.ModCompat;
import advancedsystemsmanager.flow.setting.ModItemHelper;
import advancedsystemsmanager.helpers.Config;
import advancedsystemsmanager.gui.GuiHandler;
import advancedsystemsmanager.naming.EventHandler;
import advancedsystemsmanager.naming.NameData;
import advancedsystemsmanager.naming.NameRegistry;
import advancedsystemsmanager.network.FileHelper;
import advancedsystemsmanager.network.MessageHandler;
import advancedsystemsmanager.network.PacketEventHandler;
import advancedsystemsmanager.proxy.CommonProxy;
import advancedsystemsmanager.reference.Metadata;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.commands.ParentCommand;
import advancedsystemsmanager.registry.ItemRegistry;
import hilburnlib.registry.Registerer;
import hilburnlib.utils.LogHelper;
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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.HashMap;

@Mod(modid = Reference.ID, name = Reference.NAME, version = Reference.VERSION_FULL)
public class AdvancedSystemsManager
{
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

    public static CreativeTabs creativeTab;

    static Registerer registerer = new Registerer(log);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        metadata = Metadata.init(metadata);
        Config.init(event.getSuggestedConfigurationFile());

        creativeTab = new CreativeTabs(Reference.ID)
        {
            @Override
            public ItemStack getIconItemStack()
            {
                return new ItemStack(BlockRegistry.blockManager);
            }

            @Override
            public Item getTabIconItem()
            {
                return null;
            }
        };

        registerer.scan(BlockRegistry.class);
        registerer.scan(ItemRegistry.class);

        MessageHandler.init();

        packetHandler = NetworkRegistry.INSTANCE.newEventDrivenChannel(Reference.ID);

        FileHelper.setConfigDir(event.getModConfigurationDirectory());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        PROXY.init();

        packetHandler.register(new PacketEventHandler());

        //new ChatListener();
        NetworkRegistry.INSTANCE.registerGuiHandler(AdvancedSystemsManager.INSTANCE, guiHandler);

        ItemRegistry.registerRecipes();
        BlockRegistry.registerRecipes();
        EventHandler handler = new EventHandler();
        FMLCommonHandler.instance().bus().register(handler);
        MinecraftForge.EVENT_BUS.register(handler);
        PROXY.initHandlers();

        ModCompat.loadAll();
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
        event.registerServerCommand(ParentCommand.instance);
        File file = new File(DimensionManager.getCurrentSaveRootDirectory().getPath() + File.separator + "managers");
        if (!file.exists()) file.mkdirs();
    }
}
