package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.SystemTypeRegistry;

import java.util.List;

public class MenuSigns extends MenuContainer
{
    public MenuSigns(FlowComponent parent)
    {
        super(parent, SystemTypeRegistry.SIGN);
    }

    @Override
    public String getName()
    {
        return Names.SIGNS;
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty() && isVisible())
        {
            errors.add(Names.NO_SIGNS_ERROR);
        }
    }

    @Override
    public void initRadioButtons()
    {

    }
}
