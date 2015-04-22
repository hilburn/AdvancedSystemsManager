package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.helpers.StevesEnum;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.network.DataReader;

import java.util.List;

public class MenuRFInput extends MenuRF
{
    public MenuRFInput(FlowComponent parent)
    {
        super(parent, StevesEnum.RF_PROVIDER);
    }

    public String getName()
    {
        return StevesEnum.TYPE_RF_INPUT.toString();
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (this.selectedInventories.isEmpty() && this.isVisible())
        {
            errors.add(StevesEnum.NO_RF_ERROR.toString());
        }
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
