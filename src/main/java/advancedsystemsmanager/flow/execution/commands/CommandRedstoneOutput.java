package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.Executor;

import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.tileentities.TileEntityEmitter;
import advancedsystemsmanager.util.SystemCoord;

import java.util.List;

public class CommandRedstoneOutput extends CommandBase
{
    public CommandRedstoneOutput()
    {
        super(REDSTONE_OUTPUT, Names.REDSTONE_EMITTER, CommandType.OUTPUT, ConnectionSet.STANDARD);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuContainer(component, SystemTypeRegistry.EMITTER));
        menus.add(new MenuRedstoneSidesEmitter(component));
        menus.add(new MenuRedstoneOutput(component));
        menus.add(new MenuPulse(component));
    }

    @Override
    public void execute(FlowComponent command, int connectionId, Executor executor)
    {
        List<SystemCoord> coords = getContainers(command.getManager(), (MenuContainer)command.menus.get(0));
        if (coords != null)
        {
            for (SystemCoord coord : coords)
            {
                ((TileEntityEmitter)coord.tileEntity).updateState((MenuRedstoneSidesEmitter)command.getMenus().get(1), (MenuRedstoneOutput)command.getMenus().get(2), (MenuPulse)command.getMenus().get(3));
            }
        }
    }
}
