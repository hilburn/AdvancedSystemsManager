package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.flow.FlowComponent;

public class MenuInventoryCondition extends MenuInventory
{
    public MenuInventoryCondition(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public void initRadioButtons()
    {
        radioButtonsMulti.add(new RadioButtonInventory(0, Localization.RUN_SHARED_ONCE));
        radioButtonsMulti.add(new RadioButtonInventory(1, Localization.REQUIRE_ALL_TARGETS));
        radioButtonsMulti.add(new RadioButtonInventory(2, Localization.REQUIRE_ONE_TARGET));
    }
}
