package advancedsystemsmanager.helpers;

import net.minecraft.util.StatCollector;

public class LocalizationHelper
{
    public static String translateFormatted(String key, Object... additional)
    {
        if (StatCollector.canTranslate(key)) return StatCollector.translateToLocalFormatted(key, additional);
        return String.format(StatCollector.translateToFallback(key), additional);
    }

    public static String translate(String key)
    {
        if (StatCollector.canTranslate(key)) return StatCollector.translateToLocal(key);
        return StatCollector.translateToFallback(key);
    }
}
