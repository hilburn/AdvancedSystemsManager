package advancedsystemsmanager.client.gui.elements;

import advancedsystemsmanager.api.gui.IGuiElement;
import advancedsystemsmanager.client.gui.GuiBase;

public abstract class GuiButton implements IGuiElement
{
    private String mouseoverText;

    @Override
    public boolean drawMouseOver(GuiBase gui, int mouseX, int mouseY)
    {
        if (hasMouseoverText())
        {
            gui.drawMouseOver(getMouseoverText(), mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyStroke(GuiBase gui, char character, int key)
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

    private boolean hasMouseoverText()
    {
        return mouseoverText != null;
    }

    public String getMouseoverText()
    {
        return mouseoverText;
    }
}
