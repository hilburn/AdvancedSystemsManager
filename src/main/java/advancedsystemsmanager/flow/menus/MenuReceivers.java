package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.registry.SystemTypeRegistry;

import java.util.List;

public class MenuReceivers extends MenuContainer
{

    public MenuReceivers(FlowComponent parent)
    {
        super(parent, SystemTypeRegistry.RECEIVER);

        radioButtonsMulti.setSelectedOption(2);
    }

    @Override
    public String getName()
    {
        return Names.RECEIVERS_MENU;
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty() && isVisible())
        {
            errors.add(Names.NO_RECEIVER_ERROR.toString());
        }
    }

    @Override
    public void initRadioButtons()
    {
        radioButtonsMulti.add(new MenuContainer.RadioButtonInventory(0, Names.RUN_SHARED_ONCE));
        radioButtonsMulti.add(new MenuContainer.RadioButtonInventory(1, Names.REQUIRE_ALL_TARGETS));
        radioButtonsMulti.add(new MenuContainer.RadioButtonInventory(2, Names.REQUIRE_ONE_TARGET));
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() == ConnectionSet.REDSTONE;
    }
}
