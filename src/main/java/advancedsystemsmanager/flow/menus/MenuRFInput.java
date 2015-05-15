package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.helpers.StevesEnum;
import io.netty.buffer.ByteBuf;

public class MenuRFInput extends MenuRF
{
    public MenuRFInput(FlowComponent parent)
    {
        super(parent, StevesEnum.RF_PROVIDER);
    }

    @Override
    public void readNetworkComponent(ByteBuf buf)
    {
        super.readNetworkComponent(buf);
        updateConnectedNodes();
    }
}
