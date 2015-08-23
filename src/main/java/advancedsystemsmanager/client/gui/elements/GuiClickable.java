package advancedsystemsmanager.client.gui.elements;

import advancedsystemsmanager.api.gui.IGuiElement;
import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.helpers.LocalizationHelper;

public abstract class GuiClickable implements IGuiElement
{
    private String mouseoverText;
    protected int x, y, w, h;

    public GuiClickable()
    {
        this(null);
    }

    public GuiClickable(String mouseover)
    {
        mouseoverText = mouseover;
    }

    @Override
    public boolean drawMouseOver(GuiBase gui, int mouseX, int mouseY)
    {
        if (hasMouseoverText() && isInBounds(mouseX, mouseY))
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
        if (isInBounds(mouseX, mouseY))
        {
            click();
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    protected boolean isInBounds(int mouseX, int mouseY)
    {
        return !(mouseY < y || mouseY > y + h || mouseX < x || mouseX > x + w);
    }

    protected abstract void click();

    private boolean hasMouseoverText()
    {
        return mouseoverText != null;
    }

    public String getMouseoverText()
    {
        return LocalizationHelper.translate(mouseoverText);
    }
}
