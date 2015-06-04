package advancedsystemsmanager.gui.theme;

public class ThemeTextBox
{
    public HexValue text;
    public HexValue background;
    public HexValue border;
    public HexValue selected;

    public ThemeTextBox(int text, int background, int border, int selected)
    {
        this.text = new HexValue(text);
        this.background = new HexValue(background);
        this.border = new HexValue(border);
        this.selected = new HexValue(selected);
    }
}
