package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.helpers.StevesEnum;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.util.ConnectionBlockType;

import java.util.EnumSet;
import java.util.List;

public class MenuRFStorage extends MenuContainer
{
    public MenuRFStorage(FlowComponent parent)
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
    public EnumSet<ConnectionBlockType> getValidTypes()
    {
        return EnumSet.of(StevesEnum.RF_PROVIDER, StevesEnum.RF_RECEIVER);
    }

    public void initRadioButtons()
    {
    }
}
