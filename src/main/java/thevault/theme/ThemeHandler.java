package thevault.theme;

import advancedsystemsmanager.helpers.FileHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThemeHandler
{
    private static final Pattern JSON = Pattern.compile(".*\\.json", Pattern.CASE_INSENSITIVE);
    private static final Pattern HEX = Pattern.compile("([\\dA-F]+$)", Pattern.CASE_INSENSITIVE);
    private final String backupLocation;
    private final File themeDir;
    private File theme;

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
                return false;
            }
            if (!theme.isFile()) return false;
        }
        this.theme = theme;
        loadTheme();
        return true;
    }

    public List<String> getThemes()
    {
        File[] files = themeDir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return JSON.matcher(pathname.getName()).find();
            }
        });
        List<String> result = new ArrayList<String>();
        if (files != null)
        {
            for (File file : files)
            {
                result.add(file.getName());
            }
        }
        return result;
    }

    public JsonObject getThemeObject()
    {
        try
        {
            InputStream stream = new FileInputStream(theme);
            JsonReader jReader = new JsonReader(new InputStreamReader(stream));
            JsonParser parser = new JsonParser();
            return parser.parse(jReader).getAsJsonObject();
        } catch (FileNotFoundException ignored)
        {
            return null;
        }
    }

    public static int[] getRGBAValue(String string)
    {
        Matcher hex = HEX.matcher(string);
        hex.find();
        String hexValue = hex.group();
        boolean opaque = hexValue.length() < 7;
        int value = Integer.parseInt(hexValue, 16);
        return new int[]{value >> 16 & 0xFF, value >> 8 & 0xFF, value & 0xFF, opaque ? 0xFF : value >> 24};
    }

    public void loadTheme()
    {

    }
}
