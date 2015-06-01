package advancedsystemsmanager.gui;

import advancedsystemsmanager.api.gui.IGuiElement;

public class GuiColourSelectorHex implements IGuiElement<GuiBase>
{
    @Override
    public void draw(GuiBase guiBase, int mouseX, int mouseY, int zLevel)
    {

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
