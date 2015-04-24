package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferProvider;
import advancedsystemsmanager.api.execution.IBufferSubElement;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.util.ConnectionBlock;

import java.util.List;

public class CommandFluidInput extends CommandInput
{
    public CommandFluidInput()
    {
        super(FLUID_INPUT, Localization.LIQUID_INPUT_SHORT.toString(), Localization.LIQUID_INPUT_LONG.toString(), IBuffer.FLUID);
    }

    @Override
    public List<Menu> getMenus(FlowComponent component)
    {
        List<Menu> menus = component.getMenus();
        menus.add(new MenuTank(component));
        menus.add(new MenuTargetTank(component));
        menus.add(new MenuLiquid(component));
        return menus;
    }

    @Override
    public void execute(FlowComponent command, int connectionId, IBufferProvider bufferProvider)
    {

    }

    @Override
    protected IBuffer getNewBuffer()
    {
        return null;
    }

    @Override
    protected List<IBufferSubElement> getBufferSubElements(List<ConnectionBlock> blocks, List<Menu> menus)
    {
        return null;
    }
}
