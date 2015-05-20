package advancedsystemsmanager.helpers;

import advancedsystemsmanager.commands.CommandPastebin;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.settings.Settings;
import cpw.mods.fml.common.Loader;
import hilburnlib.registry.IConfigLock;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class Config implements IConfigLock
{
    public static final String CATEGORY_SETTINGS = "default_manager_settings";
    public static final String CATEGORY_ENABLE = "enable";
    public static boolean wailaIntegration = true;
    public static boolean aeIntegration = Loader.isModLoaded(Mods.APPLIEDENERGISTICS2);
    public static Map<String, Boolean> managerSettings = new LinkedHashMap<String, Boolean>(Settings.settingsRegistry);
    private Configuration config;

    public Config(File file)
    {
        config = new Configuration(file);
    }

    public void init()
    {
        config.load();

        Property whitelist = config.get(Configuration.CATEGORY_GENERAL, "pastebin_whitelist", new String[]{"hilburn"}, "Add player names permitted to use Pastebin");
        CommandPastebin.usernameWhitelist.addAll(Arrays.asList(whitelist.getStringList()));

        wailaIntegration = config.get(Configuration.CATEGORY_GENERAL, "waila_integration", wailaIntegration, "Show labels in WAILA tags").getBoolean();

        for (Map.Entry<String, Boolean> entry : managerSettings.entrySet())
        {
            entry.setValue(config.getBoolean(CATEGORY_SETTINGS, entry.getKey(), entry.getValue(), LocalizationHelper.translate("gui.asm.Settings." + entry.getKey())));
        }

        config.save();
    }

    @Override
    public boolean shouldRegister(String string, boolean defaultValue)
    {
        config.load();
        boolean result = config.getBoolean(string, CATEGORY_ENABLE, defaultValue, "Set true to enable, false to disable");
        config.save();
        return result;
    }
}
