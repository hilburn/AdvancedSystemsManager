package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.flow.menus.MenuTarget;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.util.SystemCoord;

import java.util.List;
import java.util.Set;

public abstract class CommandCondition<Type, Menu extends MenuTarget> extends CommandBase<Type>
{
    public CommandCondition(int id, String name)
    {
        super(id, name, CommandType.COMMAND_CONTROL, ConnectionSet.STANDARD_CONDITION);
    }

    public void searchForStuff(List<SystemCoord> blocks, List<Setting<Type>> settings, Menu targets, Set<Setting<Type>> found)
    {
        for (SystemCoord block : blocks)
        {
            if (settings.isEmpty()) break;
            searchForStuff(block, settings, targets, found);
        }
    }

    public abstract void searchForStuff(SystemCoord block, List<Setting<Type>> settings, Menu targets, Set<Setting<Type>> found);
}
