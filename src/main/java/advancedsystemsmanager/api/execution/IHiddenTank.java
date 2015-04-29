package advancedsystemsmanager.api.execution;

import advancedsystemsmanager.flow.execution.CommandExecutor;
import advancedsystemsmanager.flow.execution.LiquidBufferElement;
import advancedsystemsmanager.flow.execution.SlotInventoryHolder;
import advancedsystemsmanager.flow.menus.MenuStuff;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

public interface IHiddenTank
{
    IFluidHandler getTank();

    void addFluidsToBuffer(MenuStuff menuItem, SlotInventoryHolder tank, List<LiquidBufferElement> liquidBuffer, CommandExecutor commandExecutor);
}
