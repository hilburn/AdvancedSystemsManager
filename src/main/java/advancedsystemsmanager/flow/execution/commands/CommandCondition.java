package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.IConditionStuffMenu;
import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class CommandCondition<Type, Menu extends MenuTarget> extends CommandBase<Type>
{
    public CommandCondition(int id, String name)
    {
        super(id, name, CommandType.COMMAND_CONTROL, ConnectionSet.STANDARD_CONDITION);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Connection> getActiveChildren(FlowComponent command, int connectionId)
    {
        MenuStuff<Type> menuItem = (MenuStuff<Type>)command.getMenus().get(2);
        List<Setting<Type>> settings = getValidSettings(menuItem.getSettings());
        Set<Setting<Type>> found = new HashSet<Setting<Type>>();
        searchForStuff(getContainers(command.getManager(), (MenuContainer)command.getMenus().get(0)), settings, (Menu)command.getMenus().get(1), found);
        boolean findAll = ((IConditionStuffMenu)menuItem).requiresAll();
        boolean success = findAll;
        for (Setting<Type> setting : found)
        {
            if (findAll && setting.getAmountLeft() > 0)
            {
                success = false;
                break;
            } else if (!findAll && setting.getAmountLeft() < 1)
            {
                success = true;
                break;
            }
        }
        List<Connection> active = new ArrayList<Connection>();
        for (int i = 0; i < command.getConnectionSet().getConnections().length; ++i)
        {
            Connection connection = command.getConnection(i);
            ConnectionOption option = command.getConnectionSet().getConnections()[i];
            if (connection != null && !option.isInput() && (option == ConnectionOption.CONDITION_TRUE) == success)
            {
                active.add(connection);
                break;
            }
        }
        return active;
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
