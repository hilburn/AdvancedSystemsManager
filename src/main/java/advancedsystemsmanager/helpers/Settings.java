package advancedsystemsmanager.helpers;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.gui.GuiSettings;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Settings
{
    public static Map<String, Boolean> settingsRegistry = new LinkedHashMap<String, Boolean>(9);
    public static final String AUTO_CLOSE_GROUP = "autoCloseGroup";
    public static final String LARGE_OPEN = "largeOpenHitBox";
    public static final String LARGE_OPEN_MENU = "largeOpenHitBoxMenu";
    public static final String QUICK_GROUP_OPEN = "quickGroupOpen";
    public static final String COMMAND_TYPES = "commandTypes";
    public static final String AUTO_SIDE = "autoSide";
    public static final String AUTO_BLACKLIST = "autoBlacklist";
    public static final String CRAFT_MOVE_FIRST = "priorityMoveFirst";
    public static final String ENLARGE_INTERFACES = "enlargeInterfaces";
    public static final String MIDDLE_CLICK = "middleClick";

    static
    {
        registerSetting(AUTO_CLOSE_GROUP, false);
        registerSetting(LARGE_OPEN, true);
        registerSetting(LARGE_OPEN_MENU, true);
        registerSetting(QUICK_GROUP_OPEN, false);
        registerSetting(COMMAND_TYPES, false);
        registerSetting(AUTO_SIDE, true);
        registerSetting(AUTO_BLACKLIST, true);
        registerSetting(CRAFT_MOVE_FIRST, false);
        registerSetting(ENLARGE_INTERFACES, false);
        registerSetting(MIDDLE_CLICK, true);
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
        AdvancedSystemsManager.config.setConfigValue(name, String.valueOf(value));
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
        manager.specialRenderer = new GuiSettings(manager);
    }

    public static void loadDefault()
    {
        settingsRegistry = new LinkedHashMap<String, Boolean>(Config.managerSettings);
    }

    public static boolean isAutoCloseGroup()
    {
        return settingsRegistry.get(AUTO_CLOSE_GROUP);
    }

    public static boolean isLargeOpenHitBox()
    {
        return settingsRegistry.get(LARGE_OPEN);
    }

    public static boolean isLargeOpenHitBoxMenu()
    {
        return settingsRegistry.get(LARGE_OPEN_MENU);
    }

    public static boolean isQuickGroupOpen()
    {
        return settingsRegistry.get(QUICK_GROUP_OPEN);
    }

    public static boolean isCommandTypes()
    {
        return settingsRegistry.get(COMMAND_TYPES);
    }

    public static boolean isAutoSide()
    {
        return settingsRegistry.get(AUTO_SIDE);
    }

    public static boolean isAutoBlacklist()
    {
        return settingsRegistry.get(AUTO_BLACKLIST);
    }

    public static boolean isEnlargeInterfaces()
    {
        return settingsRegistry.get(ENLARGE_INTERFACES);
    }

    public static boolean isPriorityMoveFirst()
    {
        return settingsRegistry.get(CRAFT_MOVE_FIRST);
    }

    public static boolean isLimitless(TileEntityManager manager)
    {
        return (manager.getWorldObj().getBlockMetadata(manager.xCoord, manager.yCoord, manager.zCoord) & 1) != 0;
    }

    public static void setLimitless(TileEntityManager manager, boolean limitless)
    {
        if (manager.getWorldObj().isRemote)
        {
            ASMPacket packet = PacketHandler.getBaseContainerPacket();
            packet.writeByte(PacketHandler.SETTING_MESSAGE);
            packet.writeBoolean(limitless);
            PacketHandler.sendDataToServer(packet);
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
