package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;

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
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, Names.REQUIRES_ALL));
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, Names.IF_ANY));
    }

    @Override
    public String getMessage()
    {
        if (isBUD())
        {
            return Names.UPDATE_SIDES_INFO;
        } else
        {
            return Names.REDSTONE_SIDES_INFO;
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
        return isBUD() ? Names.UPDATE_SIDES_MENU: Names.REDSTONE_SIDES_MENU_TRIGGER;
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
            errors.add(Names.NO_SIDES_ERROR);
        }
    }
}
