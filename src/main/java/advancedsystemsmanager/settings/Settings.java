package advancedsystemsmanager.settings;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.helpers.Config;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Settings
{

    private static final String NAME = "AdvancedSystemsManagerSettings";
    public static Map<String, Boolean> settingsRegistry = new LinkedHashMap<String, Boolean>(9);

    static
    {
        registerSetting("autoCloseGroup", false);
        registerSetting("largeOpenHitBox", true);
        registerSetting("largeOpenHitBoxMenu", true);
        registerSetting("quickGroupOpen", false);
        registerSetting("commandTypes", false);
        registerSetting("autoSide", true);
        registerSetting("autoBlacklist", true);
        registerSetting("priorityMoveFirst", false);
        registerSetting("enlargeInterfaces", false);
    }

    private Settings()
    {
    }

    public static void registerSetting(String name, boolean value)
    {
        if (!settingsRegistry.containsKey(name)) settingsRegistry.put(name, value);
    }

    public static void setSetting(String name, boolean value)
    {
        settingsRegistry.put(name, value);
        save();
    }

    private static void save()
    {
        AdvancedSystemsManager.config.saveSettings(settingsRegistry);
    }

    public static boolean getSetting(String name)
    {
        return settingsRegistry.get(name);
    }

    @SideOnly(Side.CLIENT)
    public static void openMenu(TileEntityManager manager)
    {
        manager.specialRenderer = new SettingsScreen(manager);
    }

    public static void loadDefault()
    {
        settingsRegistry = new LinkedHashMap<String, Boolean>(Config.managerSettings);
    }

    public static boolean isAutoCloseGroup()
    {
        return settingsRegistry.get("autoCloseGroup");
    }

    public static boolean isLargeOpenHitBox()
    {
        return settingsRegistry.get("largeOpenHitBox");
    }

    public static boolean isLargeOpenHitBoxMenu()
    {
        return settingsRegistry.get("largeOpenHitBoxMenu");
    }

    public static boolean isQuickGroupOpen()
    {
        return settingsRegistry.get("quickGroupOpen");
    }

    public static boolean isCommandTypes()
    {
        return settingsRegistry.get("commandTypes");
    }

    public static boolean isAutoSide()
    {
        return settingsRegistry.get("autoSide");
    }

    public static boolean isAutoBlacklist()
    {
        return settingsRegistry.get("autoBlacklist");
    }

    public static boolean isEnlargeInterfaces()
    {
        return settingsRegistry.get("enlargeInterfaces");
    }

    public static boolean isPriorityMoveFirst()
    {
        return settingsRegistry.get("priorityMoveFirst");
    }

    public static boolean isLimitless(TileEntityManager manager)
    {
        return (manager.getWorldObj().getBlockMetadata(manager.xCoord, manager.yCoord, manager.zCoord) & 1) != 0;
    }

    public static void setLimitless(TileEntityManager manager, boolean limitless)
    {
        if (manager.getWorldObj().isRemote)
        {
//            DataWriter dw = PacketHandler.getWriterForServerActionPacket();
//            dw.writeBoolean(limitless);
//            PacketHandler.sendDataToServer(dw);
        } else
        {
            int meta = manager.getWorldObj().getBlockMetadata(manager.xCoord, manager.yCoord, manager.zCoord);
            if (limitless)
            {
                meta |= 1;
            } else
            {
                meta &= ~1;
            }
            manager.getWorldObj().setBlockMetadataWithNotify(manager.xCoord, manager.yCoord, manager.zCoord, meta, 3);
        }
    }
}
