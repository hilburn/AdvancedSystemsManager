package advancedsystemsmanager.gui.theme;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexValue
{
    private static final Pattern HEX = Pattern.compile("([\\dA-F]+$)", Pattern.CASE_INSENSITIVE);
    private int[] colour;

    public HexValue()
    {
        this.colour = new int[]{255,255,255,255};
    }

    public HexValue(String hex)
    {
        this.colour = getRGBAValue(hex);
    }

    public HexValue(int colour)
    {
        this(getRGBA(colour));
    }

    public HexValue(int[] colour)
    {
        this.colour = colour;
    }

    public int[] getColour()
    {
        return colour;
    }

    public static int[] getRGBAValue(String string)
    {
        Matcher hex = HEX.matcher(string);
        hex.find();
        String hexValue = hex.group();
        boolean opaque = hexValue.length() < 7;
        int colour = Integer.parseInt(hexValue, 16);
        return new int[]{colour >> 16 & 0xFF, colour >> 8 & 0xFF, colour & 0xFF, opaque ? 0xFF : colour >> 24 & 0xFF};
    }

    public static int[] getRGBA(int colour)
    {
        return new int[]{colour >> 16 & 0xFF, colour >> 8 & 0xFF, colour & 0xFF, colour >> 24 & 0xFF};
    }

    public String getHexValue()
    {
        return getHexString(colour);
    }

    @Override
    public String toString()
    {
        return getHexString(this.colour);
    }

    public static String getHexString(int[] colour)
    {
        int value = (colour[3] == 0xFF ? 0 : (colour[3] << 24)) | colour[0] << 16 | colour[1] << 8 | colour[2];
        int minSize = colour[3] == 0xFF ? 6 : 8;
        String string = Integer.toHexString(value);
        while (string.length() < minSize) string = "0" + string;
        return "#" + string;
    }
}
