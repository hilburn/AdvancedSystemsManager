package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.flow.FlowComponent;

public class MenuRedstoneSidesEmitter extends MenuRedstoneSides
{
    public MenuRedstoneSidesEmitter(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public void initRadioButtons()
    {
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, Localization.STRONG_POWER));
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, Localization.WEAK_POWER));
    }

    @Override
    public String getMessage()
    {
        return Localization.REDSTONE_EMITTER_SIDES_INFO.toString();
    }

    public boolean useStrongSignal()
    {
        return useFirstOption();
    }

    @Override
    public String getName()
    {
        return Localization.REDSTONE_EMITTER_SIDES_MENU.toString();
    }
}
