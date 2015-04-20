package advancedfactorymanager.components;

import advancedfactorymanager.helpers.StevesEnum;
import advancedfactorymanager.network.DataReader;

import java.util.List;

public class ComponentMenuRFOutput extends ComponentMenuRF
{
    public ComponentMenuRFOutput(FlowComponent parent)
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

    protected void initRadioButtons()
    {
    }
}
