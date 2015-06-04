package advancedsystemsmanager.gui.theme;

public class ThemeCommand
{
    public HexValue baseColor = new HexValue(0xffc6c6c6);
    public HexValue text;
    public HexValue type;
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
