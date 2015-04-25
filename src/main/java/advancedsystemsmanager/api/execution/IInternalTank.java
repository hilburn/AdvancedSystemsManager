package advancedsystemsmanager.api.execution;

import advancedsystemsmanager.flow.menus.MenuLiquid;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

public interface IInternalTank
{
    IFluidHandler getTank();

    List<IBufferElement<Fluid>> getSubElements(int id, MenuLiquid menuLiquid);
}
