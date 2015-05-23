package advancedsystemsmanager.flow.elements;

import advancedsystemsmanager.api.network.IPacketProvider;
import advancedsystemsmanager.network.ASMPacket;

public abstract class CheckBox extends UpdateElement
{
    public int x, y;
    public String name;
    public int textWidth;
    public boolean checked;

    public CheckBox(IPacketProvider packetProvider, String name, int x, int y)
    {
        super(packetProvider);
        this.x = x;
        this.y = y;
        this.name = name;
        textWidth = Integer.MAX_VALUE;
    }

    public void setValue(boolean val)
    {
        checked = val;
    }

    public boolean getValue()
    {
        return checked;
    }

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

    @Override
    public void writeData(ASMPacket packet)
    {
        packet.writeBoolean(getValue());
    }

    @Override
    public void readData(ASMPacket packet)
    {
        setValue(packet.readBoolean());
    }
}
