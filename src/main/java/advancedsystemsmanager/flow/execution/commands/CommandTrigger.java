package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBufferProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.helpers.StevesEnum;
import advancedsystemsmanager.registry.ConnectionSet;

import java.util.List;

public class CommandTrigger extends CommandBase
{
    public CommandTrigger()
    {
        super(TRIGGER, Localization.TRIGGER_SHORT.toString(), Localization.TRIGGER_LONG.toString(), CommandType.TRIGGER, ConnectionSet.CONTINUOUSLY, ConnectionSet.REDSTONE, ConnectionSet.BUD, StevesEnum.DELAYED);
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
