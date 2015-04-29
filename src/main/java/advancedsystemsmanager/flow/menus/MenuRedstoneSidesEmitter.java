package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.reference.Names;

public class MenuRedstoneSidesEmitter extends MenuRedstoneSides
{
    public MenuRedstoneSidesEmitter(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public void initRadioButtons()
    {
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, Names.STRONG_POWER));
        radioButtonList.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, Names.WEAK_POWER));
    }

    @Override
    public String getMessage()
    {
        return Names.REDSTONE_EMITTER_SIDES_INFO;
    }

    public boolean useStrongSignal()
    {
        return useFirstOption();
    }

    @Override
    public String getName()
    {
        return Names.REDSTONE_EMITTER_SIDES_MENU;
    }
}
