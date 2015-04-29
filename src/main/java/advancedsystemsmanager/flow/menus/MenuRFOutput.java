package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.helpers.StevesEnum;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.reference.Names;

public class MenuRFOutput extends MenuRF
{
    public MenuRFOutput(FlowComponent parent)
    {
        super(parent, StevesEnum.RF_RECEIVER);
    }

    public String getName()
    {
        return Names.TYPE_RF_OUTPUT;
    }

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        super.readNetworkComponent(dr);
        updateConnectedNodes();
    }

    public void initRadioButtons()
    {
    }
}
