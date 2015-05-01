package advancedsystemsmanager.helpers;

import advancedsystemsmanager.commands.CommandPastebin;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.settings.Settings;
import cpw.mods.fml.common.Loader;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class Config
{
    public static final String CATEGORY_SETTINGS = "Default Settings";
    public static boolean wailaIntegration = true;
    public static boolean aeIntegration = Loader.isModLoaded(Mods.APPLIEDENERGISTICS2);
    public static Map<String, Boolean> managerSettings = new LinkedHashMap<String, Boolean>(Settings.settingsRegistry);

    public static void init(File file)
    {
        Configuration config = new Configuration(file);

        Property whitelist = config.get(Configuration.CATEGORY_GENERAL, "pastebin_whitelist", new String[]{"hilburn"});
        whitelist.comment = "Add player names permitted to use Pastebin";
        CommandPastebin.usernameWhitelist.addAll(Arrays.asList(whitelist.getStringList()));

        Property waila = config.get(Configuration.CATEGORY_GENERAL, "waila_integration", wailaIntegration);
        waila.comment = "Show labels in WAILA tags";
        wailaIntegration = waila.getBoolean();

        if (aeIntegration)
        {
            Property ae = config.get(Configuration.CATEGORY_GENERAL, "applied_energistics_2", true);
            ae.comment = "Enable Energistics Connector";
            aeIntegration = ae.getBoolean();
        }

        for (Map.Entry<String, Boolean> entry : managerSettings.entrySet())
        {
            Property setting = config.get(CATEGORY_SETTINGS, entry.getKey(), entry.getValue());
            setting.comment = StatCollector.translateToLocal("gui.asm.Settings." + entry.getKey());
            entry.setValue(setting.getBoolean());
        }

        config.save();
    }
}
