package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.reference.Names;

public class MenuRedstoneSidesNodes extends MenuRedstoneSidesTrigger
{

    public MenuRedstoneSidesNodes(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return Names.REDSTONE_SIDES_MENU;
    }
}
