package advancedsystemsmanager.flow.elements;


import advancedsystemsmanager.api.network.IPacketProvider;

public class WideNumberBox extends TextBoxNumber
{
    public WideNumberBox(IPacketProvider packetProvider, int x, int y, int length)
    {
        super(packetProvider, x, y, length, true);
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

    @Override
    public int getMaxNumber()
    {
        return Integer.MAX_VALUE;
    }

}
