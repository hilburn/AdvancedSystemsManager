package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.reference.Names;

public class MenuInventoryCondition extends MenuInventory
{
    public MenuInventoryCondition(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public void initRadioButtons()
    {
        radioButtonsMulti.add(new RadioButtonInventory(0, Names.RUN_SHARED_ONCE));
        radioButtonsMulti.add(new RadioButtonInventory(1, Names.REQUIRE_ALL_TARGETS));
        radioButtonsMulti.add(new RadioButtonInventory(2, Names.REQUIRE_ONE_TARGET));
    }
}
