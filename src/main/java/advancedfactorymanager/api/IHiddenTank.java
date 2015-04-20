package advancedfactorymanager.api;

import net.minecraftforge.fluids.IFluidHandler;
import advancedfactorymanager.components.CommandExecutorRF;
import advancedfactorymanager.components.ComponentMenuStuff;
import advancedfactorymanager.components.LiquidBufferElement;
import advancedfactorymanager.components.SlotInventoryHolder;

import java.util.List;

public interface IHiddenTank
{
    IFluidHandler getTank();

    void addFluidsToBuffer(ComponentMenuStuff menuItem, SlotInventoryHolder tank, List<LiquidBufferElement> liquidBuffer, CommandExecutorRF commandExecutorRF);
}
