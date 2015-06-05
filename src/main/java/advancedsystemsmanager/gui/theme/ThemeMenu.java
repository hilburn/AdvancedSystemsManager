package advancedsystemsmanager.gui.theme;

public class ThemeMenu
{
    public HexValue title;
    public HexValue selectedTitle;
    public HexValue openMenuBackground;
    public ThemeMultiColour checkboxes = new ThemeMultiColour(0xffbfbfbf, 0xFF2c568e, 0xff647ea1, 0xFF3970ba);
    public ThemeMultiColour radioButtons = new ThemeMultiColour(0xffbfbfbf, 0xFF2c568e, 0xff647ea1, 0xFF3970ba);
    public ThemeTextBox textBoxes = new ThemeTextBox(0xffffffff, 0xff333333, 0, 0xff313e4e);

    public ThemeMenu(int t, int st, int background)
    {
        title = new HexValue(t);
        selectedTitle = new HexValue(st);
        openMenuBackground = new HexValue(background);
    }
}
