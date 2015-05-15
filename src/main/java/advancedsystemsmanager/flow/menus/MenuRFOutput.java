package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.helpers.StevesEnum;
import io.netty.buffer.ByteBuf;

public class MenuRFOutput extends MenuRF
{
    public MenuRFOutput(FlowComponent parent)
    {
        super(parent, StevesEnum.RF_RECEIVER);
    }

    @Override
    public void readData(ByteBuf dr)
    {
        super.readData(dr);
        updateConnectedNodes();
    }
}
