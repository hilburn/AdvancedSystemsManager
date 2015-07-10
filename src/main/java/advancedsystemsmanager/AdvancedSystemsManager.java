package advancedsystemsmanager;

import advancedsystemsmanager.commands.ParentCommand;
import advancedsystemsmanager.compatibility.ModCompat;
import advancedsystemsmanager.flow.setting.ModItemHelper;
import advancedsystemsmanager.gui.GuiHandler;
import advancedsystemsmanager.helpers.ConfigHandler;
import advancedsystemsmanager.naming.EventHandler;
import advancedsystemsmanager.naming.NameRegistry;
import advancedsystemsmanager.network.MessageHandler;
import advancedsystemsmanager.network.PacketEventHandler;
import advancedsystemsmanager.proxy.CommonProxy;
import advancedsystemsmanager.reference.Metadata;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.registry.ItemRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.classloading.FMLForgePlugin;
import thevault.registry.Registerer;
import advancedsystemsmanager.registry.ThemeHandler;
import thevault.utils.LogHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

@Mod(modid = Reference.ID, name = Reference.NAME, version = Reference.VERSION_FULL)
public class AdvancedSystemsManager
{
    public static FMLEventChannel packetHandler;

    @SidedProxy(clientSide = "advancedsystemsmanager.proxy.ClientProxy", serverSide = "advancedsystemsmanager.proxy.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.Instance(value = Reference.ID)
    public static AdvancedSystemsManager INSTANCE;

    @Mod.Metadata(Reference.ID)
    public static ModMetadata metadata;

    public static GuiHandler guiHandler = new GuiHandler();

    public static LogHelper log = new LogHelper(Reference.ID);

    public static ConfigHandler configHandler;

    public static CreativeTabs creativeTab;

    public static Registerer registerer;

    public static ThemeHandler themeHandler;

    public static boolean DEV_ENVIRONMENT = FMLForgePlugin.RUNTIME_DEOBF;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        metadata = Metadata.init(metadata);
        File configDir = new File(event.getModConfigurationDirectory() + File.separator + Reference.ID);
        if (!configDir.exists()) configDir.mkdir();
        configHandler = new ConfigHandler(new File(configDir.getAbsolutePath() + File.separator + event.getSuggestedConfigurationFile().getName()));
        configHandler.init();
        if (event.getSide() == Side.CLIENT)
        {
            themeHandler = new ThemeHandler(configDir, Reference.THEMES);
            if (!themeHandler.setTheme(ConfigHandler.theme))
                themeHandler.setTheme("default");
        }

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

        registerer = new Registerer(log, PROXY, configHandler);
        registerer.scan(BlockRegistry.class, event.getSide());
        registerer.scan(ItemRegistry.class, event.getSide());

        MessageHandler.init();

        packetHandler = NetworkRegistry.INSTANCE.newEventDrivenChannel(Reference.ID);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        PROXY.init();

        packetHandler.register(new PacketEventHandler());

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
    public void loadComplete(FMLLoadCompleteEvent event)
    {
        configHandler.loadPowerValues();
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event)
    {
        NameRegistry.clear();
        ModItemHelper.init();
        event.registerServerCommand(ParentCommand.instance);
        File file = new File(DimensionManager.getCurrentSaveRootDirectory().getPath() + File.separator + "managers");
        if (!file.exists()) file.mkdir();
    }
}
