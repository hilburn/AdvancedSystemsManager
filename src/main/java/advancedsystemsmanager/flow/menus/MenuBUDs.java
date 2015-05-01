package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.registry.SystemTypeRegistry;

import java.util.List;

public class MenuBUDs extends MenuContainer
{
    public MenuBUDs(FlowComponent parent)
    {
        super(parent, SystemTypeRegistry.BUD);
    }

    @Override
    public String getName()
    {
        return Names.DETECTOR_MENU;
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty())
        {
            errors.add(Names.NO_DETECTOR_ERROR);
        }
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() == ConnectionSet.BUD;
    }

    @Override
    public void initRadioButtons()
    {
        radioButtonsMulti.add(new MenuContainer.RadioButtonInventory(0, Names.REQUIRE_ALL_TARGETS));
        radioButtonsMulti.add(new MenuContainer.RadioButtonInventory(1, Names.REQUIRE_ONE_TARGET));
    }
}
