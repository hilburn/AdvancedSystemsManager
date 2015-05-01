package advancedsystemsmanager.flow.elements;


public class WideNumberBox extends TextBoxNumber
{
    public WideNumberBox(int x, int y, int length)
    {
        super(x, y, length, true);
    }

    @Override
    public int getMaxNumber()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    public int getWidth()
    {
        return 64;
    }

}
