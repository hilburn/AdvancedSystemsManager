package advancedsystemsmanager.client.gui.theme;

public class ThemeCommand
{
    public HexValue text;
    public HexValue type;
    public HexValue menuArea = new HexValue(0xffdcdcdc);
    public CommandSet commands;
    public ThemeConnectionType connections = new ThemeConnectionType();
    public ThemeMouseover connectionNodes = new ThemeMouseover(0xff949494, 0xffd4d4d4);

    public ThemeCommand(int text, int type)
    {
        this.text = new HexValue(text);
        this.type = new HexValue(type);
    }

    public static class CommandSet
    {
    }
}
