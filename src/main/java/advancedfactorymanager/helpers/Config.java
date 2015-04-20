package advancedfactorymanager.helpers;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import advancedfactorymanager.commands.CommandPastebin;

import java.io.File;
import java.util.Arrays;

public class Config
{
    public static boolean wailaIntegration = true;
    public static boolean aeIntegration = Loader.isModLoaded("appliedenergistics2");

    public static void init(File file)
    {
        Configuration config = new Configuration(file);

        Property whitelist = config.get("General", "pastebin_whitelist", new String[]{"hilburn"});
        whitelist.comment = "Add player names permitted to use Pastebin";
        CommandPastebin.usernameWhitelist.addAll(Arrays.asList(whitelist.getStringList()));

        Property waila = config.get("General", "waila_integration", wailaIntegration);
        waila.comment = "Show labels in WAILA tags";
        wailaIntegration = waila.getBoolean();

        if (aeIntegration)
        {
            Property ae = config.get("General", "applied_energistics_2", aeIntegration);
            ae.comment = "Enable Energistics Connector";
            aeIntegration = ae.getBoolean();
        }

        config.save();
    }
}
