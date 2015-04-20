package advancedfactorymanager.components;


import advancedfactorymanager.helpers.Localization;

public class RadioButton
{
    public int x;
    public int y;
    public Localization text;

    public RadioButton(int x, int y, Localization text)
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
        return text.toString();
    }

    public boolean isVisible()
    {
        return true;
    }
}
