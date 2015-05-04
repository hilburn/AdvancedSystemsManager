package advancedsystemsmanager.flow.elements;

import advancedsystemsmanager.flow.menus.MenuContainer;

public class RadioButton
{
    public int x;
    public int y;
    public String text;

    public RadioButton(int id, String text)
    {
        this(MenuContainer.RADIO_BUTTON_MULTI_X, MenuContainer.RADIO_BUTTON_MULTI_Y + id * MenuContainer.RADIO_BUTTON_SPACING, text);
    }

    public RadioButton(int x, int y, String text)
    {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public String getText()
    {
        return text;
    }

    public boolean isVisible()
    {
        return true;
    }
}
