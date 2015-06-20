package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.reference.Names;

public class MenuRedstoneStrengthNodes extends MenuRedstoneStrength
{
    public MenuRedstoneStrengthNodes(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public String getName()
    {
        return Names.REDSTONE_STRENGTH_MENU_CONDITION;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }
}
