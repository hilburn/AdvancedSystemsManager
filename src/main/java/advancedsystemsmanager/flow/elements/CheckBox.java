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
    public boolean writeData(ASMPacket packet)
    {
        packet.writeBoolean(getValue());
        return true;
    }

    public boolean getValue()
    {
        return checked;
    }

    public void setValue(boolean val)
    {
        checked = val;
    }

    @Override
    public boolean readData(ASMPacket packet)
    {
        setValue(packet.readBoolean());
        return false;
    }
}
