package advancedsystemsmanager.flow.elements;

public abstract class CheckBox
{
    public int x, y;
    public String name;
    public int textWidth;

    public CheckBox(String name, int x, int y)
    {
        this.x = x;
        this.y = y;
        this.name = name;
        textWidth = Integer.MAX_VALUE;
    }

    public abstract boolean getValue();

    public abstract void setValue(boolean val);

    public abstract void onUpdate();


    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public String getName()
    {
        return name;
    }

    public boolean isVisible()
    {
        return true;
    }

    public int getTextWidth()
    {
        return textWidth;
    }

    public void setTextWidth(int textWidth)
    {
        this.textWidth = textWidth;
    }
}
