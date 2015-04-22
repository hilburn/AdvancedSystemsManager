package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.util.ConnectionBlockType;

import java.util.List;

public class MenuSigns extends MenuContainer
{
    public MenuSigns(FlowComponent parent)
    {
        super(parent, ConnectionBlockType.SIGN);
    }

    @Override
    public String getName()
    {
        return Localization.SIGNS.toString();
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty() && isVisible())
        {
            errors.add(Localization.NO_SIGNS_ERROR.toString());
        }
    }

    @Override
    public void initRadioButtons()
    {

    }
}
