package advancedfactorymanager.components;


import advancedfactorymanager.helpers.Localization;
import advancedfactorymanager.util.ConnectionBlockType;

import java.util.List;

public class ComponentMenuInventory extends ComponentMenuContainer
{
    public ComponentMenuInventory(FlowComponent parent)
    {
        super(parent, ConnectionBlockType.INVENTORY);
    }

    @Override
    public String getName()
    {
        return Localization.INVENTORY_MENU.toString();
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty())
        {
            errors.add(Localization.NO_INVENTORY_ERROR.toString());
        }
    }
}
