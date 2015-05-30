package advancedsystemsmanager.gui;

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
        RGBtoHSV(hex, HSV);
    }

    @Override
    public String toString()
    {
        return "\u00a7" + Integer.toHexString(ordinal());
    }

    public double getDistanceSq(int RGB)
    {
        float[] colourVals = new float[3];
        RGBtoHSV(RGB, colourVals);
        return (colourVals[2] - HSV[2]) * (colourVals[2] - HSV[2]) + colourVals[1] * colourVals[1] + HSV[1] * HSV[1] - 2*colourVals[1] * HSV[1] * Math.cos(colourVals[0] - HSV[0]);
    }

    public static TextColour getClosestColour(int rgb)
    {
        double min = 100D;
        TextColour matchColour = WHITE;
        for (TextColour colour : TextColour.values())
        {
            if (colour == BLACK) continue;
            double distance = colour.getDistanceSq(rgb);
            if (distance<min)
            {
                min = distance;
                matchColour = colour;
            }
        }
        return matchColour;
    }

    public static void RGBtoHSV(int RGB, float[] values)
    {
        int r = RGB >> 16;
        int g = (RGB >> 8) & 0xFF;
        int b = RGB & 0xFF;
        float hue, saturation, brightness;
        int cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0)
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }
        values[0] = hue;
        values[1] = saturation;
        values[2] = brightness;
    }
}
