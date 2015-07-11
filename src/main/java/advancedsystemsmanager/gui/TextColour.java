package advancedsystemsmanager.gui;

import advancedsystemsmanager.util.ColourUtils;

public enum TextColour
{
    BLACK(0x000000),
    BLUE(0x0000AA),
    GREEN(0x00AA00),
    CYAN(0x00AAAA),
    DARK_RED(0xAA0000),
    PURPLE(0xAA00AA),
    ORANGE(0xFFAA00),
    LIGHT_GRAY(0xAAAAAA),
    GRAY(0x555555),
    LIGHT_BLUE(0x5555FF),
    LIME(0x55FF55),
    TURQUOISE(0x55FFFF),
    RED(0xFF5555),
    MAGENTA(0xFF55FF),
    YELLOW(0xFFFF55),
    WHITE(0xFFFFFF);

    float[] HSV = new float[3];


    TextColour(int hex)
    {
        ColourUtils.HextoHSV(hex, HSV);
    }

    public static TextColour getClosestColour(int rgb)
    {
        double min = 100D;
        TextColour matchColour = WHITE;
        for (TextColour colour : TextColour.values())
        {
            if (colour == BLACK) continue;
            double distance = colour.getDistanceSq(rgb);
            if (distance < min)
            {
                min = distance;
                matchColour = colour;
            }
        }
        return matchColour;
    }

    public double getDistanceSq(int RGB)
    {
        float[] colourVals = new float[3];
        ColourUtils.HextoHSV(RGB, colourVals);
        return (colourVals[2] - HSV[2]) * (colourVals[2] - HSV[2]) + colourVals[1] * colourVals[1] + HSV[1] * HSV[1] - 2 * colourVals[1] * HSV[1] * Math.cos(colourVals[0] - HSV[0]);
    }

    @Override
    public String toString()
    {
        return "\u00a7" + Integer.toHexString(ordinal());
    }
}
