package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.flow.FlowComponent;

public class MenuRedstoneSidesNodes extends MenuRedstoneSidesTrigger
{

    public MenuRedstoneSidesNodes(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public String getName()
    {
        return Localization.REDSTONE_SIDES_MENU.toString();
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }
}
