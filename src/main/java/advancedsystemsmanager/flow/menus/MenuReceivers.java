package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.util.ConnectionBlockType;

import java.util.List;

public class MenuReceivers extends MenuContainer
{

    public MenuReceivers(FlowComponent parent)
    {
        super(parent, ConnectionBlockType.RECEIVER);

        radioButtonsMulti.setSelectedOption(2);
    }

    @Override
    public String getName()
    {
        return Localization.RECEIVERS_MENU.toString();
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty() && isVisible())
        {
            errors.add(Localization.NO_RECEIVER_ERROR.toString());
        }
    }

    @Override
    public void initRadioButtons()
    {
        radioButtonsMulti.add(new MenuContainer.RadioButtonInventory(0, Localization.RUN_SHARED_ONCE));
        radioButtonsMulti.add(new MenuContainer.RadioButtonInventory(1, Localization.REQUIRE_ALL_TARGETS));
        radioButtonsMulti.add(new MenuContainer.RadioButtonInventory(2, Localization.REQUIRE_ONE_TARGET));
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() == ConnectionSet.REDSTONE;
    }
}
