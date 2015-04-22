package advancedsystemsmanager.api;

import advancedsystemsmanager.flow.execution.CommandExecutorRF;
import advancedsystemsmanager.flow.menus.MenuStuff;
import advancedsystemsmanager.flow.execution.LiquidBufferElement;
import advancedsystemsmanager.flow.execution.SlotInventoryHolder;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

public interface IHiddenTank
{
    IFluidHandler getTank();

    void addFluidsToBuffer(MenuStuff menuItem, SlotInventoryHolder tank, List<LiquidBufferElement> liquidBuffer, CommandExecutorRF commandExecutorRF);
}
