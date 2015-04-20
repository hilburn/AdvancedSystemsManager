package advancedfactorymanager.components;

import advancedfactorymanager.blocks.ConnectionBlockType;
import advancedfactorymanager.helpers.StevesEnum;

import java.util.EnumSet;
import java.util.List;

public class ComponentMenuRFStorage extends ComponentMenuContainer
{
    public ComponentMenuRFStorage(FlowComponent parent)
    {
        super(parent, StevesEnum.RF_CONNECTION);
    }

    public String getName()
    {
        return StevesEnum.TYPE_RF.toString();
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
    protected EnumSet<ConnectionBlockType> getValidTypes()
    {
        return EnumSet.of(StevesEnum.RF_PROVIDER, StevesEnum.RF_RECEIVER);
    }

    protected void initRadioButtons()
    {
    }
}
