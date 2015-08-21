package advancedsystemsmanager.client.gui.elements;

import advancedsystemsmanager.api.gui.IGuiElement;
import advancedsystemsmanager.client.gui.GuiBase;

public class GuiTab implements IGuiElement<GuiBase>
{
    protected static float SLOPE = 0.3F;
    private double x;
    private boolean visible = true;
    private double[] coordinates, outline;
    private GuiTabList parentList;
    private String label;
    static int[] SELECTED_COLOURS = new int[]{0x1d, 0x1d, 0x1d, 0x1d, 0x1d, 0x1d, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30};
    static int[] DESELECTED_COLOURS = new int[]{0x1d, 0x1d, 0x1d, 0x1d, 0x1d, 0x1d, 0x1d, 0x1d, 0x1d, 0x1d, 0x1d, 0x1d};
    static int[] OUTLINE_COLOURS = new int[]{0x64, 0x64, 0x64, 0xff};

    public GuiTab(GuiTabList list)
    {
        this.parentList = list;
        label = String.valueOf(list.size());
        list.add(this);
        setX();
        recalculateCoordinates();
    }

    public void setX()
    {
        int id = parentList.indexOf(this) - parentList.getStartIndex();
        if (id < 0)
        {
            this.setVisible(false);
        } else
        {
            this.x = id == 0 ? 0 : parentList.get(parentList.indexOf(this) - 1).getNextX();
            this.setVisible(true);
            recalculateCoordinates();
        }
    }

    public void setPosition(int x)
    {
        this.x = x;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public double getNextX()
    {
        return coordinates[6];
    }

    public boolean isSelected()
    {
        return parentList.isSelected(this);
    }

    private void recalculateCoordinates()
    {
        coordinates = new double[]{x, 0, x + parentList.tabWidth, 0, x + parentList.tabHeight * SLOPE, -parentList.tabHeight, x + parentList.tabWidth - parentList.tabHeight * SLOPE, -parentList.tabHeight};
        outline = new double[]{coordinates[0], coordinates[1], coordinates[4], coordinates[5], coordinates[6], coordinates[7], coordinates[2], coordinates[3]};
    }

    @Override
    public void draw(GuiBase guiBase, int mouseX, int mouseY, int zLevel)
    {
        guiBase.drawTriangleStrip(isSelected() ? SELECTED_COLOURS : DESELECTED_COLOURS, coordinates);
        guiBase.drawLines(outline, OUTLINE_COLOURS);
        if (label != null)
        {
            guiBase.drawString(label, (int)(x + parentList.tabHeight * SLOPE + 2), -10, 0xFFFFFFFF);
        }
    }

    @Override
    public void drawMouseOver(GuiBase guiBase, int mouseX, int mouseY)
    {

    }

    @Override
    public boolean onKeyStroke(GuiBase guiBase, char character, int key)
    {
        return false;
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int button)
    {
        return !(mouseY > 0 || mouseY < -parentList.tabHeight || mouseX < x || mouseX > x + parentList.tabWidth || mouseX - x < -mouseY * SLOPE || mouseX - x - parentList.tabWidth > mouseY * SLOPE);
    }

    @Override
    public boolean isVisible()
    {
        return visible;
    }
}
