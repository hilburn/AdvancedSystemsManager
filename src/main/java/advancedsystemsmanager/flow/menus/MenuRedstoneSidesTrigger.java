package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.flow.FlowComponent;

import java.util.List;

public class MenuRedstoneSidesTrigger extends MenuRedstoneSides
{
    public MenuRedstoneSidesTrigger(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public void initRadioButtons()
    {
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, Localization.REQUIRES_ALL));
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, Localization.IF_ANY));
    }

    @Override
    public String getMessage()
    {
        if (isBUD())
        {
            return Localization.UPDATE_SIDES_INFO.toString();
        } else
        {
            return Localization.REDSTONE_SIDES_INFO.toString();
        }
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() == ConnectionSet.REDSTONE || isBUD();
    }

    public boolean requireAll()
    {
        return useFirstOption();
    }

    @Override
    public String getName()
    {
        return isBUD() ? Localization.UPDATE_SIDES_MENU.toString() : Localization.REDSTONE_SIDES_MENU_TRIGGER.toString();
    }

    public boolean isBUD()
    {
        return getParent().getConnectionSet() == ConnectionSet.BUD;
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (isVisible() && selection == 0)
        {
            errors.add(Localization.NO_SIDES_ERROR.toString());
        }
    }
}
