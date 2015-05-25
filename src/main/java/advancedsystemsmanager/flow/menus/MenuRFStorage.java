package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.flow.FlowComponent;

import java.util.HashSet;
import java.util.Set;

public class MenuRFStorage extends MenuContainer
{
    public MenuRFStorage(FlowComponent parent)
    {
        super(parent, null);
    }

    @Override
    public Set<ISystemType> getValidTypes()
    {
        return new HashSet<ISystemType>();
//        return new HashSet<ISystemType>(Arrays.asList(StevesEnum.RF_PROVIDER, StevesEnum.RF_RECEIVER));
    }
}
