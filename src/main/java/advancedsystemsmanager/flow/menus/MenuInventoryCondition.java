package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.SystemTypeRegistry;

public class MenuInventoryCondition extends MenuContainer
{
    public MenuInventoryCondition(FlowComponent parent)
    {
        super(parent, SystemTypeRegistry.INVENTORY);
    }

    @Override
    public void initRadioButtons()
    {
        radioButtonsMulti.add(new RadioButton(0, Names.RUN_SHARED_ONCE));
        radioButtonsMulti.add(new RadioButton(1, Names.REQUIRE_ALL_TARGETS));
        radioButtonsMulti.add(new RadioButton(2, Names.REQUIRE_ONE_TARGET));
    }
}
