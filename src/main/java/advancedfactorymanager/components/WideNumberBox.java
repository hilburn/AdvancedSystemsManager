package advancedfactorymanager.components;


public class WideNumberBox extends TextBoxNumber
{
    public WideNumberBox(int x, int y, int length)
    {
        super(x, y, length, true);
    }


    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public int getMaxNumber()
    {
        return Integer.MAX_VALUE;
    }

    public int getWidth()
    {
        return 64;
    }

}
