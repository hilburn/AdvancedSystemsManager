package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.helpers.StevesEnum;
import advancedsystemsmanager.network.ASMPacket;
import io.netty.buffer.ByteBuf;

public class MenuRFInput extends MenuRF
{
    public MenuRFInput(FlowComponent parent)
    {
        super(parent, StevesEnum.RF_PROVIDER);
    }


//    @Override
//    public void readData(ASMPacket packet)
//    {
//        super.readData(packet);
//        updateConnectedNodes();
//    }

}
