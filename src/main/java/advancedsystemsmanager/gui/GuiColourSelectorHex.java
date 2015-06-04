package advancedsystemsmanager.gui;

import advancedsystemsmanager.api.gui.IGuiElement;

public class GuiColourSelectorHex implements IGuiElement<GuiBase>
{
    private static final double[] points = new double[]{
            256,128,
            356,128,
            306, 41,
            206, 41,
            156, 128,
            206, 215,
            306, 215,
            356,128};
    private static final int[] colours = new int[]{128,128,128,255, 0, 0, 255, 255, 0 ,0, 255, 0, 0, 255, 255, 0, 0, 255, 255, 0, 255, 255, 0, 0};

    @Override
    public void draw(GuiBase gui, int mouseX, int mouseY, int zLevel)
    {
        gui.drawTriangleFan(colours, points);
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
        return false;
    }

    @Override
    public boolean isVisible()
    {
        return false;
    }
}
