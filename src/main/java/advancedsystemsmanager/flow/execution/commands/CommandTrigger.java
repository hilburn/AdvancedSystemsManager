package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBufferProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;

import java.util.List;

public class CommandTrigger extends CommandBase
{
    public CommandTrigger()
    {
        super(TRIGGER, Names.TRIGGER, CommandType.TRIGGER, ConnectionSet.CONTINUOUSLY, ConnectionSet.REDSTONE, ConnectionSet.BUD, ConnectionSet.DELAYED);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuReceivers(component));
        menus.add(new MenuBUDs(component));
        menus.add(new MenuInterval(component));
        menus.add(new MenuRedstoneSidesTrigger(component));
        menus.add(new MenuRedstoneStrength(component));
        menus.add(new MenuUpdateBlock(component));
        menus.add(new MenuDelayed(component));
    }

    @Override
    public void execute(FlowComponent command, int connectionId, IBufferProvider bufferProvider)
    {

    }
}
