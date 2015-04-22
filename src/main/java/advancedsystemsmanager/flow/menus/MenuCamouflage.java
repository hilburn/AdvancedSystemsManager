package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.util.ConnectionBlockType;

import java.util.List;


public class MenuCamouflage extends MenuContainer
{
    public MenuCamouflage(FlowComponent parent)
    {
        super(parent, ConnectionBlockType.CAMOUFLAGE);
    }

    @Override
    public String getName()
    {
        return Localization.CAMOUFLAGE_BLOCK_MENU.toString();
    }

    @Override
    public void initRadioButtons()
    {
        //nothing here
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty())
        {
            errors.add(Localization.NO_CAMOUFLAGE_BLOCKS_ERROR.toString());
        }
    }
}
