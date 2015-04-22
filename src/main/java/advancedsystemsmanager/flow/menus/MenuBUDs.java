package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.util.ConnectionBlockType;

import java.util.List;

public class MenuBUDs extends MenuContainer
{
    public MenuBUDs(FlowComponent parent)
    {
        super(parent, ConnectionBlockType.BUD);
    }

    @Override
    public String getName()
    {
        return Localization.DETECTOR_MENU.toString();
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() == ConnectionSet.BUD;
    }

    @Override
    public void initRadioButtons()
    {
        radioButtonsMulti.add(new MenuContainer.RadioButtonInventory(0, Localization.REQUIRE_ALL_TARGETS));
        radioButtonsMulti.add(new MenuContainer.RadioButtonInventory(1, Localization.REQUIRE_ONE_TARGET));
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty())
        {
            errors.add(Localization.NO_DETECTOR_ERROR.toString());
        }
    }
}
