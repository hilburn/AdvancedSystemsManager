package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.api.IConditionStuffMenu;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.flow.FlowComponent;

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
        radioButtons.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, Localization.REQUIRES_ALL));
        radioButtons.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, Localization.IF_ANY));
    }

    public boolean requiresAll()
    {
        return isFirstRadioButtonSelected();
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

        errors.add(Localization.NO_CONDITION_ERROR.toString());
    }
}
