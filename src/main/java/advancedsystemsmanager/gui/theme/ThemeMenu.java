package advancedsystemsmanager.gui.theme;

public class ThemeMenu
{
    public HexValue titleBackground;
    public HexValue selectedTitleBackground;
    public HexValue titleText = new HexValue(0xff404040);
    public HexValue openMenuBackground;
    public ThemeMultiColour checkboxes = new ThemeMultiColour(0xffbfbfbf, 0xFF2c568e, 0xff647ea1, 0xFF3970ba);
    public ThemeMultiColour radioButtons = new ThemeMultiColour(0xffbfbfbf, 0xFF2c568e, 0xff647ea1, 0xFF3970ba);
    public ThemeTextBox textBoxes = new ThemeTextBox(0xffffffff, 0xff333333, 0, 0xff313e4e);

    public ThemeMenu(int t, int st, int background)
    {
        titleBackground = new HexValue(t);
        selectedTitleBackground = new HexValue(st);
        openMenuBackground = new HexValue(background);
    }
}
