package advancedsystemsmanager.api.tileentities;

import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.flow.menus.MenuLiquid;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

public interface IInternalTank
{
    IFluidHandler getTank();

    List<IBufferElement<Fluid>> getSubElements(int id, MenuLiquid menuLiquid);
}
