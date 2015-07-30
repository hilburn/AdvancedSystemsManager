package advancedsystemsmanager.registry;

import advancedsystemsmanager.client.gui.theme.HexValue;
import advancedsystemsmanager.client.gui.theme.Theme;
import advancedsystemsmanager.client.gui.theme.ThemeAdapters;
import advancedsystemsmanager.client.gui.theme.ThemeCommand;
import advancedsystemsmanager.helpers.FileHelper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ThemeHandler
{
    public static final Gson GSON = getGson();
    private static final Pattern JSON = Pattern.compile(".*\\.json", Pattern.CASE_INSENSITIVE);
    private static final FileFilter JSON_FILTER = new FileFilter()
    {
        @Override
        public boolean accept(File pathname)
        {
            return JSON.matcher(pathname.getName()).find();
        }
    };
    public static Theme theme = new Theme();
    private final String backupLocation;
    private final File themeDir;
    private File themeFile;

    public ThemeHandler(File directory, String backupLocation)
    {
        this.themeDir = directory;
        this.backupLocation = backupLocation;
    }

    public boolean setTheme(String name)
    {
        if (!name.endsWith(".json")) name += ".json";
        File theme = new File(themeDir.getAbsoluteFile() + File.separator + name);
        if (!theme.isFile())
        {
            if (FileHelper.doesFileExistInJar(getClass(), backupLocation + name))
            {
                FileHelper.copyFromJar(getClass(), backupLocation + name, themeDir.getAbsolutePath() + File.separator + name);
            } else
            {
                if (name.equals("default.json"))
                {
                    ThemeHandler.theme = new Theme();
                    saveTheme(name);
                    return true;
                }
                return false;
            }
            if (!theme.isFile()) return false;
        }
        this.themeFile = theme;
        loadTheme();
        return true;
    }

    public void saveTheme(String name)
    {
        if (!name.endsWith(".json")) name += ".json";
        File theme = new File(themeDir.getAbsoluteFile() + File.separator + name);
        if (theme.isFile())
        {
            //warn user?
        }
        if (!theme.exists()) try
        {
            theme.createNewFile();
        } catch (IOException ignored)
        {
        }
        try
        {
            FileWriter fileWriter = new FileWriter(theme);
            GSON.toJson(ThemeHandler.theme, Theme.class, fileWriter);
            fileWriter.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    public void loadTheme()
    {
        try
        {
            JsonReader reader = new JsonReader(new FileReader(this.themeFile));
            theme = GSON.fromJson(reader, Theme.class);
            reader.close();
        } catch (IOException e)
        {
            //TODO: log?
            theme = new Theme();
        }
    }

    public List<String> getThemes()
    {
        File[] files = themeDir.listFiles(JSON_FILTER);
        List<String> result = new ArrayList<String>();
        if (files != null)
        {
            for (File file : files)
            {
                result.add(file.getName().replace(".json", ""));
            }
        }
        return result;
    }

    public JsonObject toJson(Theme theme)
    {
        String test = getGson().toJson(theme);
        Theme back = getGson().fromJson(test, Theme.class);

        return null;
    }

    public static Gson getGson()
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(HexValue.class, ThemeAdapters.HEX_ADAPTER);
        builder.registerTypeAdapter(ThemeCommand.CommandSet.class, ThemeAdapters.COMMAND_ADAPTER);
        builder.setPrettyPrinting();
        builder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES);
        return builder.create();
    }
}
