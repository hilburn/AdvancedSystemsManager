package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.helpers.StevesEnum;
import advancedsystemsmanager.reference.Names;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuRFStorage extends MenuContainer
{
    public MenuRFStorage(FlowComponent parent)
    {
        super(parent, StevesEnum.RF_CONNECTION);
    }

    public String getName()
    {
        return Names.TYPE_RF;
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (this.selectedInventories.isEmpty() && this.isVisible())
        {
            errors.add(Names.NO_RF_ERROR);
        }
    }

    public void initRadioButtons()
    {
    }

    @Override
    public Set<ISystemType> getValidTypes()
    {
        return new HashSet<ISystemType>(Arrays.asList(StevesEnum.RF_PROVIDER, StevesEnum.RF_RECEIVER));
    }
}
