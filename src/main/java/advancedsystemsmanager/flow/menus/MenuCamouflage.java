package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.SystemTypeRegistry;

import java.util.List;


public class MenuCamouflage extends MenuContainer
{
    public MenuCamouflage(FlowComponent parent)
    {
        super(parent, SystemTypeRegistry.CAMOUFLAGE);
    }

    @Override
    public String getName()
    {
        return Names.CAMOUFLAGE_BLOCK_MENU;
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty())
        {
            errors.add(Names.NO_CAMOUFLAGE_BLOCKS_ERROR);
        }
    }

    @Override
    public void initRadioButtons()
    {
        //nothing here
    }
}
