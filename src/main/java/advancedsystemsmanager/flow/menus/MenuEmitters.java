package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.SystemTypeRegistry;

import java.util.List;

public class MenuEmitters extends MenuContainer
{
    public MenuEmitters(FlowComponent parent)
    {
        super(parent, SystemTypeRegistry.EMITTER);
    }

    @Override
    public String getName()
    {
        return Names.EMITTER_MENU;
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty())
        {
            errors.add(Names.NO_EMITTER_ERROR);
        }
    }

    @Override
    public void initRadioButtons()
    {

    }
}
