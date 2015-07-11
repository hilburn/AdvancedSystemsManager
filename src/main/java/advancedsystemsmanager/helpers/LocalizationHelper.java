package advancedsystemsmanager.helpers;

import advancedsystemsmanager.reference.Names;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

public class LocalizationHelper
{
    public static void addChatMessage(EntityPlayer player, String key)
    {
        player.addChatComponentMessage(new ChatComponentText(translate(key)));
    }

    public static String translate(String key)
    {
        if (StatCollector.canTranslate(key)) return StatCollector.translateToLocal(key);
        return StatCollector.translateToFallback(key);
    }

    public static void addChatMessageFormatted(EntityPlayer player, String key, Object... additional)
    {
        player.addChatComponentMessage(new ChatComponentText(translateFormatted(key, additional)));
    }

    public static String translateFormatted(String key, Object... additional)
    {
        if (StatCollector.canTranslate(key)) return StatCollector.translateToLocalFormatted(key, additional);
        return String.format(StatCollector.translateToFallback(key), additional);
    }

    public static String getDirectionString(int id)
    {
        switch (id)
        {
            case 0:
                return Names.DOWN;
            case 1:
                return Names.UP;
            case 2:
                return Names.NORTH;
            case 3:
                return Names.SOUTH;
            case 4:
                return Names.WEST;
            default:
                return Names.EAST;
        }
    }
}
