package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.flow.FlowComponent;

public class MenuRedstoneStrengthNodes extends MenuRedstoneStrength
{
    public MenuRedstoneStrengthNodes(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public String getName()
    {
        return Localization.REDSTONE_STRENGTH_MENU_CONDITION.toString();
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }
}
