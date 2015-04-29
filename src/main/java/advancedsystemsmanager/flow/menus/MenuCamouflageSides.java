package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.reference.Names;

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
        return Names.CAMOUFLAGE_SIDES_INFO;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return Names.CAMOUFLAGE_SIDES_NAME;
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
