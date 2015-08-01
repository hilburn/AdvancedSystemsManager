package advancedsystemsmanager.client.gui.theme;

public class ThemeMultiColour
{
    private HexValue baseColour;
    private HexValue selected;
    private HexValue hover;
    private HexValue hoverSelected;

    public ThemeMultiColour(int colour, int selected, int hover, int hoverSelected)
    {
        this.baseColour = new HexValue(colour);
        this.selected = new HexValue(selected);
        this.hover = new HexValue(hover);
        this.hoverSelected = new HexValue(hoverSelected);
    }

    public int[] getColour(boolean selected, boolean mouseOver)
    {
        return selected ? mouseOver ? hoverSelected.getColour() : this.selected.getColour() : mouseOver ? hover.getColour() : baseColour.getColour();
    }
}
