package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.util.ConnectionBlockType;

import java.util.List;

public class MenuEmitters extends MenuContainer
{
    public MenuEmitters(FlowComponent parent)
    {
        super(parent, ConnectionBlockType.EMITTER);
    }

    @Override
    public String getName()
    {
        return Localization.EMITTER_MENU.toString();
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty())
        {
            errors.add(Localization.NO_EMITTER_ERROR.toString());
        }
    }

    @Override
    public void initRadioButtons()
    {

    }
}
