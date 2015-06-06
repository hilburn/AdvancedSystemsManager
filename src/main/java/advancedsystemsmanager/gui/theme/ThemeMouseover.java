package advancedsystemsmanager.gui.theme;

public class ThemeMouseover
{
    public HexValue colour;
    public HexValue mouseover;

    public ThemeMouseover(int colour, int mouseover)
    {
        this.colour = new HexValue(colour);
        this.mouseover = new HexValue(mouseover);
    }
}
