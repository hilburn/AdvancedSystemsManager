package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.api.IConditionStuffMenu;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.reference.Names;

import java.util.List;

public class MenuLiquidCondition extends MenuLiquid implements IConditionStuffMenu
{
    public MenuLiquidCondition(FlowComponent parent)
    {
        super(parent);
    }


    @Override
    public void initRadioButtons()
    {
        radioButtons.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, Names.REQUIRES_ALL));
        radioButtons.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, Names.IF_ANY));
    }

    @Override
    public void addErrors(List<String> errors)
    {
        for (Setting setting : getSettings())
        {
            if (setting.isValid())
            {
                return;
            }
        }

        errors.add(Names.NO_CONDITION_ERROR);
    }

    public boolean requiresAll()
    {
        return isFirstRadioButtonSelected();
    }
}
