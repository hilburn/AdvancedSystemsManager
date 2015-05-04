package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.helpers.StevesEnum;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.reference.Names;

public class MenuRFInput extends MenuRF
{
    public MenuRFInput(FlowComponent parent)
    {
        super(parent, StevesEnum.RF_PROVIDER);
    }

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        super.readNetworkComponent(dr);
        updateConnectedNodes();
    }
}
