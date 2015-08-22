package advancedsystemsmanager.client.gui.elements;

import advancedsystemsmanager.api.gui.IGuiListElement;
import advancedsystemsmanager.client.gui.GuiBase;

public class GuiListElement implements IGuiListElement
{
    int width, height, x, y;

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public void setX(int x)
    {
        this.x = x;
    }

    @Override
    public int getX()
    {
        return x;
    }

    @Override
    public void setY(int y)
    {
        this.y = y;
    }

    @Override
    public int getY()
    {
        return y;
    }

    @Override
    public void draw(GuiBase guiBase, int mouseX, int mouseY, int zLevel)
    {

    }

    @Override
    public boolean drawMouseOver(GuiBase guiBase, int mouseX, int mouseY)
    {
        return false;
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
