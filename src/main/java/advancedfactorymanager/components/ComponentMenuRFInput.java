package advancedfactorymanager.components;

import advancedfactorymanager.helpers.StevesEnum;
import advancedfactorymanager.network.DataReader;

import java.util.List;

public class ComponentMenuRFInput extends ComponentMenuRF
{
    public ComponentMenuRFInput(FlowComponent parent)
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

    protected void initRadioButtons()
    {
    }
}
