package advancedsystemsmanager.gui.theme;

public class ThemeTwoLayerMouseover
{
    public HexValue background;
    public HexValue backgroundMouseover;
    public HexValue foreground;
    public HexValue foregroundMouseover;

    public ThemeTwoLayerMouseover(int b, int bm, int f, int fm)
    {
        background = new HexValue(b);
        backgroundMouseover = new HexValue(bm);
        foreground = new HexValue(f);
        foregroundMouseover = new HexValue(fm);
    }
}
