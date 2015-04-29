package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.SystemTypeRegistry;

import java.util.List;

public class MenuNodes extends MenuContainer
{
    public MenuNodes(FlowComponent parent)
    {
        super(parent, SystemTypeRegistry.NODE);

        radioButtonsMulti.setSelectedOption(2);
    }

    @Override
    public String getName()
    {
        return Names.REDSTONE_NODE_MENU;
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty())
        {
            errors.add(Names.NO_NODE_ERROR);
        }
    }

    @Override
    public void initRadioButtons()
    {
        radioButtonsMulti.add(new RadioButtonInventory(0, Names.RUN_SHARED_ONCE));
        radioButtonsMulti.add(new RadioButtonInventory(1, Names.REQUIRE_ALL_TARGETS));
        radioButtonsMulti.add(new RadioButtonInventory(2, Names.REQUIRE_ONE_TARGET));
    }
}
