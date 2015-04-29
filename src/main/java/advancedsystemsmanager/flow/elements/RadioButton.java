package advancedsystemsmanager.flow.elements;

public class RadioButton
{
    public int x;
    public int y;
    public String text;

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
