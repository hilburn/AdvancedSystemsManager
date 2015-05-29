package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.Executor;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.flow.menus.MenuSignText;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.tileentities.TileEntitySignUpdater;
import advancedsystemsmanager.util.SystemCoord;

import java.util.List;

public class CommandSign extends CommandBase
{
    public CommandSign()
    {
        super(SIGN, Names.SIGN, CommandType.MISC, ConnectionSet.STANDARD);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuContainer(component, SystemTypeRegistry.SIGN));
        menus.add(new MenuSignText(component));
    }

    @Override
    public void execute(FlowComponent command, int connectionId, Executor executor)
    {
        List<SystemCoord> signBlocks = getContainers(command.getManager(), (MenuContainer)command.getMenus().get(0));
        if (signBlocks != null)
        {
            for (SystemCoord coord : signBlocks)
            {
                ((TileEntitySignUpdater)coord.tileEntity).updateSign((MenuSignText)command.getMenus().get(1));
            }
        }
    }
}
