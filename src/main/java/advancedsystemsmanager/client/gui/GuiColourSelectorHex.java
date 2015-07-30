package advancedsystemsmanager.client.gui;

import advancedsystemsmanager.api.gui.IGuiElement;

public class GuiColourSelectorHex implements IGuiElement<GuiBase>
{
    private static final double[] points = new double[]{
            256, 128,
            356, 128,
            306, 41,
            206, 41,
            156, 128,
            206, 215,
            306, 215,
            356, 128};
    private static final int[] colours = new int[]{128, 128, 128, 255, 0, 0, 255, 255, 0, 0, 255, 0, 0, 255, 255, 0, 0, 255, 255, 0, 255, 255, 0, 0};
    private static final int[] RED = new int[]{255, 0, 0};
    private static final int[] GREEN = new int[]{0, 255, 0};
    private static final int[] BLUE = new int[]{0, 0, 255};
    private static final int[] GREY = new int[]{128, 128, 128};
    private static float R = 100f;
    private int[] hexagonColour = new int[3];

    @Override
    public void draw(GuiBase guiBase, int mouseX, int mouseY, int zLevel)
    {
        guiBase.drawTriangleFan(colours, points);
        guiBase.drawRainbowRectangle(400, 20, 20, 216, GuiColourSelector.WHITE, hexagonColour, GuiColourSelector.BLACK);
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
        mouseY -= 128;
        mouseX -= 256;
        if (isInHex(mouseX, mouseY))
        {
            hexClick(mouseX, mouseY);
            return true;
        }
        return false;
    }

    private boolean isInHex(int x, int y)
    {
        x /= R;
        y /= R;
        float l2 = x * x + y * y;
        if (l2 > 1.0f) return false;
        if (l2 < 0.75f) return true; // (sqrt(3)/2)^2 = 3/4

        // Check against borders
        float py = y * 1.15470053838f; // 2/sqrt(3)
        if (py > 1.0f || py < -1.0f) return false;

        float px = 0.5f * py + x;
        return !(px > 1.0f || px < -1.0f) && !(py - px > 1.0f || py - px < -1.0f);

    }

    private void hexClick(int mouseX, int mouseY)
    {

        double theta = -Math.atan2(mouseY, mouseX);
        double hex = Math.PI * 2 / 3;
        int[] colour1;
        int[] colour2;
        if (theta < 0) theta += Math.PI * 2;
        double alpha = theta;
        while (alpha > hex) alpha -= hex;
        alpha *= 0.75;
        switch ((int)(theta / hex))
        {
            case 0:
                colour1 = RED;
                colour2 = GREEN;
                break;
            case 1:
                colour1 = GREEN;
                colour2 = BLUE;
                break;
            default:
                colour1 = BLUE;
                colour2 = RED;
                break;
        }
        double x = Math.sqrt(2 * (mouseY * mouseY + mouseX * mouseX));
        double cos = x * Math.cos(alpha) / R / Math.sqrt(2);
        double sin = x * Math.sin(alpha) / R / Math.sqrt(2);
        cos = 1 / (cos - 1);
        sin = 1 / (sin - 1) + 1;
        if (cos < 1 && sin < 1)
        {
            int r = (int)(colour1[0] * cos + colour2[0] * sin) - GREY[0];
            int g = (int)(colour1[1] * cos + colour2[1] * sin) - GREY[1];
            int b = (int)(colour1[2] * cos + colour2[2] * sin) - GREY[2];
            hexagonColour = new int[]{r, g, b};
        }
    }

    @Override
    public boolean isVisible()
    {
        return false;
    }
}
