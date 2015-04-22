package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.flow.FlowComponent;

import java.util.List;


public class MenuCamouflageSides extends MenuRedstoneSides
{
    public MenuCamouflageSides(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public void initRadioButtons()
    {
        //no options
    }

    @Override
    public String getMessage()
    {
        return Localization.CAMOUFLAGE_SIDES_INFO.toString();
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return Localization.CAMOUFLAGE_SIDES_NAME.toString();
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
