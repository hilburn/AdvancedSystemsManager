package advancedsystemsmanager.helpers;

import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.commands.CommandPastebin;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import thevault.registry.IConfigLock;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigHandler implements IConfigLock
{
    public static final String CATEGORY_SETTINGS = "default_manager_settings";
    public static final String CATEGORY_ENABLE = "enable";
    public static final String CATEGORY_POWER = "power_settings";
    public static String theme;
    public static boolean wailaIntegration = true;
    public static boolean aeIntegration = Loader.isModLoaded(Mods.APPLIEDENERGISTICS2);
    public static Map<String, Boolean> managerSettings = new LinkedHashMap<String, Boolean>(Settings.settingsRegistry);
    private Configuration config;
    private File file;

    public ConfigHandler(File file)
    {
        config = new Configuration(this.file = file);
    }

    public void init()
    {
        config.load();

        Property whitelist = config.get(Configuration.CATEGORY_GENERAL, "pastebin_whitelist", new String[]{"hilburn"}, "Add player names permitted to use Pastebin");
        CommandPastebin.usernameWhitelist.addAll(Arrays.asList(whitelist.getStringList()));

        wailaIntegration = config.get(Configuration.CATEGORY_GENERAL, "waila_integration", wailaIntegration, "Show labels in WAILA tags").getBoolean();

        config.addCustomCategoryComment(CATEGORY_SETTINGS, "Modify these to change the settings on the manager (Client Side Only)");

        for (Map.Entry<String, Boolean> entry : managerSettings.entrySet())
        {
            entry.setValue(config.get(CATEGORY_SETTINGS, entry.getKey(), entry.getValue(), LocalizationHelper.translate(Names.GUI_PREFIX + "Settings." + entry.getKey())).getBoolean());
        }

        theme = config.get(CATEGORY_SETTINGS, "theme", "default").getString();

        config.addCustomCategoryComment(CATEGORY_ENABLE, "Set true to enable, false to disable");
        config.setCategoryRequiresWorldRestart(CATEGORY_ENABLE, true);

        save();
    }

    private void save()
    {
        if (config.hasChanged())
            config.save();
    }

    public void loadPowerValues()
    {
        config.load();
        config.setCategoryComment(CATEGORY_POWER, "Control the power settings of the manager, if enabled");
        TileEntityManager.energyCostActive = config.get(CATEGORY_POWER, "enable_energy_costs", false).getBoolean();
        for (ICommand command : CommandRegistry.getCommands())
        {
            if (command != null)
                command.setEnergyCost(config.get(CATEGORY_POWER, LocalizationHelper.translate(command.getName()), command.getEnergyCost()).getInt());
        }
        save();
    }

    @Override
    public boolean shouldRegister(String string, boolean defaultValue)
    {
        config.load();
        boolean result = config.get(CATEGORY_ENABLE, string, defaultValue).getBoolean();
        save();
        return result;
    }

    public boolean setConfigValue(String name, String to)
    {
        boolean found = false;

        try
        {
            FileReader fr1 = new FileReader(file);
            BufferedReader read = new BufferedReader(fr1);

            ArrayList<String> strings = new ArrayList<String>();

            while (read.ready())
            {
                strings.add(read.readLine());
            }

            fr1.close();
            read.close();

            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            Pattern pattern = Pattern.compile("(.*" + name + "=)");
            Matcher matcher;
            for (String s : strings)
            {
                if (!found && (matcher = pattern.matcher(s)).find())
                {
                    s = matcher.group() + to;
                    found = true;
                }

                fw.write(s + "\n");
            }

            bw.flush();
            bw.close();
        } catch (Throwable t)
        {
            t.printStackTrace();
        }

        return found;
    }

    public void saveSettings(Map<String, Boolean> settings)
    {
        try
        {
            FileReader fr1 = new FileReader(file);
            BufferedReader read = new BufferedReader(fr1);

            ArrayList<String> strings = new ArrayList<String>();

            while (read.ready())
            {
                strings.add(read.readLine());
            }

            fr1.close();
            read.close();

            Matcher matcher;
            Pattern pattern;

            for (Map.Entry<String, Boolean> entry : settings.entrySet())
            {
                pattern = Pattern.compile("(.*" + entry.getKey() + "=)");
                for (int i = 0; i < strings.size(); i++)
                {
                    String s = strings.get(i);
                    if ((matcher = pattern.matcher(s)).find())
                    {
                        strings.set(i, matcher.group() + entry.getValue().toString());
                    }
                }
            }

            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            for (String s : strings)
            {
                bw.write(s + "\n");
            }

            bw.flush();
            bw.close();

            managerSettings.putAll(settings);
        } catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
}
