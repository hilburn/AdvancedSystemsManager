package advancedsystemsmanager.gui.theme;

import com.google.gson.annotations.Expose;

public class ThemeCommand
{
    @Expose(serialize = false) public HexValue baseColour = new HexValue(0xffc6c6c6);
    public HexValue text;
    public HexValue type;
    public HexValue menuArea = new HexValue(0xffdcdcdc);
    public CommandSet commands;

    public ThemeCommand(int text, int type)
    {
        this.text = new HexValue(text);
        this.type = new HexValue(type);
    }

    public static class CommandSet
    {
    }
}
