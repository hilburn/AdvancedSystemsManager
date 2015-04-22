package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.helpers.StevesEnum;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.network.DataReader;

import java.util.List;

public class MenuRFOutput extends MenuRF
{
    public MenuRFOutput(FlowComponent parent)
    {
        super(parent, StevesEnum.RF_RECEIVER);
    }

    public String getName()
    {
        return StevesEnum.TYPE_RF_OUTPUT.toString();
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
